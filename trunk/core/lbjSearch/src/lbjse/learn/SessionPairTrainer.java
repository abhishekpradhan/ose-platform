package lbjse.learn;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.trainer.TrainingSession;
import lbjse.trainer.Utils;

import org.json.JSONObject;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.GenericPair;
import common.profiling.Profile;

public class SessionPairTrainer {
	protected boolean bForget = false;
	protected LBJTrecFileParser docParser;
	protected TrainingSession trainingSession;
	protected List<Query> allQueries ;
	protected String fieldName;
	protected List<DocumentFromTrec> docs;
	public static int MAXIMUM_PAIR_PER_QUERY = 1000;
	public static int NEGATIVE_EXAMPLES_RATIO = 3;
	
	public SessionPairTrainer(int sessionId) throws IllegalAccessException, ClassNotFoundException, 
		SQLException, InstantiationException{
		LbjseTrainingSessionManager sessMan = new LbjseTrainingSessionManager();
		LbjseTrainingSession session = sessMan.getSessionForId(sessionId) ;
		trainingSession = new TrainingSession(session);
		FieldInfo finfo = new FieldInfoManager().getFieldInfoForId(session.getFieldId());
		fieldName = finfo.getName();
		allQueries = new ArrayList<Query>();
		LbjseQueryValueManager qvMan = new  LbjseQueryValueManager();
		
		for (LbjseQueryValue lbjQValue : qvMan.getQueryValueForSession(session.getId())){
			Query query = new Query(session.getDomainId());
			query.setFieldValue(session.getFieldId(), lbjQValue.getValue());
			allQueries.add(query);
		}
		
		System.out.println("Number of queries " + allQueries.size());
	}
	
	public void learn(String trecFile, int numRounds) throws IOException, NoSuchFieldException, IllegalAccessException{
		docParser = new LBJTrecFileParser(trecFile);
		docs = docParser.getDocs();
		
		List<String> queryStrings = new ArrayList<String>();
		for (Query q : allQueries){
			queryStrings.add(q.getFieldValue(fieldName));
		}
		String[] fieldValues = queryStrings.toArray(new String[]{});
		
		Set<String> partitions = new HashSet<String>( Arrays.asList(fieldValues));
		Set<Integer> targetDocIds ;
		if ("other".equals(fieldName))
			targetDocIds = Utils.getAllDocIds(docs);
		else
			targetDocIds = Utils.getDocIdsWithField(docs,fieldName,  partitions);
		Set<Integer> trainingDocIds = new HashSet<Integer>();
		Set<Integer> testingDocIds = new HashSet<Integer>();
		for (Integer docId : targetDocIds) {
			if (Math.random() < 1.0/5){
				testingDocIds.add(docId);
			}
			else{
				trainingDocIds.add(docId);
			}
		}
		
		System.out.println("Total docs : " + docs.size());
		System.out.println("Target docs : " + targetDocIds.size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + testingDocIds.size());
		List<GenericPair<DocQueryPair, DocQueryPair>> trainingData = getPairsOfDifferentLabels(NEGATIVE_EXAMPLES_RATIO, Utils.getSubset(docs, trainingDocIds), allQueries);
		List<GenericPair<DocQueryPair, DocQueryPair>> testingData = getPairsOfDifferentLabels(NEGATIVE_EXAMPLES_RATIO, Utils.getSubset(docs,testingDocIds), allQueries);
		
		Learner ranker = trainingSession.getLearner();
		
		if (bForget )
			ranker.forget();
		
		Field field = ranker.getClass().getField("isTraining");
		field.setBoolean(ranker, true);
		
		learnAndTest(ranker, numRounds, trainingData, testingData);
		
		ranker.save();	
//		ranker.write(System.out);
	}
	
	public void test(String trecFile) throws Exception{
		Learner ranker = trainingSession.getLearner();
		
		docParser = new LBJTrecFileParser(trecFile);
		DocQueryFileParser data = new DocQueryFileParser(docParser.getDocs(), allQueries);
		double accuracy = Utils.evaluatePairwiseClassifier(data, ranker, trainingSession.getFeatureGenerator(), true);
		JSONObject perf = new  JSONObject();
		perf.put("accuracy", accuracy);
		
		LbjseTrainingSessionManager sessMan = new LbjseTrainingSessionManager();
		trainingSession.getSession().setCurrentPerformance(perf.toString());
		sessMan.update(trainingSession.getSession());
		System.out.println("Updated with : " + perf.toString());
	}
	
