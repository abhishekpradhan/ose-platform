package lbjse.learn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.trainer.TrainingSession;
import lbjse.trainer.Utils;
import lbjse.utils.CommonUtils;
import lbjse.utils.ProgressBar;

import org.json.JSONObject;

import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseSearchFeature;
import ose.database.lbjse.LbjseSearchFeatureManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.profiling.Profile;

public class LearnFromAllFeatures {
	boolean bForget = false;
	Set<String> featureSet;
	LBJTrecFileParser docParser;
	TrainingSession trainingSession;
	List<Query> allQueries ;
	
	public LearnFromAllFeatures(int sessionId) throws IllegalAccessException, ClassNotFoundException, 
		SQLException, InstantiationException{
		LbjseTrainingSessionManager sessMan = new LbjseTrainingSessionManager();
		LbjseTrainingSession session = sessMan.getSessionForId(sessionId) ;
		trainingSession = new TrainingSession(session);
		
		allQueries = new ArrayList<Query>();
		LbjseQueryValueManager qvMan = new  LbjseQueryValueManager();
		
		for (LbjseQueryValue lbjQValue : qvMan.getQueryValueForSession(session.getId())){
			Query query = new Query(session.getDomainId());
			query.setFieldValue(session.getFieldId(), lbjQValue.getValue());
			allQueries.add(query);
		}
		System.out.println("Number of queries " + allQueries.size());
	}
	
	public void initFeatures(String featureFile) throws IOException{
		featureSet = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(featureFile));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			featureSet.add(line.trim());
		}
		reader.close();
		System.out.println("Initialized with " + featureSet.size() + " features. ");
	}
	
	public void initFeatures(int sessionId) throws IOException{
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
	
	public void learn(String trecFile, int numRounds) throws IOException, NoSuchFieldException, IllegalAccessException{
		docParser = new LBJTrecFileParser(trecFile);
		Learner ranker = trainingSession.getLearner();
		if (bForget){
			System.out.println("Forget training");
			ranker.forget();
		}
		Field field = ranker.getClass().getField("isTraining");
		field.setBoolean(ranker, true);
		DocQueryFileParser data = new DocQueryFileParser(docParser.getDocs(), allQueries);
		int nexamples = 0;
		for (int i = 0; i < numRounds; i++) {
			for (Query query : allQueries) {
				docParser.reset();
				while (true){ 
					DocumentFromTrec doc = (DocumentFromTrec) docParser.next();
					if (doc == null)
						break;
					if (! CommonUtils.hasEnoughTags(doc,query))
						continue;
					nexamples +=1 ;
					ProgressBar.printDot(nexamples, 20, 800);
					DocQueryPairFromFile pair = new DocQueryPairFromFile(doc,query);
					ranker.learn(pair);
				}
			}
			System.out.println("one round");
			Utils.evaluateClassifier(data, ranker, true);
		}
		
		ranker.save();
	}
	
	public void test(String trecFile) throws Exception{
		Learner ranker = trainingSession.getLearner();
		
		docParser = new LBJTrecFileParser(trecFile);
		DocQueryFileParser data = new DocQueryFileParser(docParser.getDocs(), allQueries);
		double accuracy = Utils.evaluateClassifier(data, ranker, true);
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
		Utils.evaluateClassifier(data, ranker, true);
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
		LearnFromAllFeatures learner = new LearnFromAllFeatures(sessionId);
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
