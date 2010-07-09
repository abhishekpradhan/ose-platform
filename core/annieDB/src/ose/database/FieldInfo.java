package ose.database;

public class FieldInfo {
	private int fieldId;
	private int domainId;
	private String name;
	private String type;
	private String description;
	private int trainingSessionId;
	
	public FieldInfo(int fieldId, int domainId, String name, String type, String description, int trainingSessionId){
		this.fieldId = fieldId;
		this.domainId = domainId;
		this.type = type;
		this.name = name;
		this.description = description;
		this.trainingSessionId = trainingSessionId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getTrainingSessionId() {
		return trainingSessionId;
	}
	
	@Override
	public String toString() {
		return "FieldInfo(" + fieldId + "," + domainId + "," + name + "," + type + "," + description + "," + trainingSessionId + ")";
	}
}
