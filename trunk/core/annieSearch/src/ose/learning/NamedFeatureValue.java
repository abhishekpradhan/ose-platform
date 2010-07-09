package ose.learning;

import ose.query.FeatureValue;

public abstract class NamedFeatureValue implements FeatureValue {

	protected String name;
	
	public NamedFeatureValue(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + name +"]" + toNumber(); 
	}
}
