package ose.processor.splock;

import java.io.IOException;
import java.util.List;

import ose.processor.SpanStatus;
import ose.processor.TreeNode;

//this class should be called exactly once

public class OrderedSkiper {
	private List<TreeNode> skippers;
	public OrderedSkiper(List<TreeNode> children) {
		skippers = children;
	}
	
	/*
	 * call skipTo in order from smallest docId to largest. Stop when one of them doesn't move or all of them has skipped to target.
	 * return the smallest node after moving.
	 */
	public SpanStatus skipTo(int target) throws IOException{
		SpanStatusAggregation resultAgg = new SpanStatusAggregation();
		for (TreeNode skipper : skippers){ 
			SpanStatus currentStatus = skipper.getCurrentStatus();
			if (currentStatus.docId < target){
				resultAgg.aggregate(skipper.skipTo(target));
			}
			else{ //account for the other as well
				resultAgg.aggregate(currentStatus);
			}
		}
		skippers = null; //nullify it sothat no one can call again
		return resultAgg.getSmallest();
	}
	
	/*
	 * 
	 * call next on the smallest ones. Plus those that are on hold (because those onhold one might hold some of the smallest ones)
	 */
	public SpanStatus next() throws IOException{
		SpanStatusAggregation agg = new SpanStatusAggregation();
		for (TreeNode skipper : skippers){
			agg.aggregate(skipper.getCurrentStatus());
		}
		SpanStatus smallest = agg.getSmallest();
		
		SpanStatusAggregation nextAgg = new SpanStatusAggregation();
		for (TreeNode skipper : skippers){
			SpanStatus currentStatus = skipper.getCurrentStatus();
			if (smallest.equals(currentStatus) || currentStatus.isOnHold()){
				nextAgg.aggregate(skipper.nextDoc());
			}
			else { //account for the other as well
				nextAgg.aggregate(currentStatus);
			}
		}
		skippers = null; //nullify it sothat no one can call again
		return nextAgg.getSmallest();
	}
	
	public boolean noMoreLeft(){
		return skippers.size() == 0;
	}
}
