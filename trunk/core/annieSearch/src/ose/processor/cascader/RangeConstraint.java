/**
 * 
 */
package ose.processor.cascader;

import ose.index.Utils;



/**
 * @author Pham Kim Cuong
 *
 */
public class RangeConstraint implements Constraint {

	protected double lowerBound, upperBound;
	
	
	
	public RangeConstraint(double lower, double upper){
		lowerBound = lower;
		upperBound = upper;
	}
	
	public boolean satisfy(Object fieldValue) {
		Double v ;
		if (fieldValue instanceof Double) {
			v = (Double) fieldValue;
		}
		else if (fieldValue instanceof String) { //old code
			v = Utils.getNumberValue((String)fieldValue);
		}
		else {
			return false;
		}
		return (v != null && v >= lowerBound && v <= upperBound); 
	}
	
	public double getLowerBound() {
		return lowerBound;
	}
	
	public double getUpperBound() {
		return upperBound;
	}
	
	@Override
	public String toString() {
		return lowerBound + ".." + upperBound;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof RangeConstraint) {
			RangeConstraint otherConstraint = (RangeConstraint) obj;
			return lowerBound == otherConstraint.lowerBound && upperBound == otherConstraint.upperBound;
		}
		return false;
	}

}
