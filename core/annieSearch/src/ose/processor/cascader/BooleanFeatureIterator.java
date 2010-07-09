package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ose.learning.IntegerFeatureValue;
import ose.query.FeatureValue;

public class BooleanFeatureIterator implements DocFeatureIterator{

	protected DocIterator childIterator;
	
	public BooleanFeatureIterator(DocIterator childIterator) {
		this.childIterator = childIterator;
	}
	
	public List<FeatureValue> getFeatures() throws IOException {
		List<FeatureValue> result = new ArrayList<FeatureValue>();
		result.add(new IntegerFeatureValue(1));
		return result;
	}
	
	public int getNumberOfFeatures() {
		return 1;
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
