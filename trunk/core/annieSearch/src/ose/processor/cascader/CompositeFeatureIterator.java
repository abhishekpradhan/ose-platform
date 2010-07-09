package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ose.query.FeatureValue;


public class CompositeFeatureIterator extends DisjunctiveJoinIterator 
		implements DocFeatureIterator {

	private int numberOfFeatures;
	
	private int [] featureSizes; //TODO : deprecated
	
	private List< List<FeatureValue>> defaultValues;
	
	public CompositeFeatureIterator(List<DocFeatureIterator> subIterators) throws IOException{
		super(subIterators);
		numberOfFeatures = 0;
		featureSizes = new int[subIterators.size()];
		int i = 0;
		defaultValues = new ArrayList<List<FeatureValue>>();
		for (DocFeatureIterator iterator : subIterators) {			
			numberOfFeatures += iterator.getNumberOfFeatures();
			featureSizes[i++] = iterator.getNumberOfFeatures();
			defaultValues.add(iterator.getDefaultValues());
		}
		
	}
	
	public List<FeatureValue> getFeatures() throws IOException {
		List<FeatureValue> features = new ArrayList<FeatureValue>();
		for (int i = 0 ; i < featureSizes.length ; i++) {
			if (toAdvanceList.indexOf(i) > -1){
				features.addAll(((DocFeatureIterator) iteratorList[i]).getFeatures());
			}
			else{
				features.addAll(defaultValues.get(i));
			}
		}
		return features;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public List<FeatureValue> getDefaultValues() {
		List<FeatureValue> values = new ArrayList<FeatureValue>();
		for (List<FeatureValue> defVals: defaultValues) {
			values.addAll(defVals);
		}
		return values;
	}
}
