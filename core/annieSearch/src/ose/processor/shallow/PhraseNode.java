package ose.processor.shallow;

import java.io.IOException;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public class PhraseNode extends ose.processor.splock.PhraseNode {
	
	private int currentPosition;
	
	public PhraseNode() {
		super();
		nodeName = "Phrase-S";
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
	}}
