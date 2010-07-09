package ose.processor.splock;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

/*
 * This class uses the first child to push the join down.
 */
public class LeftPushConjunctiveNode extends ProcessingNode {
	protected StatusesMaxHeapManager childManager;
	protected boolean gotFirstSpan;
	protected TreeNode pushingNode;
	
	public LeftPushConjunctiveNode() {
		super("LeftJoin");
	}
	
	public LeftPushConjunctiveNode(String nodeName) {
		super(nodeName);
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		childManager = new StatusesMaxHeapManager(children.size());
		for (int i = 0; i < children.size(); i++) {
			childManager.addRequestStatus(i, children.get(i).getCurrentStatus().clone());
		}
		childManager.initializeHeap();
		pushingNode = children.get(0);
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		return nextDoc(true);
	}
	
	

	
	@Override
	public SpanStatus skipTo(int target) throws IOException {		
		if (status.isDone())
			return status;
		
		SpanStatus response = movePushingNode(target); //only need to move the pushing node, others will follow
		if (!response.isBadSkipTo())
			return nextDoc(false);
		else
			return response;
	}
	
	public SpanStatus lazySkipTo(int docID) throws IOException {
		if (status.isDone())
			return status;
		
		SpanStatus response = lazyMovePushingNode(docID); //only need to move the pushing node, others will follow
		if (!response.isBadSkipTo())
			return lazyNextDoc();
		else
			return response;
	}
	
	/**
	 * @param skipToDocId
	 * @return the smallest moving child.
	 * @throws IOException
	 */
	private SpanStatus moveChildren(int skipToDocId) throws IOException {
		SpanStatusAggregation agg = new SpanStatusAggregation();
		for (int i = 0 ; i < childManager.getNumberOfRequesters();i ++){
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (currentStatus.docId < skipToDocId || (currentStatus.docId == skipToDocId && currentStatus.isOnHold())){
				SpanStatus response = children.get(i).lazySkipTo(skipToDocId);
				childManager.updateStatus(i, response.docId, response.getStatus());
				agg.aggregate(response);
			}
			else 
				agg.aggregate(currentStatus);
		}
		
		return agg.getSmallest();
	}
	
	private SpanStatus movePushingNode(int skipToDocId) throws IOException {
		SpanStatus response = children.get(0).skipTo(skipToDocId);
//		SpanStatus response = children.get(0).nextDoc();  //this is the dumber version, that doesn't use other child to push this pushing child
		childManager.updateStatus(0, response.docId, response.getStatus());
		return response;
	}
	
	private SpanStatus lazyMovePushingNode(int skipToDocId) throws IOException {
		SpanStatus response = children.get(0).lazySkipTo(skipToDocId);
		childManager.updateStatus(0, response.docId, response.getStatus());
		return response;
	}
	
	/*
	 * precondition : if all children are available, they must have the same docId
	 * if some are on-hold, advance them, otherwise, advance everyone. 
	 */
	private boolean advanceChildren() throws IOException {
		SpanStatus response = children.get(0).nextDoc();
		childManager.updateStatus(0, response.docId, response.getStatus());
		return true;
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		currentSpan = null;
		return false; //only return one span by getSpan; 
	}
	
	@Override
	public Span getCurrentSpan() {
		if (!gotFirstSpan){ //lazy set span
			setCurrentSpan();
			gotFirstSpan = true;
		}
		return currentSpan;
	}
	
	private void setCurrentSpan(){
		currentSpan.docId = status.docId;
		currentSpan.startPos = Integer.MAX_VALUE;
		currentSpan.endPos = -1;
		for (TreeNode child : children){
			if (child.getCurrentSpan().startPos < currentSpan.startPos){
				currentSpan.startPos = child.getCurrentSpan().startPos ;
			}
			if (child.getCurrentSpan().endPos > currentSpan.endPos){
				currentSpan.endPos = child.getCurrentSpan().endPos ;
			}
		}
	}
	
	/*
	 * make sure that all children of this node release their locks on any "ShareSkipToNode"
	 */
	private void skipAllChildrenToTheEnd() throws IOException{
		for (TreeNode child : children){
			if (!child.getCurrentStatus().isDone())
				child.skipTo(Integer.MAX_VALUE);
		}
	}
	
	private SpanStatus nextDoc(boolean advanceChildrenFirst) throws IOException {
		if (status.isDone())
			return status;
		
		//doesnot advance if currently onhold because the it might miss the current on-hold docId
		if ( advanceChildrenFirst && !status.isOnHold()) 
			advanceChildren();
		int lastMovedDocId = -1;
		while (true){
			SpanStatus largest = childManager.getLargest().clone();
			if (largest.isDone()) {
				status = SpanStatus.done();
				skipAllChildrenToTheEnd();
				return status;
			}
				
			if (largest.isAvailable() && childManager.allTheSame()){ //got a hit
				currentSpan = new Span(largest.docId, 0,Integer.MAX_VALUE);
				status = SpanStatus.available(largest.docId);
				gotFirstSpan = false; //lazy
				return status;
			}

			SpanStatus res;
			boolean moveNode = false;
			if (childManager.getRequesterStatus(0).equals(largest)){
				res = moveChildren(largest.docId);
			}
			else{
				res = movePushingNode(largest.docId);
				moveNode = true;
			}
			
			if (res.docId == largest.docId && res.isOnHold()){ //moved, but still has on-hold child.
				status = res;
				return status;
			}
			
			//when does this happen?
			if (moveNode && res.docId == lastMovedDocId){ //stalled at lastMovedDocId
				status = res;
				return status;
			}
			
			if (moveNode)
				lastMovedDocId = res.docId; //don't know why?			
			
			if (res.isBadSkipTo()){				
				status = res;
//				return status;
				throw new RuntimeException("Why? " + res);
			}
		}
	}
	
	private SpanStatus lazyNextDoc() throws IOException {
		if (status.isDone())
			return status;
		
		SpanStatus largest = childManager.getLargest().clone();
		if (largest.isDone()) {
			status = SpanStatus.done();
			skipAllChildrenToTheEnd();
			return status;
		}
				
		if (largest.isAvailable() && childManager.allTheSame()){ //got a hit
			currentSpan = new Span(largest.docId, 0,Integer.MAX_VALUE);
			status = SpanStatus.available(largest.docId);
			gotFirstSpan = false; //lazy
			return status;
		}
		
		currentSpan = null;
		status = SpanStatus.onhold(largest.docId);
		return status;
	}
}
