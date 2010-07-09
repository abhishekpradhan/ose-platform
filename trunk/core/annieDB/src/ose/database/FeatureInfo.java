package ose.database;

public class FeatureInfo {
	private int featureId;
	private int fieldId;
	private String template;
	private double weight;

	public FeatureInfo(int featureId, int fieldId, String template, double weight){
		this.featureId = featureId;
		this.fieldId = fieldId;
		this.template = template;
		this.weight = weight;
	}
	
	public FeatureInfo(int fieldId, String template, double weight){
		this.featureId = -1;
		this.fieldId = fieldId;
		this.template = template;
		this.weight = weight;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public int getFeatureId() {
		return featureId;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
	
	@Override
	public String toString() {
		return "FeatureInfo(" + featureId + "," + fieldId + "," + template + "," + weight + ")";
	}
}
