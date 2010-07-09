package ose.database;

public class TrainingQueryInfo {
	private int attributeId;
	private int fieldId;
	private String value;
	
	public TrainingQueryInfo(int attributeId, int fieldId, String value){
		this.fieldId = fieldId;
		this.value= value;
		this.attributeId = attributeId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getTrainingQueryId() {
		return attributeId;
	}
	
	@Override
	public String toString() {
		return "TrainingQueryInfo(" + attributeId + "," + fieldId + "," + value + ")";
	}
}
