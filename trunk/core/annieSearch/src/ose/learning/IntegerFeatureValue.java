package ose.learning;

import ose.query.FeatureValue;


public class IntegerFeatureValue implements FeatureValue {
	private int value;
	
	public IntegerFeatureValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {	
		return "" + value;
	}
	
	public Double toNumber() {
		return new Double(value);
	}
}
