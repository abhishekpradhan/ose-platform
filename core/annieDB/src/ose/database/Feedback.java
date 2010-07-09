package ose.database;

public class Feedback {
	private int docId;
	private int indexId;
	private int queryId;
	private int domainId;
	
	private boolean relevant;
	
	public Feedback(int queryId, int docId, int indexId, int domainId, boolean relevant){
		this.docId = docId;
		this.indexId = indexId;
		this.queryId = queryId;
		this.domainId = domainId;
		this.relevant = relevant ;
	}
	
	public int getDocId() {
		return docId;
	}
	
	public int getQueryId() {
		return queryId;
	}
	
	public boolean getRelevant() {
		return relevant;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	@Override
	public String toString() {
		return "Feedback : \t" + queryId + "\t" + docId + "\t" + indexId + "\t" + domainId + "\t" + relevant;
	}
}
