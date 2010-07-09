package lbjse.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lbjse.rank.ResultItem;
import lbjse.trainer.TrainingSession;
import lbjse.utils.CommonUtils;

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

public class LbjseObjectQuery extends FeatureQuery {
	
	protected OSHits result;
	protected OQuery oquery;
	protected int domainId;
	List<String> fields;
	List<Learner> rankers;
	List<List<String>> features;
	List<LbjseFeatureQuery> fieldQueries;
	
	public LbjseObjectQuery(QueryInfo queryInfo) throws SQLException{
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
		result = new OSHits(reader);
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
		
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			
			List<FeatureValue> featureVector = iterator.getFeatures();
			double score = getRegressedScore(featureVector);
			
			result.addNewDocument(score, docId, featureVector );
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.println("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
	}
	
	public double getRegressedScore(List<FeatureValue> featureValues) {
		double score = 1.0;
		int k = 0;
		for (int i = 0 ; i < fields.size() ; i++ ){
			List<FeatureValue> fValForThisField = new ArrayList<FeatureValue>();
			for (int j=0; j < features.get(i).size() ; j++){
				fValForThisField.add(featureValues.get(k));
				k += 1;
			}
			double s = fieldQueries.get(i).getRegressedScore(fValForThisField);
			featureValues.add(new DoubleFeatureValue(s));
			score *= s;
		}
		return score;
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
		LbjseObjectQuery query = new LbjseObjectQuery(qinfo);
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