	public void printResult(){
		docParser.reset();
		DocQueryFileParser data = new DocQueryFileParser(docParser.getDocs(), allQueries);
		Learner ranker = trainingSession.getLearner();
		Utils.evaluatePairwiseClassifier(data, ranker, trainingSession.getFeatureGenerator(), true);
	}
	
	public static List<GenericPair<DocQueryPair, DocQueryPair>> getPairsOfDifferentLabels(double negPosRatio, List<DocumentFromTrec> docs, List<Query> queries){
		Random random = new Random(System.currentTimeMillis());
		List<GenericPair<DocQueryPair, DocQueryPair>> result = new ArrayList<GenericPair<DocQueryPair, DocQueryPair>>();
		int i = 0;
		for (Query query : queries){
			i += 1;
			List<DocQueryPair> positivePairs = new ArrayList<DocQueryPair>();
			List<DocQueryPair> negativePairs = new ArrayList<DocQueryPair>();
			for (DocumentFromTrec doc : docs){
				DocQueryPair pair = new DocQueryPairFromFile(doc, query);
				if (pair.oracle())
					positivePairs.add(pair);
				else
					negativePairs.add(pair);
			}
			double threshold = 1.0;
			System.out.println("Q" + i + " : " + positivePairs.size() + "," + negativePairs.size());
			if (negativePairs.size() > positivePairs.size() * negPosRatio){
				threshold = positivePairs.size() * negPosRatio / negativePairs.size();
				negativePairs = (List<DocQueryPair>) randomSampling(threshold, negativePairs);
				System.out.println("\t post sample, #neg = " + negativePairs.size());
			}
			if (positivePairs.size() * negativePairs.size() == 0) continue;
			//we don't want to take too many example pairs per query, thus dropping them here. 
			double droppingThreshold = 1.0 * MAXIMUM_PAIR_PER_QUERY / (positivePairs.size() * negativePairs.size());
			for (DocQueryPair pos : positivePairs)
				for (DocQueryPair neg : negativePairs){
					if (random.nextDouble() > droppingThreshold) continue;
					//toss a coin for the order of the pair
					if (random.nextDouble() >= 0.5) 
						result.add(new GenericPair<DocQueryPair, DocQueryPair>(pos,neg));
					else
						result.add(new GenericPair<DocQueryPair, DocQueryPair>(neg,pos));
				}
		}
		System.out.println("Total pairs " + result.size());
		return result;
	}
	
	public static List<? extends Object> randomSampling(double ratio, List<? extends Object> population){
		List<Object> result = new ArrayList<Object>();
		Random rand = new Random(System.currentTimeMillis());
		for (Object o : population)
			if (rand.nextDouble() < ratio)
				result.add(o);
		return result;
	}
	
	public void learnAndTest(Learner ranker, int nRounds, 
			List<GenericPair<DocQueryPair, DocQueryPair>> trainingData, 
			List<GenericPair<DocQueryPair, DocQueryPair>>  testingData ){
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			count = 0;
			for (GenericPair<DocQueryPair, DocQueryPair> pair : trainingData){
				count += 1;
				ranker.learn(pair);
				if ( (count + 1) % 1000 == 0)
					System.out.print("+");
			}
			System.out.print(".");
			if ( (i + 1) % 10 == 0) {
				System.out.println(" round : " + i);
				System.out.println("Progress .... ");
//				System.out.println("\tTrain : " + Utils.evaluateClassifier(trainingData, ranker, false) );
				System.out.println("\tTest : " + Utils.evaluatePairClassifier(testingData, ranker, false) );
			}
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		Utils.evaluatePairClassifier(trainingData, ranker, true);
		Utils.evaluatePairClassifier(testingData, ranker, true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"opt", "sessionId","trec"});
		String trecFile = options.getString("trec");
		int sessionId = options.getInt("sessionId");
		Profile.getProfile("runtime").start();
		SessionPairTrainer learner = new SessionPairTrainer(sessionId);
		if (options.hasArg("forget")){
			learner.bForget = true;
		}
		if ("train".equals(options.getString("opt"))){
			int nRounds = options.getInt("nrounds",5);
			learner.learn(trecFile, nRounds);
			learner.printResult();
		}
		else if ("test".equals(options.getString("opt"))){
			learner.test(trecFile);
		}
		Profile.getProfile("runtime").end();
		Profile.printAll();
	}

}
