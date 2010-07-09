package ose.processor.splock;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;


public class ConjunctiveNode extends ProcessingNode {
	protected StatusesMaxHeapManager childManager;
	protected boolean gotFirstSpan;
	
	public ConjunctiveNode() {
		super("Join");
	}
	
	public ConjunctiveNode(String nodeName) {
		super(nodeName);
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		if ( children.size() <= 0){
			throw new RuntimeException("No children found");
		}
		childManager = new StatusesMaxHeapManager(children.size());
		for (int i = 0; i < children.size(); i++) {
			childManager.addRequestStatus(i, children.get(i).getCurrentStatus().clone());
		}
		childManager.initializeHeap();
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		return nextDoc(true);
	}
	
	private SpanStatus nextDoc(boolean advanceChildrenFirst) throws IOException {
		if (status.isDone())
			return status;
		
		if ( advanceChildrenFirst )
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
			
			SpanStatus res = moveChildren(largest.docId);
			if (res.docId == largest.docId && res.isOnHold()){ //moved, but still has on-hold child.
				status = res;
				return status;
			}			
			if (res.docId == lastMovedDocId){ //stalled at lastMovedDocId
				status = res;
				return status;
			}
			lastMovedDocId = res.docId;
			
			if (res.isBadSkipTo()){				
				status = res;
//				return status;
				throw new RuntimeException("Why? " + res);
			}
		}
	}

	
	@Override
	public SpanStatus skipTo(int target) throws IOException {		
		if (status.isDone())
			return status;
		
		SpanStatus response = moveChildren(target);
		if (!response.isBadSkipTo())
			return nextDoc(false);
		else
			return response;
	}
	
	public SpanStatus lazySkipTo(int target) throws IOException {
		if (status.isDone())
			return status;
		
		SpanStatus response = lazyMoveChildren(target);
		if (!response.isBadSkipTo())
			return lazyNextDoc();
		else
			return response;
	}
	
	private SpanStatus lazyNextDoc() throws IOException{
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
	
	/**
	 * @param skipToDocId
	 * @return the smallest moving child.
	 * @throws IOException
	 */
	private SpanStatus moveChildren(int skipToDocId) throws IOException {
		SpanStatusAggregation agg = new SpanStatusAggregation();
		for (int i = 0 ; i < childManager.getNumberOfRequesters();i ++){
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (currentStatus.docId < skipToDocId){
				SpanStatus response = children.get(i).skipTo(skipToDocId);
				childManager.updateStatus(i, response.docId, response.getStatus());
				agg.aggregate(response);
			}
			else 
				agg.aggregate(currentStatus);
		}
		
		return agg.getSmallest();
	}
	
	/**
	 * move all children to the target, but no more than target
	 * @param skipToDocId
	 * @return the smallest moving child.
	 * @throws IOException
	 */
	private SpanStatus lazyMoveChildren(int skipToDocId) throws IOException {
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
	
	/*
	 * precondition : if all children are available, they must have the same docId
	 * if some are on-hold, advance them, otherwise, advance everyone. 
	 */
	private boolean advanceChildren() throws IOException {
		boolean advanced = false;
		int minDocId = Integer.MAX_VALUE;
		int maxDocId = Integer.MIN_VALUE;
		for (int i = 0; i < childManager.getNumberOfRequesters(); i++) {
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (currentStatus.isOnHold()){
				SpanStatus response = children.get(i).nextDoc();
				childManager.updateStatus(i, response.docId, response.getStatus());
				advanced = true;
			}
			if (currentStatus.docId < minDocId)
				minDocId = currentStatus.docId ;
			if (currentStatus.docId > maxDocId)
				maxDocId = currentStatus.docId ;
		}
		if (!advanced && minDocId == maxDocId){ //all of children are available, advance all of them to the next
			for (int i = 0; i < childManager.getNumberOfRequesters(); i++) {
				SpanStatus response = children.get(i).nextDoc();
				childManager.updateStatus(i, response.docId, response.getStatus());
			}
		} //other, don't advance anyone, because the zi
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
	
	
}
