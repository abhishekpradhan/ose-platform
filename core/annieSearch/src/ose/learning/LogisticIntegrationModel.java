package ose.learning;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.query.FeatureValue;
import ose.retrieval.CombineResult;
import weka.classifiers.functions.Logistic;

public abstract class LogisticIntegrationModel {
	
	protected InputStream getInputStreamFromResource(String resourceURL){
		ClassLoader loader = getClass().getClassLoader();		
		return loader.getResourceAsStream(resourceURL);
	}
	
	public abstract String getFeatureQuery();
	
	public abstract int getKeyForPredicate(String key);
	
	protected abstract int [] getFeatureModelMapping();
	
	private Logistic [] logistic_models;
	private int numberOfModels;
	private Map<Integer, List<FeatureValue> > segmentedFeatures;
	private VectorInstance segmentedInst ;
	private double [] modelAccuracyFactor;
	protected LogisticIntegrationModel(String modelDir, String [] modelFiles, double [] modelAccuracy) throws Exception{
		if (modelFiles.length != modelAccuracy.length)
			throw new RuntimeException("Not equal sized modelFiles and modelAccuracy");
			
		numberOfModels = modelFiles.length;
		logistic_models = new Logistic [numberOfModels ];
		modelAccuracyFactor = modelAccuracy;
		for (int i = 0; i < modelFiles.length; i++) {
			logistic_models[i] = (Logistic) weka.core.SerializationHelper.read(getInputStreamFromResource(modelDir + "/" + modelFiles[i]));
		}
	}
	
	public CombineResult getCombineResult() {
		CombineResult scoredResult = new CombineResult(numberOfModels);
		for (int i = 0; i < numberOfModels; i++) {
			scoredResult.setWeight(i, modelAccuracyFactor[i]);	
		}
		return scoredResult;
	}
	
	public void setFeatures(List<FeatureValue> features){
		
		segmentedFeatures = new HashMap<Integer, List<FeatureValue>>();
		int [] featureModelMapping = getFeatureModelMapping();
		if (features.size() != featureModelMapping.length )
			throw new RuntimeException("Feature does not contain enough data , required " + featureModelMapping.length + ", required " + features.size());  
		for (int j = 0; j < features.size(); j++) {
			int k = featureModelMapping[j];
			if (! segmentedFeatures.containsKey(k)) {
				segmentedFeatures.put(k, new ArrayList<FeatureValue>() );
			}
			FeatureValue v = features.get(j);
			if (v == null) v = new IntegerFeatureValue(0);
			segmentedFeatures.get(k).add(v);
		}
		
		FeatureValue [] segmentedValues = new FeatureValue [numberOfModels];
		
		for (Integer key : segmentedFeatures.keySet()) {
			VectorInstance inst = new VectorInstance(segmentedFeatures.get(key).toArray(new FeatureValue[]{}));
//			System.out.println("_+_+_+_+ : " + key + " : " + inst.toString());
			double score = logistic_models[key].getScore(inst.toWekaInstance(null));
			segmentedValues[key] = new DoubleFeatureValue(score);
		}
		
		segmentedInst = new VectorInstance(segmentedValues);
	}
	
	public Map<Integer, List<FeatureValue>> getSegmentedFeatures() {
		return segmentedFeatures;
	}
	
	public VectorInstance getSegmentedInstance() {
		return segmentedInst;
	}
	
}
