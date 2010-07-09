package ose.processor.splock;

import ose.processor.SpanStatus;

public class SpanStatusAggregation {
	SpanStatus smallest;
	
	public SpanStatusAggregation() {
		smallest = SpanStatus.done();
	}
	
	public void aggregate(SpanStatus sp) {
		if (sp.compareTo(smallest) < 0)
			smallest = sp;
	}
	
	public SpanStatus getSmallest() {
		return smallest;
	}
	
}
