package lbjse.learn;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.LinkedList;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import LBJ2.classify.Classifier;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.LabelVectorReturner;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.profiling.Profile;

/*
 * A bit smarter learner, that try to learn negative examples separated from positive examples
 * Each round, the learner only learn either negatives or positives
 * In the next round, it focuses on the types that it makes most errors.
 * The rationale behind this is that, if the classifier performs well on negative examples, then
 * we wouldn't have to go through negative examples in the next round, thus, saving training time.
 */
public class ImprovedLearnFromConcreteFeatures extends LearnFromConcreteFeatures{
	
	public enum DecisionEnum { POS, NEG, BOTH };
	
	public ImprovedLearnFromConcreteFeatures(int sessionId) throws IllegalAccessException, ClassNotFoundException, 
		SQLException, InstantiationException{
		super(sessionId);
	}
	
	public void learn(String trecFile, int numRounds) throws IOException, NoSuchFieldException, IllegalAccessException{
		docParser = new LBJTrecFileParser(trecFile);
		Learner ranker = trainingSession.getLearner();
		ranker.forget();
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
		Field field = ranker.getClass().getField("isTraining");
		field.setBoolean(ranker, true);
		DecisionEnum decision = DecisionEnum.BOTH;
		int numberOfNegativeMistake = 0;
		int numberOfPositiveMistake = 0;
		
		for (int i = 0; i < numRounds; i++) {
			
			
			for (Query query : allQueries) {
				docParser.reset();
				while (true){ 
					DocumentFromTrec doc = (DocumentFromTrec) docParser.next();
					if (doc == null)
						break;
					DocQueryPairFromFile pair = new DocQueryPairFromFile(doc,query);
					
					DiscreteFeature label = new DiscreteFeature("lbjse.objectsearch",
							"oracle", "" + pair.oracle());
						
					if (decision == DecisionEnum.BOTH || 
							"true".equals(label.getValue()) && decision == DecisionEnum.POS  ||
							"false".equals(label.getValue()) && decision == DecisionEnum.NEG){
						FeatureVector allFeatures = trainingSession.getFeatureVector(pair);
						LinkedList<Object> filteredList = new LinkedList<Object>();
						for (Object f : allFeatures.features){
							if (featureSet.contains( f.toString() ) ){
								filteredList.add(f);
							}
						}
						FeatureVector concreteFeatures = new FeatureVector(filteredList);
						DiscreteFeature feature = (DiscreteFeature )ranker.classify(concreteFeatures).features.getFirst();
						concreteFeatures.addLabel(label);
						ranker.learn(concreteFeatures);
						if (! feature.getValue().equals(label.getValue())) {
							if ("true".equals(label.getValue()))
								numberOfPositiveMistake += 1;
							else 
								numberOfNegativeMistake += 1;
						}
					}
				}
			}
			System.out.println("round : " + i + "\t" + decision);
			System.out.println("\t positive mistake : " + numberOfPositiveMistake);
			System.out.println("\t negative mistake : " + numberOfNegativeMistake);
			System.out.println("\t total mistakes : " + (numberOfNegativeMistake + numberOfPositiveMistake));
			if (decision == DecisionEnum.BOTH){
				if (numberOfPositiveMistake > numberOfNegativeMistake){ 
					decision = DecisionEnum.POS;
					numberOfPositiveMistake = 0;
				}
				else {
					decision = DecisionEnum.NEG;
					numberOfNegativeMistake = 0;
				}
			}
			else if (decision == DecisionEnum.POS && numberOfPositiveMistake < numberOfNegativeMistake){
				decision = DecisionEnum.BOTH;
				numberOfPositiveMistake = 0;
				numberOfNegativeMistake = 0;
			}
			else if (decision == DecisionEnum.NEG && numberOfNegativeMistake < numberOfPositiveMistake){
				decision = DecisionEnum.BOTH;
				numberOfPositiveMistake = 0;
				numberOfNegativeMistake = 0;
			}
//			Utils.evaluateClassifier(data, ranker, true);
		}
		
		ranker.setLabeler(originalLabeler);
		ranker.save();
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
		ImprovedLearnFromConcreteFeatures learner = new ImprovedLearnFromConcreteFeatures(sessionId);
		if ("train".equals(options.getString("opt"))){
			
			int nRounds = options.getInt("nrounds",5);
			if (options.getString("features") != null)
				learner.initFeatures(options.getString("features"));
			else
				learner.initFeatures(sessionId);
			
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
