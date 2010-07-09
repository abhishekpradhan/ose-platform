package lbjse.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lbjse.objectsearch.Utils;
import lbjse.rank.ResultItem;
import lbjse.trainer.TrainingSession;
import lbjse.utils.LbjUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import common.CommandLineOption;
import common.profiling.Profile;

import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.Feature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.FeatureVectorReturner;
import LBJ2.classify.RealFeature;
import LBJ2.learn.Learner;
import LBJ2.learn.Sigmoid;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;
import ose.learning.DoubleNamedFeatureValue;
import ose.learning.IntegerFeatureValue;
import ose.parser.OSQueryParser;
import ose.processor.cascader.CompositeFeatureIterator;
import ose.processor.cascader.DocFeatureIterator;
import ose.processor.cascader.OSHits;
import ose.processor.cascader.QueryPredicate;
import ose.query.FeatureQuery;
import ose.query.FeatureValue;
import ose.retrieval.ResultPresenter;

public class LbjseFeatureQuery extends FeatureQuery {
	
	protected OSHits result;
	protected List<QueryPredicate> predicates;
	protected List<DiscreteFeature> searchFeatures;
	protected Learner learner ;
	protected String queryValue;
	protected String featureQueryString = "";

	//HACK : should not use global variable here. BUGGY.
	public static boolean PAIRWISE = true;
	
