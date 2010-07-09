package ose.database.lbjse;

public class LbjseSearchFeature {
	private int featureId;
	private int sessionId;
	private String value;
	
	public LbjseSearchFeature(int featureId, int sessionId, String value){
		this.featureId = featureId;
		this.sessionId = sessionId;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	public int getSessionId() {
		return sessionId;
	}

	public int getFeatureId() {
		return featureId;
	}
	
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
	
	@Override
	public String toString() {
		return "lbjseSearchFeature(" + featureId + "," + sessionId + "," + value + ")";
	}
}
