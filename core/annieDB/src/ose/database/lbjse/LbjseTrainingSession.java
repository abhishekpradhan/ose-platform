package ose.database.lbjse;

import org.json.JSONObject;

public class LbjseTrainingSession {
	private int id;
	private int domainId;
	private int fieldId;
	private String description;
	private String featureGeneratorClass;
	private String classifierClass;
	private String currentPerformance;
	
	public LbjseTrainingSession(int id,  int domainId, int fieldId, 
			String description, String featureGenClass, String classifierClass, String curPerformance){
		this.id = id;
		this.domainId = domainId;
		this.fieldId = fieldId;
		this.description = description;
		this.featureGeneratorClass = featureGenClass;
		this.classifierClass = classifierClass;
		this.currentPerformance = curPerformance;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public void setLbjClass(String lbjClass) {
		this.featureGeneratorClass = lbjClass;
	}
	
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getClassifierClass() {
		return classifierClass;
	}
	
	public String getFeatureGeneratorClass() {
		return featureGeneratorClass;
	}
	
	public String getCurrentPerformance() {
		return currentPerformance;
	}
	
	public void setCurrentPerformance(String currentPerformance) {
		this.currentPerformance = currentPerformance;
	}
	
	@Override
	public String toString() {
		return "LbjseTrainingSession(" + domainId + "," + fieldId + "," + description +
			"," + classifierClass +
			"," + featureGeneratorClass +
			"," + currentPerformance +
			")";
	}
}
