package ose.learning;

public class DoubleNamedFeatureValue extends  NamedFeatureValue {

	private double value;
	
	public DoubleNamedFeatureValue(String name, double value) {
		super(name);
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public Double toNumber() {
		return new Double(value);
	}	

}
