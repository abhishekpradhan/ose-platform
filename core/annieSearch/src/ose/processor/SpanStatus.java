package ose.processor;

public class SpanStatus implements Comparable<SpanStatus> {
	private static final SpanStatus SPAN_STATUS_MAXIMAL = new SpanStatus(Integer.MAX_VALUE, Integer.MAX_VALUE);
	public static final int STATUS_INVALID = 0;
	private static final SpanStatus SPAN_STATUS_INVALID = new SpanStatus(STATUS_INVALID, -1);
	public static final int STATUS_ON_HOLD = 2;
	public static final int STATUS_AVAILABLE = 4;
	public static final int STATUS_NO_MORE_SPAN = 6;
	public static final int STATUS_BAD_SKIP_TO = 100;
	private static final SpanStatus SPAN_STATUS_BAD_SKIP_TO = new SpanStatus(STATUS_BAD_SKIP_TO);
	public static final int STATUS_DONE = 10;
	
	private int status;
	public int docId;
	
	public SpanStatus(int status) {
		this.status = status;
		docId = -1;
	}

	public SpanStatus(int status, int docId) {
		this.status = status;
		this.docId = docId;
	}

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean isOnHold() {
		return status == STATUS_ON_HOLD;
	}
	
	public boolean isAvailable(){
		return status == STATUS_AVAILABLE;
	}
	
	public boolean isInvalid(){
		return status == STATUS_INVALID;
	}
	
	public boolean isBadSkipTo(){
		return status == STATUS_BAD_SKIP_TO;
	}
	
	public boolean isDone(){
		return status == STATUS_DONE || docId == Integer.MAX_VALUE;
	}
	
	public boolean isNoMoreSpan(){
		return status == STATUS_NO_MORE_SPAN;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpanStatus) {
			SpanStatus other = (SpanStatus) obj;
			return other.status == status && other.docId == docId; 
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + status + "]" + docId;
	}
	
	static public SpanStatus invalid(){
		return SPAN_STATUS_INVALID;
	}
	
	static public SpanStatus available(int docId){
		return new SpanStatus(STATUS_AVAILABLE,docId);
	}
	
	static public SpanStatus onhold(int docId){
		return new SpanStatus(STATUS_ON_HOLD, docId);
	}
	
	static public SpanStatus badSkipTo(){
		return SPAN_STATUS_BAD_SKIP_TO;
	}

	static public SpanStatus done(){
		return new SpanStatus(STATUS_DONE, Integer.MAX_VALUE);
	}
	
	static public SpanStatus noMoreSpan(int docId){
		return new SpanStatus(STATUS_NO_MORE_SPAN, docId);
	}
	
	static public SpanStatus maximal(){
		return SPAN_STATUS_MAXIMAL;
	}
	
	public int compareTo(SpanStatus o) {
		if (docId != o.docId)
			if (docId < o.docId)
				return -1;
			else
				return 1;
		else {
			if (status < o.status)
				return -1;
			else
				return 1;
		}
	}
	
	@Override
	public SpanStatus clone() {
		return new SpanStatus(status, docId);
	}
}
