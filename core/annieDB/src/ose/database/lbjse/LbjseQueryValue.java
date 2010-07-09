package ose.database.lbjse;

public class LbjseQueryValue {
	private int valueId;
	private int sessionId;
	private String value;
	
	public LbjseQueryValue(int valueId, int sessionId, String value){
		this.valueId = valueId;
		this.sessionId = sessionId;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	public int getSessionId() {
		return sessionId;
	}
	
	public int getValueId() {
		return valueId;
	}
	
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValueId(int valueId) {
		this.valueId = valueId;
	}
	
	@Override
	public String toString() {
		return "lbjseQueryValue(" + valueId + "," + sessionId + "," + value + ")";
	}
}
