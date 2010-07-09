package ose.processor.cascader;

import java.io.IOException;
import java.util.List;

import ose.query.FeatureValue;


public class DisjunctiveFeatureIterator extends DisjunctiveJoinIterator
		implements DocFeatureIterator {

	private int numberOfFeatures;
	
	public DisjunctiveFeatureIterator(List<DocFeatureIterator> children) throws IOException {
		super(children);
		numberOfFeatures = -1;
		for (DocFeatureIterator iterator : children) {
			if (numberOfFeatures == -1){
				numberOfFeatures = iterator.getNumberOfFeatures();
			}
			else if (numberOfFeatures != iterator.getNumberOfFeatures()){
				throw new IOException("Number of features in each child iterator don't match");
			}
				
		}
	}
	
	
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}
	
	public List<FeatureValue> getFeatures() throws IOException {
		throw new IOException("not sure how");
//		if (currentListID >= 0 && currentListID < N){
//			return ((DocFeatureIterator) iteratorList[currentListID]).getFeatures();
//		}
//		else
//			return null;
	}
	
	public List<FeatureValue> getDefaultValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
