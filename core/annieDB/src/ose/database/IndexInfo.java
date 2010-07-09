package ose.database;

public class IndexInfo {
	private int indexId;
	private String description;
	private String indexPath;
	private String cachePath;
	private String name;
	
	public IndexInfo(int indexId, String description, String path, String name, String cachePath){
		this.indexId = indexId;
		this.description = description;
		this.indexPath = path;
		this.name = name;
		this.cachePath = cachePath;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getIndexPath() {
		return indexPath;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCachePath() {
		return cachePath;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}
	
	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
	
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "IndexInfo(" + indexId + "," + description + "," + indexPath + "," + name + "," + cachePath + ")";
	}
}
