package ose.database.lbjse;

public class LbjseFeatureTemplate {
	private int id;
	private int sessionId;
	private String template;
	private double weight;

	public LbjseFeatureTemplate(int id, int sessionId, String template, double weight){
		this.id = id;
		this.sessionId = sessionId;
		this.template = template;
		this.weight = weight;
	}
	
	public LbjseFeatureTemplate(int sessionId, String template, double weight){
		this.id = -1;
		this.sessionId = sessionId;
		this.template = template;
		this.weight = weight;
	}
	
	public int getSessionId() {
		return sessionId;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "lbjseFeatureTemplate(" + id + "," + sessionId + "," + template + "," + weight + ")";
	}
}
