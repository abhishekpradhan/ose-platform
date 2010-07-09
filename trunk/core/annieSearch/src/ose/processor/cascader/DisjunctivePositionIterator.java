package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ose.index.IdValuePairComparator;

public class DisjunctivePositionIterator extends DisjunctiveJoinIterator implements DocPositionIterator{
	
	private boolean positionHeapInitialized;
	private PriorityQueue<IdValuePairComparator> nextPositions; 
	private int currentPosition;
	private ArrayList<Integer> toAdvancePositionList;
	
	public DisjunctivePositionIterator(List<DocPositionIterator> iterators) throws IOException{
		super(iterators);

		positionHeapInitialized = false;
	}

	@Override
	public boolean next() throws IOException {
		positionHeapInitialized = false;
		currentPosition =  -1;
		return super.next() && nextPosition();
	}
	
	@Override
	public boolean skipTo(int target) throws IOException {
		positionHeapInitialized = false;
		currentPosition =  -1;
		return super.skipTo(target) && nextPosition();
	}
	
	public boolean nextPosition() throws IOException {
		if (!positionHeapInitialized){
			initializePositionHeap();
			advanceThePositionList(true);
		}else{ 
			advanceThePositionList(false);
		}
		
		if (nextPositions.size() == 0){
			currentPosition = -1;
			return false;
		}
		else{
			IdValuePairComparator nextID = nextPositions.poll();
			int theId = nextID.getListID();
			currentPosition = nextID.getValue();
			//check all of them share this doc, remove from the heap
			toAdvancePositionList.add(theId);
			
			while ( nextPositions.size() > 0 ){
				IdValuePairComparator nextPair = nextPositions.peek();
				if ( nextPair.getValue() == currentPosition){
					nextPositions.poll();
					toAdvancePositionList.add(nextPair.getListID());
				}
				else 
					break;
			}
			
			return true;
		}
	}
	
	public int getPosition() throws IOException {
		if (currentPosition == -1 && nextPosition())  //nextPosition() will compute currentPosition
			return currentPosition; 
		else
			return currentPosition;
	}
	
	public boolean skipToPosition(int nextPos) throws IOException {
		while (nextPosition()){
			if (getPosition() >= nextPos ) return true;
		}
		return false;
	}
	
	private void initializePositionHeap() throws IOException{

		nextPositions = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
		toAdvancePositionList = new ArrayList<Integer>();
		for (int j = 0; j < N; j++) {
			if (iteratorList[j] != null && iteratorList[j].getDocID() == currentDocID)
				toAdvancePositionList.add(j);
		}
		
		positionHeapInitialized = true;
	}
	
	private void advanceThePositionList(boolean firsttime) throws IOException {
		/* push next Position back to the list. */
		for(Integer lid : toAdvancePositionList ){
			if (firsttime || ((DocPositionIterator)iteratorList[lid]).nextPosition()){ //the first time, don't call nextPosition() since it has been called once already.
				nextPositions.add(new IdValuePairComparator(lid,((DocPositionIterator)iteratorList[lid]).getPosition()));
			}
		}
		toAdvancePositionList.clear();
	}
	
	/*
	 * is the sum of all sub iterator frequencies 
	 */
	public int getFrequency() {
		int sum = 0;
		for (Integer lid : toAdvanceList) {
			sum += ((DocPositionIterator)iteratorList[lid]).getFrequency();
		}
		return sum;
	}
}
