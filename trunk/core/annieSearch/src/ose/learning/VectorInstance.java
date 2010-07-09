/**
 * 
 */
package ose.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import ose.query.FeatureValue;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;


/**
 * @author Pham Kim Cuong
 *
 */
public class VectorInstance implements Instance {
	private int classification;
	private int numberOfFeatures;
	private List<FeatureValue> featureVector;
	
	public VectorInstance(FeatureValue [] features) {
		classification = -1; //unknown
		numberOfFeatures = features.length;
		featureVector = Arrays.asList(features.clone());
	}
	
	public VectorInstance(VectorInstance another) {
		classification = -1; //unknown
		numberOfFeatures = another.getNumberOfFeatures();
		featureVector = new ArrayList<FeatureValue>( another.getFeatureVector() );
	}
	
	public List<FeatureValue> getFeatureVector() {
		return featureVector;
	}
	
	/* (non-Javadoc)
	 * @see ose.learning.Instance#getClassification()
	 */
	public int getClassification() {
		return classification;
	}

	public void setClassification(int classification) {
		this.classification = classification;
	}
	
	/* (non-Javadoc)
	 * @see ose.learning.Instance#getFeature(int)
	 */
	public FeatureValue getFeature(int nth) {
		if (nth >= 0 && nth < numberOfFeatures ){
			return featureVector.get(nth);
		}
		else 
			return null;	
	}
	
	public void setFeature(int nth, FeatureValue feature) {
		if (nth >= 0 && nth < numberOfFeatures ){
			featureVector.set(nth, feature);
		}
	}

	/* (non-Javadoc)
	 * @see ose.learning.Instance#getNumberOfFeatures()
	 */
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" [" + getClassification() + "] :");
		for (int j = 0 ; j < getNumberOfFeatures(); j ++){
			buffer.append("\t" + getFeature(j));
		}
		return buffer.toString();
	}
	
	public String toJSONString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" [" );
		for (int j = 0 ; j < getNumberOfFeatures(); j ++){
			buffer.append(getFeature(j) + ",");
		}
		buffer.append("]" );
		return buffer.toString();
	}
	
	static public VectorInstance fromJSONArray(JSONArray array) throws JSONException{
		FeatureValue [] values = new FeatureValue[array.length()];
		for (int i = 0; i < values.length; i++) {
			values[i] = new DoubleFeatureValue(array.getDouble(i));
		}
		return new VectorInstance(values);
	}
	
	public weka.core.Instance toWekaInstance(Instances dataset){
		if (dataset == null){
			dataset = getWekaDatasetInfo(numberOfFeatures);
		}
		if (dataset.numAttributes() != numberOfFeatures + 1){
			throw new RuntimeException("Number of Attribute mismatched");
		}
		weka.core.Instance inst = new weka.core.Instance(numberOfFeatures+1);
		inst.setDataset(dataset);
		int i =0 ;
		for (FeatureValue fval : featureVector) {
			if (fval != null){
				Double d = fval.toNumber();
				if (d != null)
					inst.setValue(i, d );
				else
					inst.setMissing(i);
			}
			else
				inst.setMissing(i);
			i++;
		}
		inst.setClassValue(classification);
		return inst;
	}
	
	
	static public Instances getWekaDatasetInfo(int numberOfFeatures) {
		Instances instancesCache = null;
		FastVector attInfo = new FastVector();
		for (int i = 0; i < numberOfFeatures; i++) {
			attInfo.addElement(new Attribute(""+i,i));	
		}
		attInfo.addElement(new Attribute("class"));
		instancesCache = new Instances("default",attInfo, 1);
		instancesCache.setClassIndex(numberOfFeatures);
		return instancesCache ;

	}
}
