/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;

import ose.index.IdValuePairComparator;

/**
 * @author Pham Kim Cuong
 * TODO : implement DocPositionIterator interface
 */
public class ConsecutiveConjunctiveJoinIterator extends ConjunctiveJoinIterator implements DocPositionIterator{
	
	public ConsecutiveConjunctiveJoinIterator(List<DocPositionIterator> iterators) throws IOException{
		N = iterators.size();		
		iteratorList = iterators.toArray(new DocIterator[]{});
		nextDocIDs = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
		bInitializePositionHead = false;
	}
	
	public boolean next() throws IOException {
		bInitializePositionHead = false;
		if (nextDocIDs.size() != 0){ //meaning some iterator has finished, but some left.
			currentDocID = -1;
			return false;
		}
		else{
			
			moveAllNext();
			
			while (nextDocIDs.size() == N){ //loop until all nextDocID are the same
				IdValuePairComparator current = nextDocIDs.poll();
				currentDocID = current.getValue();
				int maxDocID = currentDocID;
				for (IdValuePairComparator item : nextDocIDs) {
					if (item.getValue()> maxDocID) maxDocID = item.getValue();
				}
				
				if (maxDocID == currentDocID){ //all docIDs are the same
					bInitializePositionHead = false;
					if (nextPosition()){ 
						nextDocIDs.clear(); //clear all sothat we know to call next() next time.
						return true; //found a position that forms a phrase
					} 
					else { //otherwise, just keep looking
						nextDocIDs.clear();
						moveAllNext();
					}
				}
				else{
					nextDocIDs.add(current); //push it back to move forward.
					while (nextDocIDs.peek().getValue() < maxDocID) {
						current = nextDocIDs.poll();
						int lid = current.getListID();
						if (iteratorList[lid].skipTo(maxDocID)){
							nextDocIDs.add(new IdValuePairComparator(lid,iteratorList[lid].getDocID()) );
						}
						else
							return false;
					}
				}
			}
			return false;
		}
	}

	@Override
	public boolean skipTo(int docID) throws IOException {
		while (super.skipTo(docID)){
			bInitializePositionHead = false;
			if (nextPosition())
				return true;
		}
		return false;
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
						nextPositions.add(new IdValuePairComparator(j,((DocPositionIterator)iteratorList[j]).getPosition() - j));
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
			
			if (maxPosition == currentPosition){ //all docIDs are the same
				nextPositions.clear();
				return true;
			}
			else{
				nextPositions.add(current); //push this one back for moving forward.
				while (nextPositions.peek().getValue() < maxPosition) {
					current = nextPositions.poll();
					int lid = current.getListID();
					if (((DocPositionIterator)iteratorList[lid]).skipToPosition(maxPosition + lid)){
						nextPositions.add(new IdValuePairComparator(lid,((DocPositionIterator)iteratorList[lid]).getPosition()-lid));
					}
					else
						return false;
				}
			}
			
		} while (true);
	}

	public int getPosition() throws IOException {
		return currentPosition;
	}

	public boolean skipToPosition(int nextPos) throws IOException {
		if (!bInitializePositionHead){
			nextPositions = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
			bInitializePositionHead = true;
			currentPosition = -1;
		}
		nextPositions.clear();
		for (int j = 0; j < N; j++) {
			if (((DocPositionIterator)iteratorList[j]).skipToPosition(nextPos + j)){
				nextPositions.add(new IdValuePairComparator(j,((DocPositionIterator)iteratorList[j]).getPosition() - j));
			}
			else
				return false;
		}
		
		return nextPosition();
	}
	
	public int getFrequency() {
		int minFreq = 1000000000;
		for (int j = 0; j < N; j++) {
			minFreq = Math.min(minFreq, ((DocPositionIterator)iteratorList[j]).getFrequency() );
		}
		return minFreq;
	}
	
	/** this must be called after next() returns true
	 * @throws IOException
	 */
	private void initializePositionHeap() throws IOException {
		nextPositions = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
		bInitializePositionHead = true;
		currentPosition = -1;
		for (int j = 0; j < N; j++) {
			nextPositions.add(new IdValuePairComparator(j,((DocPositionIterator)iteratorList[j]).getPosition() - j));
		}
	}
	
}
