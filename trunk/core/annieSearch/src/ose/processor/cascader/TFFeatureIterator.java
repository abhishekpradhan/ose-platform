/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ose.learning.IntegerFeatureValue;
import ose.query.FeatureValue;

/**
 * @author Pham Kim Cuong
 *
 */
public class TFFeatureIterator implements DocFeatureIterator{
	protected DocPositionIterator childIterator;
	
	public TFFeatureIterator(DocPositionIterator childIterator) {
		this.childIterator = childIterator;
	}
	
	public int getNumberOfFeatures() {
		return 1;
	}
	
	/*
	 * This must always be called after next() == true; 
	 */
	public List<FeatureValue> getFeatures() throws IOException {
		List<FeatureValue> result = new ArrayList<FeatureValue>();
		result.add(new IntegerFeatureValue(childIterator.getFrequency()));
		return result;
	}
	

	public String getClue() {
		return "no clue";
	}

	public int getDocID() {
		return childIterator.getDocID();
	}

	public boolean next() throws IOException {
		return childIterator.next();
	}

	public boolean skipTo(int docID) throws IOException {
		return childIterator.skipTo(docID);
	}
	
	static protected FeatureValue DEFAULT_FEATURE_VALUES = new IntegerFeatureValue(0);
	
	public List<FeatureValue> getDefaultValues() {
		List<FeatureValue> values = new ArrayList<FeatureValue>();
		values.add(DEFAULT_FEATURE_VALUES);
		return values;
	}
}
