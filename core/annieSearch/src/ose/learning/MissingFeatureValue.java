/**
 * 
 */
package ose.learning;

import ose.query.FeatureValue;


/**
 * @author Pham Kim Cuong
 *
 */
public class MissingFeatureValue implements FeatureValue {
	@Override
	public String toString() {
		return "n/a";
	}
	
	public Double toNumber() {
		return null;
	}
}
