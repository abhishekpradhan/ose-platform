package ose.processor.shallow;

import java.io.IOException;

import ose.processor.SpanStatus;

public class ProximityNode extends ose.processor.splock.ProximityNode {

	public ProximityNode() {
		super();
		nodeName = "Proximity-S";
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
