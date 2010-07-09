package ose.processor.cascader;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import ose.index.IdValuePairComparator;


public class ConjunctiveJoinIterator implements DocIterator {

	protected DocIterator [] iteratorList;
	protected int N;
	
	protected PriorityQueue<IdValuePairComparator> nextDocIDs; 
	protected int currentDocID;
	
	protected ConjunctiveJoinIterator(){
		
	}
	
	public ConjunctiveJoinIterator(List<? extends DocIterator> iterators) throws IOException{
		N = iterators.size();
		iteratorList = iterators.toArray(new DocIterator[]{});
		nextDocIDs = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
	}
	
	public String getClue() {
		StringBuffer buffer = new StringBuffer();
		for (DocIterator iterator : iteratorList) {
			buffer.append(iterator.getClue() );
			buffer.append(',');
		}
		return buffer.toString();
	}

	public int getDocID() {
		return currentDocID;
	}

	public boolean next() throws IOException {
		if (nextDocIDs.size() != 0){ //meaning some iterator has finished, but some left.
			currentDocID = -1;
			return false;
		}
		else{
			
			moveAllNext();
			
			return findAMergingPoint();
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private boolean findAMergingPoint() throws IOException {
		while (nextDocIDs.size() == N){ //loop until all nextDocID are the same
			IdValuePairComparator current = nextDocIDs.poll();
			currentDocID = current.getValue();
			int maxDocID = currentDocID;
			for (IdValuePairComparator item : nextDocIDs) {
				if (item.getValue()> maxDocID) maxDocID = item.getValue();
			}
			
			if (maxDocID == currentDocID){ //all docIDs are the same
				nextDocIDs.clear(); //clear all sothat we know to call next() next time.
				return true; 
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
	
	/**
	 * 
	 * Precondition : all docId in iteratorList are the same. 
	 */
	public boolean skipTo(int docID) throws IOException {
		if (nextDocIDs.size() == 0){ //this is called everytime???
			for (int j = 0; j < N; j++){
				if (iteratorList[j].skipTo(docID)){
					nextDocIDs.add(new IdValuePairComparator(j,iteratorList[j].getDocID()) );
				}
			}	
		}
		else{
			/* this is a safer way to skip */ 
			List<IdValuePairComparator> toSkipToList = new ArrayList<IdValuePairComparator>();
			for (IdValuePairComparator	docIdPair: nextDocIDs) {
				if (docIdPair.getValue() < docID){
					toSkipToList.add(docIdPair);
				}
			}
			if (toSkipToList.size() == 0)
				toSkipToList.addAll(nextDocIDs);
			
			nextDocIDs.removeAll(toSkipToList);
			for (IdValuePairComparator docIdPair : toSkipToList){
				int j = docIdPair.getListID();
				if (iteratorList[j].skipTo(docID)){
					nextDocIDs.add(new IdValuePairComparator(j,iteratorList[j].getDocID()) );
				}
			}
		}
		return findAMergingPoint();
	}
	
	/**
	 * @throws IOException
	 */
	protected void moveAllNext() throws IOException {
		for (int j = 0; j < N; j++) {
			if (iteratorList[j].next()){ /* this list is not empty */
				nextDocIDs.add(new IdValuePairComparator(j,iteratorList[j].getDocID()) );
			}
		}
	}

}
