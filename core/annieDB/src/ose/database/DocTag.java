package ose.database;

public class DocTag {
	private int tagId;
	private int indexId;
	private int docId;
	private int fieldId;
	private String value;
	
	public DocTag(int tagId, int indexId, int docId, int fieldId, String value){
		this.tagId = tagId;
		this.indexId = indexId;
		this.docId = docId;
		this.fieldId = fieldId;
		this.value = value;
	}
	
	public DocTag(int indexId, int docId, int fieldId, String value){
		this.tagId = -1;
		this.indexId = indexId;
		this.docId = docId;
		this.fieldId = fieldId;
		this.value = value;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public int getDocId() {
		return docId;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public int getTagId() {
		return tagId;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
	@Override
	public String toString() {
		return "DocTag(" + tagId + "," + indexId + "," + docId + "," + fieldId + "," + value + ")";
	}
}