	public LbjseFeatureQuery(String queryValue, List<String> lbjFeatures, Learner learner) {
		this.queryValue = queryValue;
		learner.setExtractor(new FeatureVectorReturner());
		this.learner = learner;
		
		featureStrings = new ArrayList<String>();
		searchFeatures = new ArrayList<DiscreteFeature>();
		for (String lbjFeatureString : lbjFeatures){
			DiscreteFeature lbjFeature = LbjUtils.parseDiscreteFeature(lbjFeatureString);
			searchFeatures.add(lbjFeature);
			String featureStr = convertLbjFeatureToFeatureQuery(lbjFeature);
			featureStrings.add(featureStr);
			featureQueryString += " " + featureStr;
		}
		System.out.println("Got feature string : " + featureQueryString);
		try {
			OSQueryParser parser = new OSQueryParser();
			ose.processor.cascader.FeatureQuery query = parser.parseFeatureQuery(featureQueryString);
			predicates = query.getPredicates();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("LbjseFeatureQuery initialized with ");
		System.out.println("\tQuery : " + queryValue);
		System.out.println("\tLearner : " + learner);
		System.out.println("\tFeatures : " + lbjFeatures);
		System.out.println("\tFeature Query : " + featureQueryString);
	}

	
	private String convertLbjFeatureToFeatureQuery(DiscreteFeature lbjFeature ){
		if (lbjFeature.getIdentifier().startsWith("WordsInTitle")){
			return "%BooleanFeature(HTMLTitle('" + lbjFeature.getStringValue() + "'))";
		}
		else if (lbjFeature.getIdentifier().startsWith("WordsInBody")){
			return "%BooleanFeature(Token('" + lbjFeature.getStringValue() + "'))";
		}
		else if (lbjFeature.getIdentifier().startsWith("InTitle")){
			return "%BooleanFeature(HTMLTitle('" + queryValue + "'))";
		}
		else if (lbjFeature.getIdentifier().startsWith("InBody")){
			return "%BooleanFeature(Phrase('" + queryValue + "'))";
		}
		else if (lbjFeature.getIdentifier().startsWith("SurroundingWordsInBody_area")){
			//TODO : fix constant -15,5
			return "%BooleanFeature(Proximity(Phrase(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-15,5))";
		}
		else if (lbjFeature.getIdentifier().startsWith("SurroundingWordsInBody")){
			//TODO : fix constant -5,5
			return "%BooleanFeature(Proximity(Phrase(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-5,5))";
		}
		else if (lbjFeature.getIdentifier().startsWith("SurroundingWordsInTitle")){
			//TODO : fix constant -3,3
			return "%BooleanFeature(Proximity(HTMLTitle(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-3,3))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumericSurroundingWordsInBody_proc")){
			//TODO : fix constant -2,2
			return "%BooleanFeature(Proximity(Number_body(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-2,2))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumericSurroundingWordsInBody_price")){
			//TODO : fix constant -5,1
			return "%BooleanFeature(Proximity(Number_body(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-5,1))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumericSurroundingWordsInBody")){
			//TODO : fix constant -5,5
			return "%BooleanFeature(Proximity(Number_body(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-5,5))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumericSurroundingWordsInTitle_proc")){
			//TODO : fix constant -1,1
			return "%BooleanFeature(Proximity(Number_title(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-1,1))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumericSurroundingWordsInTitle")){
			//TODO : fix constant -5,5
			return "%BooleanFeature(Proximity(Number_title(" + queryValue + "),Token('" + lbjFeature.getStringValue() + "'),-5,5))";
		}
		else if (lbjFeature.getIdentifier().startsWith("InTitle")){
			return "%BooleanFeature(HTMLTitle(" + queryValue + "))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumberInTitle")){
			return "%BooleanFeature(Number_title(" + queryValue + "))";
		}
		else if (lbjFeature.getIdentifier().startsWith("NumberInBody")){
			return "%BooleanFeature(Number_body(" + queryValue + "))";
		}
		else if (lbjFeature.getIdentifier().startsWith("BigramWordsInBody")){
			String phrase = lbjFeature.getStringValue().replaceAll("_", ",");
			return "%BooleanFeature(Phrase(" + phrase + "))";
		} 
		else 
			throw new RuntimeException("Dont know how to deal with this feature : " + lbjFeature);
	}
	
	static private final int CHECK_POINT = 100;
	public void aggregateResult(IndexReader reader)
		throws IOException {
		result = new OSHits(reader);
		List<DocFeatureIterator> featureGenIterList = new ArrayList<DocFeatureIterator>();
		for (QueryPredicate pred : predicates) {
			if (pred != null)
				featureGenIterList.add( (DocFeatureIterator) pred.getInvertedListIterator(reader) );
		}
		DocFeatureIterator iterator = new CompositeFeatureIterator(featureGenIterList);
		
		//iterator.skipTo(1004595); //for debugging
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			
			List<FeatureValue> featureVector = iterator.getFeatures();
			double score = getRegressedScore(featureVector);
			
			result.addNewDocument(score, docId, featureVector );
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.print("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
		
		//result.sortByScore();
	}
	
	public double getRegressedScore(List<FeatureValue> featureValues) {
		FeatureVector vector = new FeatureVector();
		for (int i = 0; i < predicates.size(); i++) {
			IntegerFeatureValue val = (IntegerFeatureValue) featureValues.get(i);
			if (val.getValue() == 1){
				DiscreteFeature f = searchFeatures.get(i);
				if (PAIRWISE)
					vector.addFeature(Utils.convertPointwiseFeatureToPairwise(f.toString(), 1.0, hackFeatureGenName(learner.name), learner.containingPackage));
				else
					vector.addFeature(f);
			}
		}
		double score = new Sigmoid().normalize(learner.scores(vector)).get("true");
//		System.out.println("score : " + score );
		return score;
	}


	private String hackFeatureGenName(String name) {
		int p = name.indexOf("_");
		return name.substring(0,p) + "_features";
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"lbjfeature","sessionId", "indexId","queryValue"});
		String featureFile = options.getString("lbjfeature");
		String queryValue = options.getString("queryValue");
		int sessionId = options.getInt("sessionId");
		int indexId = options.getInt("indexId");
		TrainingSession session = new TrainingSession(sessionId);
		BufferedReader reader = new BufferedReader(new FileReader(featureFile));
		List<String> lbjFeatureStrings = new ArrayList<String>();
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			lbjFeatureStrings.add(line.trim());
		}
		reader.close();
		             
		Profile profile = Profile.getProfile("runtime");
		profile.start();
		LbjseFeatureQuery query = new LbjseFeatureQuery(queryValue, lbjFeatureStrings, session.getLearner());
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader indexReader = IndexReader.open(iinfo.getIndexPath());
		query.aggregateResult(indexReader);
		OSHits result = query.result;
		result.sortByScore();
		String outputFile = "C:\\working\\lbjSearch\\lbjsearch.ranked_result";
		writeResultToFile(indexReader, result, outputFile);
		profile.end();
		indexReader.close();
		System.out.println("Done (" + profile.getTotalTime() + "ms)");
	}
	
	/**
	 * @param indexReader
	 * @param result
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public static void writeResultToFile(IndexReader indexReader,
			OSHits result, String outputFile) throws FileNotFoundException,
			CorruptIndexException, IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		
		for (Document doc : result) {
			Document fullDoc = indexReader.document(result.getDocID());
			String title = fullDoc.get(IndexFieldConstant.FIELD_DOCUMENT_TITLE);
			if (title == null) title = "[no_title]";
			String url = fullDoc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			if (url == null) url = "[no_url]";
			ResultItem item = new ResultItem(result.getDocID(), url ,title,  result.score(), null);
			writer.println(item + "\t" + result.docFeatures());
		}
		writer.close();
		System.out.println("Result written to " + outputFile);
	}
}
