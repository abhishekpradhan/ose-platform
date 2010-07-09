package lbjse.trainer;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ose.database.lbjse.LbjseSearchFeature;
import ose.database.lbjse.LbjseSearchFeatureManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.classify.Classifier;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.ScoreSet;
import LBJ2.learn.Learner;

public class TrainingSession {
	private LbjseTrainingSession session;
	
	protected Classifier featureGenerator ;
	protected Classifier pairwiseFeatureGenerator ;
	protected Learner learner ;

	public TrainingSession(int sessionId) throws InstantiationException, ClassNotFoundException, IllegalAccessException, SQLException{
		LbjseTrainingSession session = new LbjseTrainingSessionManager().getSessionForId(sessionId);
		initialize(session);
	}
		
	public TrainingSession(LbjseTrainingSession session) throws InstantiationException, ClassNotFoundException, IllegalAccessException{
		initialize(session);
	}

	/**
	 * @param session
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initialize(LbjseTrainingSession session)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		this.session = session;
		System.out.println("TrainingSession : Initializing with " + session);
		String featureGenClassName = session.getFeatureGeneratorClass(); //"lbjse.objectsearch.lbj.other_features";
		Class featureClass = Class.forName(featureGenClassName);
		featureGenerator = (Classifier) featureClass.newInstance();
		pairwiseFeatureGenerator = null; //on-demand
		String classifierClassName = session.getClassifierClass(); //"lbjse.objectsearch.lbj.other_ranker";
		Class classifierClass = Class.forName(classifierClassName);
		learner = (Learner) classifierClass.newInstance();
	}
	
	public FeatureVector getFeatureVector(Object pair){
		return featureGenerator.classify(pair);
	}
	
	public Learner getLearner() {
		return learner;
	}
	
	public Classifier getFeatureGenerator() {
		return featureGenerator;
	}
	
//	public Classifier getPairwiseFeatureGenerator() {
//		try {
//			if (pairwiseFeatureGenerator == null){
//				String featureGenClassName = session.getFeatureGeneratorClass(); //"lbjse.objectsearch.lbj.other_features";
//				String classifierClassName = session.getClassifierClass(); //"lbjse.objectsearch.lbj.other_ranker";
//				int p = classifierClassName.lastIndexOf(".");
//				String packageName = classifierClassName.substring(0,p);
//				int m = featureGenClassName.lastIndexOf(".");
//				String methodName = featureGenClassName.substring(m);
//				Class featureClass = Class.forName(packageName + methodName);
//				pairwiseFeatureGenerator = (Classifier) featureClass.newInstance();
//			}
//			return pairwiseFeatureGenerator;
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return pairwiseFeatureGenerator;
//	}
	
	public String discreteValue(Object pair){
		return learner.discreteValue(pair);
	}
	
	public ScoreSet score(Object pair){
		return learner.scores(pair);
	}
	
	public LbjseTrainingSession getSession() {
		return session;
	}
	
	public List<String> getSearchFeatures() throws SQLException {
		LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
		List<String> features = new ArrayList<String>();
		for (LbjseSearchFeature feature : man.getFeaturesForSession(session.getId())){
			features.add(feature.getValue());
		}
		return features;
	}
	
	/*
	 * Assume that all method takes in exactly ONE param : Object
	 */
	protected Method getMethod(Class featureClass, String methodName) {
		Class params[] = {Object.class};
		try {
			Method thisMethod = featureClass.getDeclaredMethod(methodName, params);
			return thisMethod;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
