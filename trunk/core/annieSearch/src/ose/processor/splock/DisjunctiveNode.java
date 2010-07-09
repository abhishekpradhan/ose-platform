package ose.processor.splock;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;


public class DisjunctiveNode extends ProcessingNode {
	
	protected StatusesMinHeapManager childManager;
	protected boolean gotFirstSpan; //lazy setCurrentSpan()
	
	public DisjunctiveNode() {
		super("Union");
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		childManager = new StatusesMinHeapManager(children.size());
		for (int i = 0; i < children.size(); i++) {
			childManager.addRequestStatus(i, children.get(i).getCurrentStatus().clone());
		}
		childManager.initializeHeap();
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		SpanStatus smallest = childManager.getSmallest().clone();
		
		for (int i = 0 ; i < childManager.getNumberOfRequesters();i ++){
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (smallest.equals(currentStatus) || currentStatus.isOnHold()){
				SpanStatus response = children.get(i).nextDoc();
				childManager.updateStatus(i, response.docId, response.getStatus());
			}
		}
		status = childManager.getSmallest();
		
		if (status.isAvailable()){
			gotFirstSpan = false;			
		}
		return status;
	}

	@Override
	public boolean nextSpan() throws IOException {
		boolean found = false;
		int minSpanStart = Integer.MAX_VALUE;
		TreeNode theChild = null;
		for (TreeNode child : children){
			if (child.getCurrentStatus().isAvailable() && child.getCurrentStatus().docId == status.docId && child.getCurrentSpan() != null ){
				found = true;
				if (child.getCurrentSpan().startPos < minSpanStart){
					minSpanStart = child.getCurrentSpan().startPos;	
					theChild = child;
				}
			}
		}
		if (found){
			currentSpan = theChild.getCurrentSpan().clone();
			theChild.nextSpan();
		}
		else{
			currentSpan = null;
		}
		return found;
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		for (int i = 0 ; i < childManager.getNumberOfRequesters();i ++){
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (currentStatus.docId < target){
				SpanStatus response = children.get(i).skipTo(target);
				childManager.updateStatus(i, response.docId, response.getStatus());
			}
		}
		
		status = childManager.getSmallest();
		
		if (status.isAvailable()){
			gotFirstSpan = false;
		}
		return status;
	}
	
	public SpanStatus lazySkipTo(int target) throws IOException {
		for (int i = 0 ; i < childManager.getNumberOfRequesters();i ++){
			SpanStatus currentStatus = childManager.getRequesterStatus(i);
			if (currentStatus.docId < target || currentStatus.isOnHold()){
				SpanStatus response = children.get(i).lazySkipTo(target);
				childManager.updateStatus(i, response.docId, response.getStatus());
			}
		}
		
		status = childManager.getSmallest();
		
		if (status.isAvailable()){
			gotFirstSpan = false;
		}
		return status;
	}
	
	@Override
	public Span getCurrentSpan() {
		try {
			if (!gotFirstSpan){ //lazy get span
				gotFirstSpan = true;
				nextSpan();
			}
			return currentSpan;
		} catch (IOException e) {
			return null;
		}
	}
}
