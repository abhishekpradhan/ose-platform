package ose.database;

public class TrainingFeedback {
	private int docId;
	private int indexId;
	private int queryId;
	private int domainId;
	
	private boolean relevant;
	
	public TrainingFeedback(int queryId, int docId, int indexId, int domainId, boolean relevant){
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
		return "TrainingFeedback : \t" + queryId + "\t" + docId + "\t" + indexId + "\t" + domainId + "\t" + relevant;
	}
}
