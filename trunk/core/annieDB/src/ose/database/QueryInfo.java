package ose.database;

public class QueryInfo {
	private int queryId;
	private String queryString;
	private String description;
	private int domainId;
	
	public QueryInfo(int queryId, String queryString, String description){
		this.queryId = queryId;
		this.queryString = queryString;
		this.description = description;
	}
	
	public QueryInfo(int queryId, String queryString, String description, int domainId) {
		this.queryId = queryId;
		this.queryString = queryString;
		this.description = description;
		this.domainId = domainId;
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	public String getQueryString() {
		return queryString;
	}

	public String getDescription() {
		return description;
	}
	
	public int getDomainId() {
		return this.domainId;
	}
	
	
	@Override
	public String toString() {
		return "QueryInfo : \t" + queryId + "\t" + queryString + "\t"  + description + "\t" + domainId;
	}
}
