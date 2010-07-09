package ose.database;

import java.sql.Date;
import java.sql.Time;

public class LBJSECache {
	private int queryId;
	private int cacheId;
	private int indexId;
	private String cacheFile;
	private Date dateCreated;
	
	public LBJSECache(int cacheId, int queryId, int indexId, String cacheFile, Date dateCreated){
		this.cacheId = cacheId;
		this.queryId = queryId;
		this.indexId = indexId;
		this.cacheFile = cacheFile;
		this.dateCreated = dateCreated;
	}
	
	public int getCacheId() {
		return cacheId;
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public String getCacheFile() {
		return cacheFile;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setCacheFile(String cacheFile) {
		this.cacheFile = cacheFile;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
	
	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
	
	@Override
	public String toString() {
		return "LBJSECache(" + queryId + "," + indexId + "," + cacheFile + "," + dateCreated + ")";
	}
}
