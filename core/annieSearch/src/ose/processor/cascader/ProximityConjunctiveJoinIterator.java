package ose.processor.cascader;

import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

import ose.index.IdValuePairComparator;

/*
 * WARNING : we only deal with proximity of the form |a-b| < A. prove this correctness
 */
public class ProximityConjunctiveJoinIterator extends ConjunctiveJoinIterator
		implements DocPositionIterator {

	protected double lowerBound, upperBound;
	
	public ProximityConjunctiveJoinIterator (
			DocPositionIterator iter1, 
			DocPositionIterator iter2, 
			double lower, double upper) throws IOException {
		super(Arrays.asList(new DocPositionIterator[]{iter1, iter2}));
		lowerBound = lower;
		upperBound = upper;
	}
	
	public boolean next() throws IOException {
		while (super.next()){
			bInitializePositionHead = false;
			if (nextPosition()) return true;
		}
		return false;
	}
	
	@Override
	public boolean skipTo(int docID) throws IOException {
		while (super.skipTo(docID)){
			bInitializePositionHead = false;
			if (nextPosition()) return true;
		}
		return false;
	}
	
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	private PriorityQueue<IdValuePairComparator> nextPositions; 
	private int currentPosition;
	private boolean bInitializePositionHead;

	public boolean nextPosition() throws IOException {
		if (!bInitializePositionHead)
			initializePositionHeap();
		do {
			if (nextPositions.size() == 0){
				for (int j = 0; j < N; j++) {
					if (((DocPositionIterator)iteratorList[j]).nextPosition()){
						nextPositions.add(new IdValuePairComparator(j,((DocPositionIterator)iteratorList[j]).getPosition() ));
					}
					else{
						nextPositions.clear();
						return false;
					}
				}
			}
			IdValuePairComparator current = nextPositions.poll();
			currentPosition = current.getValue();
			int maxPosition = currentPosition;
			for (IdValuePairComparator item : nextPositions) {
				if (item.getValue()> maxPosition) maxPosition = item.getValue();
			}
			
			//TODO : prove this correctness
			int diff = currentPosition - maxPosition;
			double skipToPos = lowerBound + maxPosition; //if first <= second
			if (current.getListID() == 1){ //if second one is the min, reverse the (diff = second - first) 
				diff = -diff;
				skipToPos = maxPosition - upperBound;
			}
			if (diff >= lowerBound && diff <= upperBound){ //proximity condition is satisfied.
				/* WARNING : we force all iter to move forward --> the matching will be non-overlapping
				 * If we want overlapping one, we can just comment out the following line. 
				 */
				nextPositions.clear(); 
				return true;
			}
			else{				
				//we move the smaller one.
				int lid = current.getListID();
				if (((DocPositionIterator)iteratorList[lid]).skipToPosition((int)skipToPos )){
					nextPositions.add(new IdValuePairComparator(lid,((DocPositionIterator)iteratorList[lid]).getPosition()));
				}
				else
					return false;
			}
			
		} while (true);
	}

	public int getPosition() throws IOException {
		return currentPosition;
	}

	public boolean skipToPosition(int nextPos) throws IOException {
		throw new RuntimeException("Not implemented yet");
	}
	
	/** this must be called after next() returns true
	 * @throws IOException
	 */
	private void initializePositionHeap() throws IOException {
		nextPositions = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
		bInitializePositionHead = true;
		currentPosition = -1;
		for (int j = 0; j < N; j++) {
			nextPositions.add(new IdValuePairComparator(j,((DocPositionIterator)iteratorList[j]).getPosition() ) );
		}
	}

}
