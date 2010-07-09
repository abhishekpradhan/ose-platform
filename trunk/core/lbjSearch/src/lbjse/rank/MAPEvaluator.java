package lbjse.rank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.objectsearch.Utils;
import lbjse.trainer.TrainingSession;
import lbjse.utils.OrderPair;
import lbjse.utils.Sorter;
import LBJ2.classify.Classifier;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.FeatureVectorReturner;
import LBJ2.classify.TestDiscrete;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.profiling.Profile;

public class MAPEvaluator {
	
	static private void showMAP(int sessionId, String queryFile, String trecFile) throws Exception {
		TrainingSession trainingSession = new TrainingSession(sessionId);
		Learner classifier = trainingSession.getLearner();
		
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		List<DocumentFromTrec> docs = trecParser.getDocs();
		
		int domainId = trainingSession.getSession().getDomainId();
		int fieldId = trainingSession.getSession().getFieldId();
		
		List<Query> queries = new ArrayList<Query>();
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		while (true){
			String qvalue = reader.readLine();
			if (qvalue == null) break;
			Query query = new Query(domainId);
			query.setFieldValue(fieldId, qvalue);
			queries.add(query);
		}
		reader.close();
		
		double map = 0;
		double macc = 0;
		for (Query query : queries) {
			ArrayList<Double> scores = new ArrayList<Double>();
			ArrayList<Boolean> labels = new ArrayList<Boolean>();
			System.out.println("Query : -" + query + "-");
			TestDiscrete results = new TestDiscrete();
			for (DocumentFromTrec doc : docs) {
				if (! "other".equals( query.getFieldNameFromId(trainingSession.getSession().getFieldId() ) ) )
					if (doc.getTagForField("other").size() > 0) continue; //just evaluate for object pages.
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				scores.add(classifier.scores(pair).get("true"));
				labels.add(pair.oracle());
				results.reportPrediction(classifier.discreteValue(pair) , pair.oracle() + "");			
				System.out.print(".");
			}
			System.out.println();
			double ap = getAveragePrecision(scores, labels);
			double acc = results.getOverallStats()[0];
			System.out.println("  AP : " + ap);
			results.printPerformance(System.out);
//			System.out.println("  Accuracy : " + acc);
			map += ap;
			macc += acc;
		}
		map /= queries.size();
		macc /= queries.size();
		System.out.println("MAP : " + map );
		System.out.println("Average Accuracy : " + macc );
	}
	
	static private void showPwMAP(int sessionId, String queryFile, String trecFile) throws Exception {
		TrainingSession trainingSession = new TrainingSession(sessionId);
		Learner classifier = trainingSession.getLearner();
		classifier.setExtractor(new FeatureVectorReturner());
		Classifier featureGen = trainingSession.getFeatureGenerator();
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		List<DocumentFromTrec> docs = trecParser.getDocs();
		
		int domainId = trainingSession.getSession().getDomainId();
		int fieldId = trainingSession.getSession().getFieldId();
		
		List<Query> queries = new ArrayList<Query>();
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		while (true){
			String qvalue = reader.readLine();
			if (qvalue == null) break;
			Query query = new Query(domainId);
			query.setFieldValue(fieldId, qvalue);
			queries.add(query);
		}
		reader.close();
		
		double map = 0;
		double macc = 0;
		for (Query query : queries) {
			ArrayList<Double> scores = new ArrayList<Double>();
			ArrayList<Boolean> labels = new ArrayList<Boolean>();
			System.out.println("Query : -" + query + "-");
			TestDiscrete results = new TestDiscrete();
			for (DocumentFromTrec doc : docs) {
				if (! "other".equals( query.getFieldNameFromId(trainingSession.getSession().getFieldId() ) ) )
					if (doc.getTagForField("other").size() > 0) continue; //just evaluate for object pages. 
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				FeatureVector features = Utils.pointwiseFeatureVector(featureGen,pair,classifier);
				double score = classifier.scores(features).get("true");
				scores.add(score);
				labels.add(pair.oracle());				
				results.reportPrediction(classifier.discreteValue(features) , pair.oracle() + "");			
				System.out.print(".");
//				if (pair.oracle())
//					System.out.println("hehe\t" + score + "\t" + features);
			}
			System.out.println();
			
			double ap = getAveragePrecision(scores, labels);
			double acc = results.getOverallStats()[0];
			System.out.println("  AP : " + ap);
			results.printPerformance(System.out);
//			System.out.println("  Accuracy : " + acc);
			map += ap;
			macc += acc;
		}
		map /= queries.size();
		macc /= queries.size();
		System.out.println("MAP : " + map );
		System.out.println("Average Accuracy : " + macc );
	}
	
	public static double getAveragePrecision(List<Double> scores, List<Boolean> labels) {
		Sorter sorter = new Sorter();
		for (int i = 0; i < scores.size(); i++) {
			sorter.addPair(new OrderPair(-scores.get(i), i)); // "-" so that it is sorted descendingly
		}
		
		int count = 0;
		int posCount = 0;
		double sum = 0;
		for (OrderPair pair : sorter.getOrderedList()){
			count += 1;
			if (labels.get(pair.getId())){
				posCount += 1;
				sum += posCount * 1.0 / count;
				System.out.print("\t" + count);
			}
		}
		System.out.println();
		if (posCount == 0)
			return 0;
		else 
			return sum / posCount;
	}
	
	static private void plotPosNegScore(int sessionId, String queryFile, String trecFile) throws Exception {
		TrainingSession trainingSession = new TrainingSession(sessionId);
		Learner classifier = trainingSession.getLearner();
		classifier.setExtractor(new FeatureVectorReturner());
		Classifier featureGen = trainingSession.getFeatureGenerator();
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		List<DocumentFromTrec> docs = trecParser.getDocs();
		
		int domainId = trainingSession.getSession().getDomainId();
		int fieldId = trainingSession.getSession().getFieldId();
		
		List<Query> queries = new ArrayList<Query>();
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		while (true){
			String qvalue = reader.readLine();
			if (qvalue == null) break;
			Query query = new Query(domainId);
			query.setFieldValue(fieldId, qvalue);
			queries.add(query);
		}
		reader.close();
		
		for (Query query : queries) {
			System.out.println("Query : -" + query + "-");
			for (DocumentFromTrec doc : docs) {
				if (! "other".equals( query.getFieldNameFromId(trainingSession.getSession().getFieldId() ) ) )
					if (doc.getTagForField("other").size() > 0) continue; //just evaluate for object pages. 
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				FeatureVector features = Utils.pointwiseFeatureVector(featureGen,pair,classifier);
				double score = classifier.scores(features).get("true");
				System.out.println("Score :\t" + score + "\t" + pair.oracle());
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec","session","query", "mode"});
		Profile.getProfile("main").start();
		if ("pointwise".equals(options.getString("mode"))){
			showMAP(options.getInt("session"), options.getString("query"), options.getString("trec"));
		}
		else if ("pairwise".equals(options.getString("mode"))){
			showPwMAP(options.getInt("session"), options.getString("query"), options.getString("trec"));
		}
		else if ("scoreplot".equals(options.getString("mode"))){
			plotPosNegScore(options.getInt("session"), options.getString("query"), options.getString("trec"));
		}
		else{
			System.err.println("Unknown mode " + options.getString("mode"));
		}
		Profile.getProfile("main").end();
		Profile.printAll();
	}
	

}
