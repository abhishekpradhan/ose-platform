package lbjse.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lbjse.rank.ResultItem;
import lbjse.trainer.TrainingSession;
import lbjse.utils.CommonUtils;
import lbjse.utils.OrderPair;
import lbjse.utils.Sorter;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;
import ose.index.IndexFieldConstant;
import ose.learning.DoubleFeatureValue;
import ose.processor.cascader.CompositeFeatureIterator;
import ose.processor.cascader.DocFeatureIterator;
import ose.processor.cascader.OSHits;
import ose.processor.cascader.QueryPredicate;
import ose.query.FeatureQuery;
import ose.query.FeatureValue;
import ose.query.OQuery;
import LBJ2.classify.FeatureVector;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.profiling.Profile;

public class LbjseObjectQueryBorda extends FeatureQuery {
	
	protected OSHits result;
	protected OQuery oquery;
	protected int domainId;
	List<String> fields;
	List<Learner> rankers;
	List<List<String>> features;
	List<LbjseFeatureQuery> fieldQueries;
	protected List<List<Double>> scores;
	protected List<Integer> docIds;
	protected List<List<FeatureValue>> featuresList;
	
	public LbjseObjectQueryBorda(QueryInfo queryInfo) throws SQLException{
		oquery = new OQuery(queryInfo.getDomainId());
		
		Map<String, String> queryMap = CommonUtils.convertObjectQueryJsonToMap(queryInfo.getQueryString());
		if (queryMap == null)
			throw new RuntimeException("bad query " + queryInfo);
		queryMap.put("other", "yes");
		fields = new ArrayList<String>();
		rankers = new ArrayList<Learner>();
		features = new ArrayList<List<String>>();
		fieldQueries = new ArrayList<LbjseFeatureQuery>();
		FieldInfoManager fMan = new FieldInfoManager();
		for( String field : queryMap.keySet() ) {
			int fieldId = oquery.getFieldIdFromName(field);
			fields.add(field);
			System.out.println("Adding field " + field);
			FieldInfo finfo = fMan.getFieldInfoForId(fieldId);
			if (finfo == null){
				throw new RuntimeException("Bad field " + fieldId);
			}
			if (finfo.getTrainingSessionId() == -1){
				throw new RuntimeException("no training session for field " + fieldId);
			}
			try {
				TrainingSession session = new TrainingSession(finfo.getTrainingSessionId());
				rankers.add(session.getLearner());
				List<String> searchFeatures = session.getSearchFeatures();
				features.add(searchFeatures);
				String queryValue = queryMap.get(field);
				System.out.println("==== Query value : " + queryValue);
				fieldQueries.add(new LbjseFeatureQuery(queryValue,searchFeatures, session.getLearner() ) );
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Initialized with query : " + queryInfo);
	}
	
	public OSHits getResult() {
		return result;
	}
	
	static private final int CHECK_POINT = 1000;
	
	public void aggregateResult(IndexReader reader)
		throws IOException {
		
		List<DocFeatureIterator> featureGenIterList = new ArrayList<DocFeatureIterator>();
		System.out.println("Feature query predicates : ");
		for (int i = 0; i < fields.size(); i++) {
			for (QueryPredicate pred : fieldQueries.get(i).predicates) {
				if (pred != null){
					featureGenIterList.add( (DocFeatureIterator) pred.getInvertedListIterator(reader) );
					System.out.println("\t" + pred);
				}
			}
		}
		DocFeatureIterator iterator = new CompositeFeatureIterator(featureGenIterList);
		
		scores = new ArrayList<List<Double>>();
		for (int i = 0; i < fields.size(); i++) {
			scores.add(new ArrayList<Double>());
		}
		docIds = new ArrayList<Integer>();
		featuresList = new ArrayList<List<FeatureValue>>();
		
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			
			List<FeatureValue> featureVector = iterator.getFeatures();
			double [] fieldScores = getRegressedScore(featureVector);
			assert(fieldScores.length == scores.size());
			for (int i = 0; i < fieldScores.length; i++) {
				scores.get(i).add(fieldScores[i]);
			}
			docIds.add(docId);
			featuresList.add( featureVector );
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.println("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
		result = new OSHits(reader);
		computeBordaScore();
	}
	
	private void computeBordaScore() throws IOException {
		List<List<Double>> bordaScores = new ArrayList<List<Double>>();
		for (int i = 0; i < scores.size(); i++) {
			bordaScores.add(computeBordaScore(scores.get(i)));
		}
		
		for (int i = 0; i < docIds.size(); i++) {
			Integer docId = docIds.get(i);
			double score = 0;
			for (int j = 0 ; j < fields.size() ; j ++){
				score += bordaScores.get(j).get(i);
				if (docId == 11103 || docId == 12426)
					System.out.println("Doc " + docId + ", field " + j + "\t" + bordaScores.get(j).get(i));
			}
			result.addNewDocument(score, docId, featuresList.get(i));
		}
	}

	private List<Double> computeBordaScore(List<Double> list) {
		Double [] scores = new Double[list.size()];
		Sorter sorter = new Sorter();
		int positiveCount = 0;
		for (int i = 0; i < list.size(); i++) {
			sorter.addPair(new OrderPair(-list.get(i), i));
			if (list.get(i) > 0 )
				positiveCount += 1;
		}
		System.out.println("Positive count : " + positiveCount + "/" + list.size());
		
		//maximize the discount when no positive score is found.
		if (positiveCount == 0)
			positiveCount = list.size(); 
		
		int order = 0;
		int lastOrder = 0;
		double lastScore = Double.MIN_VALUE;
		for (OrderPair pair : sorter.getOrderedList()){
			if (! pair.getValue().equals(lastScore)){
				lastOrder = order;
			}
//			scores[pair.getId()] = (scores.length - 1.0 * lastOrder) ;
			scores[pair.getId()] = (scores.length - 1.0 * lastOrder) / positiveCount; //discount by how many positive scores there are
			
			order += 1;
		}
		return Arrays.asList(scores);
	}

	public double [] getRegressedScore(List<FeatureValue> featureValues) {
		double [] scores = new double[fields.size()];
		int k = 0;
		for (int i = 0 ; i < fields.size() ; i++ ){
			List<FeatureValue> fValForThisField = new ArrayList<FeatureValue>();
			for (int j=0; j < features.get(i).size() ; j++){
				fValForThisField.add(featureValues.get(k));
				k += 1;
			}
			scores[i] = fieldQueries.get(i).getRegressedScore(fValForThisField);
			featureValues.add(new DoubleFeatureValue(scores[i]));
		}
		return scores;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"indexId","queryId"});
		int queryId = options.getInt("queryId");
		int indexId = options.getInt("indexId");
		String outputFile = "C:\\working\\lbjSearch\\lbjsearch.ranked_result";
		rankQueryOnIndexOutputToFile(queryId, indexId, outputFile);
	}

	/**
	 * @param queryId
	 * @param indexId
	 * @param outputFile
	 * @throws SQLException
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void rankQueryOnIndexOutputToFile(int queryId, int indexId,
			String outputFile) throws SQLException, CorruptIndexException,
			IOException, FileNotFoundException {
		QueryInfo qinfo = new QueryInfoManager().queryByKey(queryId);
		rankQueryOnIndexOutputToFile(qinfo, indexId, outputFile);
	}

	public static void rankQueryOnIndexOutputToFile(QueryInfo qinfo, int indexId,
			String outputFile) throws SQLException,
			CorruptIndexException, IOException, FileNotFoundException {
		LbjseObjectQueryBorda query = new LbjseObjectQueryBorda(qinfo);
		Profile profile = Profile.getProfile("runtime");
		profile.start();
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader indexReader = IndexReader.open(iinfo.getIndexPath());
		query.aggregateResult(indexReader);
		OSHits result = query.result;
		result.sortByScore();
		
		LbjseFeatureQuery.writeResultToFile(indexReader, result, outputFile);
		profile.end();
		indexReader.close();
		System.out.println("Done (" + profile.getTotalTime() + "ms)");
	}

	
}
