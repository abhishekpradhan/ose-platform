package ose.learning;

import ose.query.FeatureValue;

public class DoubleFeatureValue implements FeatureValue {

	private double value;
	
	public DoubleFeatureValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
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
