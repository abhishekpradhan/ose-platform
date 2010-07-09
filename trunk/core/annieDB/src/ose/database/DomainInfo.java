package ose.database;

public class DomainInfo {
	private int domainId;
	private String name;
	private String description;
	
	public DomainInfo( int domainId, String name, String description){
		this.domainId = domainId;
		this.name = name;
		this.description = description;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return "DomainInfo(" + domainId + "," + name + "," + description + ")";
	}
}
