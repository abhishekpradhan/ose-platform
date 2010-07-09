package ose.database.lbjse;

import java.sql.Date;
import java.sql.Time;

public class LbjseData {
	private int id;
	
	private String path;
	private String description;
	private int indexId;
	private Date dateCreated;
	
	public LbjseData(int id, String path, String description, int indexId, Date dateCreated){
		this.id = id;
		this.path = path;
		this.description = description;
		this.indexId = indexId;
		this.dateCreated = dateCreated;
	}
	
	public int getId() {
		return id;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
	
	@Override
	public String toString() {
		return "LbjseData(" + id + "," + path + "," + description + "," + indexId + "," + dateCreated + ")";
	}
}
