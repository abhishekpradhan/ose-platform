package lbjse.learn;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.trainer.Utils;

import org.json.JSONObject;

import ose.database.lbjse.LbjseSearchFeature;
import ose.database.lbjse.LbjseSearchFeatureManager;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.classify.Classifier;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.LabelVectorReturner;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.GenericPair;
import common.profiling.Profile;

public class SessionPairConcreteTrainer extends SessionPairTrainer{
	protected Set<String> featureSet ;
	
	public SessionPairConcreteTrainer(int sessionId) throws IllegalAccessException, ClassNotFoundException, 
		SQLException, InstantiationException{
		super(sessionId);
		featureSet = new HashSet<String>();
		try {
			LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
			for (LbjseSearchFeature feature : man.getFeaturesForSession(sessionId)){
				featureSet.add(feature.getValue().trim());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Initialized with " + featureSet.size() + " features. ");
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
	
	public void learnAndTest(Learner ranker, int nRounds, 
			List<GenericPair<DocQueryPair, DocQueryPair>> trainingData, 
			List<GenericPair<DocQueryPair, DocQueryPair>>  testingData ){
		Classifier originalLabeler = ranker.getLabeler();
		ranker.setLabeler(
		   new LabelVectorReturner(){
			   public String getOutputType() { return "discrete"; }
			   public String[] allowableValues() { 
				   return DiscreteFeature.BooleanValues; 
			   }
			   public String discreteValue(Object e)
			   {
				   return ((DiscreteFeature) classify(e).firstFeature()).getValue();
			   }
		});
		String pwFeatureName = trainingSession.getFeatureGenerator().name;
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			count = 0;
			for (GenericPair<DocQueryPair, DocQueryPair> pair : trainingData){
				count += 1;
			    DocQueryPair pair1 = (DocQueryPair) pair.getFirst();
			    DocQueryPair pair2 = (DocQueryPair) pair.getSecond();
			    List diffFeatures = lbjse.objectsearch.Utils.differentiateFeatures(trainingSession.getFeatureGenerator(), pair1, pair2, featureSet);
			    boolean oracle = lbjse.objectsearch.Utils.oracle(pair);
			    DiscreteFeature label = new DiscreteFeature("lbjse.objectsearch",
						"oracle", "" + oracle);
				ranker.learn(Utils.convertListToFeatureVector(diffFeatures, label, ranker.containingPackage, pwFeatureName ));
			    
				if ( (count + 1) % 1000 == 0)
					System.out.print("+");
			}
			System.out.println(".");
			if ( (i + 1) % 10 == 0) {
				System.out.println(" round : " + i);
				System.out.println("Progress .... ");
//				System.out.println("\tTrain : " + Utils.evaluateClassifier(trainingData, ranker, false) );
				System.out.println("\tTest : " + Utils.evaluatePairClassifier(testingData, ranker, false) );
			}
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		
		ranker.setLabeler(originalLabeler);
		
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
		SessionPairConcreteTrainer learner = new SessionPairConcreteTrainer(sessionId);
		if (options.hasArg("forget")){
			learner.bForget = true;
		}
		
		if (options.hasArg("maxPPQ")){
			SessionPairConcreteTrainer.MAXIMUM_PAIR_PER_QUERY = options.getInt("maxPPQ");
		}
		
		if (options.hasArg("negRatio")){
			SessionPairConcreteTrainer.NEGATIVE_EXAMPLES_RATIO = options.getInt("negRatio");
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
