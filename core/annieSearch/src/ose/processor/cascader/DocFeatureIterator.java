package ose.processor.cascader;

import java.io.IOException;
import java.util.List;

import ose.query.FeatureValue;


public interface DocFeatureIterator extends DocIterator {
	/**
	 *  This is for machine learning component to collect feature values 
	 * @return the feature value object
	 */
	public List<FeatureValue> getFeatures() throws IOException ;
	
	/**
	 * the number of values in the feature set. this number is fixed for each type of iterator. 
	 * @return
	 */
	public int getNumberOfFeatures();
	
	public List<FeatureValue> getDefaultValues();
}
