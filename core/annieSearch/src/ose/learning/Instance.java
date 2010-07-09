/**
 * 
 */
package ose.learning;

import ose.query.FeatureValue;
import weka.core.Instances;


/**
 * @author Pham Kim Cuong
 *
 */
public interface Instance {
	public int getNumberOfFeatures();
	public int getClassification();
	public void setClassification(int classification);
	public String toString();	
	public FeatureValue  getFeature(int nth);
	public weka.core.Instance toWekaInstance(Instances dataset);
}
