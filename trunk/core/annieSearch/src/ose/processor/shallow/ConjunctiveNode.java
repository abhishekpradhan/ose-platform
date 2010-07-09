package ose.processor.shallow;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.processor.SpanStatus;


public class ConjunctiveNode extends ose.processor.splock.ConjunctiveNode {
	
	public ConjunctiveNode() {
		super("Join-S");
	}
	
	public ConjunctiveNode(String nodeName) {
		super(nodeName);
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		if (status.isDone())
			return status;
		
		if (status.isOnHold() && status.docId >= target)
			return nextDoc();
		
		int holdCount = 3;
		int lastDoc = -1;
		while (holdCount > 0){
			if ( status.docId >= target || status.isDone()  )
				return status.clone();
			lastDoc = status.docId;
			nextDoc();
			if( status.isOnHold() && status.docId == lastDoc){
				holdCount -= 1;
			}
		}
//		System.out.println("Hold count exceeded @" + this);
		return status.clone();
	}
	
}
