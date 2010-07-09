package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Similarity;

import ose.learning.DoubleFeatureValue;
import ose.learning.IntegerFeatureValue;
import ose.query.FeatureValue;

public class UnaryFunctionFeatureIterator implements DocFeatureIterator {

	static public final int FUNC_LOG = 1;
	static public final int FUNC_SQRT = 2;
	static private String [] FUNC_NAMES = new String[] { "LOG", "SQRT" };
	private byte [] fieldNorms;
	private float[] normDecoder = Similarity.getNormDecoder();
	
	protected DocFeatureIterator childIterator;
	private double input;
	protected int funcType;
	protected Object [] funcArgs;
	
	public UnaryFunctionFeatureIterator(int functionType, Object [] args, DocFeatureIterator childIterator, IndexReader reader) throws IOException{
		this.childIterator = childIterator;
		funcType = functionType;
		funcArgs = args;
		fieldNorms = reader.norms("Token"); //hack here to get the norm, in the future, should also use other fields 
	}
	
	public List<FeatureValue> getFeatures() throws IOException {
		List<FeatureValue> result = new ArrayList<FeatureValue>();
		double value = 0;
		switch (funcType){
			case FUNC_LOG:
				if (input > 0) value = Math.log(input + 1) * normDecoder[fieldNorms[getDocID()] & 0xFF ];
				break;
			case FUNC_SQRT:
				if (input >= 0) value = Math.sqrt(input);
				break;
		}
		result.add(new DoubleFeatureValue(value));
		return result;
	}

	public int getNumberOfFeatures() {
		return 1;
	}
	
	public String getFunctionName(){
		if (funcType >= 0 && funcType < FUNC_NAMES.length)
			return FUNC_NAMES[funcType];
		else
			return "Unknown";
	}
	public String getClue() {
		return "=>" + getFunctionName() + "[" + funcArgs + "](" + input + ")";
	}

	public int getDocID() {
		return childIterator.getDocID();
	}

	public boolean next() throws IOException {
		if (childIterator.next()){
			input = childIterator.getFeatures().get(0).toNumber();
			return true;
		}
		else
			return false;
	}

	public boolean skipTo(int docID) throws IOException {
		if (childIterator.skipTo(docID) ){
			input = childIterator.getFeatures().get(0).toNumber();
			return true;
		}
		else
			return false;
	}
	
	static protected FeatureValue DEFAULT_FEATURE_VALUES = new IntegerFeatureValue(0);
	
	public List<FeatureValue> getDefaultValues() {
		List<FeatureValue> values = new ArrayList<FeatureValue>();
		values.add(DEFAULT_FEATURE_VALUES);
		return values;
	}
}
