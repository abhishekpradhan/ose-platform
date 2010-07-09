package ose.database;

public class LBJSEQuery {
	private int queryId;
	private int fieldId;
	private String value;
	
	public LBJSEQuery(int queryId, int fieldId, String value){
		this.fieldId = fieldId;
		this.value= value;
		this.queryId = queryId;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	@Override
	public String toString() {
		return "TrainingQueryInfo(" + queryId + "," + fieldId + "," + value + ")";
	}
}
