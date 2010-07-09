package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ose.index.IdValuePairComparator;

public class DisjunctiveJoinIterator implements DocIterator{
	static public long PROF_countSkipTo = 0;
	protected DocIterator [] iteratorList;
	protected int N;
	
	private PriorityQueue<IdValuePairComparator> nextDocIDs; 
	
	/* this list stores the list of iterators that currently expired (need to be called next()) */
	protected ArrayList<Integer> toAdvanceList;
	protected int currentDocID;
	private int currentListID;
	
	public DisjunctiveJoinIterator(List<? extends DocIterator> iterators) throws IOException{
		N = iterators.size();
		iteratorList = iterators.toArray(new DocIterator[]{});
		toAdvanceList = new ArrayList<Integer>();
		
		nextDocIDs = new PriorityQueue<IdValuePairComparator>(N, new IdValuePairComparator());
		for (int j = 0; j < N; j++) {
			toAdvanceList.add(j);
		}
	}
	
	public String getClue() {
		StringBuffer buffer = new StringBuffer();
		for (Integer lid : toAdvanceList) {
			buffer.append(lid + "-");
			buffer.append( iteratorList[lid].getClue() );
			buffer.append('+');
		}
		return buffer.toString();
	}

	public int getDocID() {
		return currentDocID;
	}

	public boolean next() throws IOException {
		return myNext();
	}

	/**
	 * This is to prevent overiden by method from derived class
	 * @return
	 * @throws IOException
	 */
	private boolean myNext() throws IOException {
		advanceTheList();
		
		if (nextDocIDs.size() == 0){
			currentDocID = -1;
			return false;
		}
		else{
			return hasNextInTheHeap();
		}
	}

	/**
	 * @return
	 */
	private boolean hasNextInTheHeap() {
		IdValuePairComparator nextID = nextDocIDs.poll();
		currentListID = nextID.getListID();
		currentDocID = nextID.getValue();
		//check all of them share this doc, remove from the heap
		
		toAdvanceList.add(currentListID);
		
		while ( nextDocIDs.size() > 0 ){
			IdValuePairComparator nextPair = nextDocIDs.peek();
			if ( nextPair.getValue() == currentDocID){
				nextDocIDs.poll();
				toAdvanceList.add(nextPair.getListID());
			}
			else 
				break;
		}
		
		return true;
	}
	
	public boolean skipToSlow(int target) throws IOException {
		PROF_countSkipTo += 1;
		do {
			if (!myNext()) return false;
		} while (getDocID() < target);
		return true;
	}
	
	public boolean skipTo(int target) throws IOException{
		return skipToFast(target);
	}
	
	//new and improved skip to.
	public boolean skipToFast(int target) throws IOException {
		PROF_countSkipTo += 1;

		
		if (target <= getDocID())
			return myNext();
		
		
		if (nextDocIDs.size() == 0){ //the first time, never called next() 
			for (int j = 0; j < N; j++){
				if (iteratorList[j] != null && iteratorList[j].skipTo(target)){
					nextDocIDs.add(new IdValuePairComparator(j,iteratorList[j].getDocID()) );
				}
				else
					iteratorList[j] = null;
				toAdvanceList.clear();
			}	
		}
		else {
			/* this is a safer way to skip */ 
			List<IdValuePairComparator> toSkipToList = new ArrayList<IdValuePairComparator>();
			List<Integer> toSkipToDocIdList = new ArrayList<Integer>();
			for (IdValuePairComparator	docIdPair: nextDocIDs) {
				if (docIdPair.getValue() < target){
					toSkipToList.add(docIdPair);
					toSkipToDocIdList.add(docIdPair.getListID());
				}
			}
			
			nextDocIDs.removeAll(toSkipToList);
			toAdvanceList.removeAll(toSkipToDocIdList);
		
			
			for (IdValuePairComparator docIdPair : toSkipToList){
				int j = docIdPair.getListID();
				if (iteratorList[j] != null && iteratorList[j].skipTo(target)){
					nextDocIDs.add(new IdValuePairComparator(j,iteratorList[j].getDocID()) );
				}
				else
					iteratorList[j] = null;
			}
		}
		
		return myNext();
		
	}

	private void advanceTheList() throws IOException{
		/* push next DocId back to the list. */
		for(Integer lid : toAdvanceList ){
			if (iteratorList[lid].next()){
				nextDocIDs.add(new IdValuePairComparator(lid,iteratorList[lid].getDocID()) );
			}
			else
				iteratorList[lid] = null;
		}
		toAdvanceList.clear();
	}
	
	
}
