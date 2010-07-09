/**
 * 
 */
package ose.learning;

import ose.query.FeatureValue;


/**
 * @author Pham Kim Cuong
 *
 */
public class ClueFeatureValue implements FeatureValue {
	String theClue;
	
	public ClueFeatureValue(String clue) {
		theClue = clue;
	}
	
	public String toString() {
		return theClue;
	}
	
	public Double toNumber() {
		return null;
	}
}
