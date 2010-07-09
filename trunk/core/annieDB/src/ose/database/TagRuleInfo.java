package ose.database;

public class TagRuleInfo {
	private int ruleId;
	private int fieldId;
	private String value;
	
	public TagRuleInfo(int ruleId, int fieldId, String value){
		this.fieldId = fieldId;
		this.value= value;
		this.ruleId = ruleId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getRuleId() {
		return ruleId;
	}
	
	@Override
	public String toString() {
		return "TrainingQueryInfo(" + ruleId + "," + fieldId + "," + value + ")";
	}
}
