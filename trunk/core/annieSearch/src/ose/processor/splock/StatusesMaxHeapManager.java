package ose.processor.splock;

import ose.processor.SpanStatus;

public class StatusesMaxHeapManager {
	private int numberOfRequesters;
	private long [] heapValues;
	private int [] ridToHeapIdMap;
	private int [] heapIdToRID ;
	private SpanStatus[] requesterStatuses;
	
	public StatusesMaxHeapManager(int numberOfRequesters) {
		this.numberOfRequesters = numberOfRequesters;
		requesterStatuses = new SpanStatus[numberOfRequesters];
	}
	
	public int getNumberOfRequesters() {
		return numberOfRequesters;
	}
	
	public void initializeHeap() {
		heapValues = new long[numberOfRequesters];
		heapIdToRID = new int[numberOfRequesters];
		ridToHeapIdMap = new int[numberOfRequesters];
		
		for (int i = 0; i < numberOfRequesters; i++) {
			heapIdToRID[i] = i;
			ridToHeapIdMap[i] = i;
			heapValues[i] = getStatusScore(requesterStatuses[i]);
		}
		makeHeap();
	}
	
	public void updateStatus(int rid, int docId, int status){
		requesterStatuses[rid].docId = docId;
		requesterStatuses[rid].setStatus(status);
		int i = ridToHeapIdMap[rid];
		long oldValue = heapValues[i];
		heapValues[i] = getStatusScore(docId, status);
		if (heapValues[i] > oldValue)
			bubbleHeapAtPosition(i);
		else if (heapValues[i] < oldValue)
			reverseBubbleHeapAtPosition(i);
	}
	
	public void addRequestStatus(int rid, SpanStatus status){
		requesterStatuses[rid] = status;
	}
	
	public SpanStatus getLargest(){
		return requesterStatuses[heapIdToRID[0]];
	}
	
	public SpanStatus getSecondLargest(){
		if (numberOfRequesters <= 2 || heapValues[1] >= heapValues[2])
			return requesterStatuses[heapIdToRID[1]];
		else
			return requesterStatuses[heapIdToRID[2]];
	}
	
	public SpanStatus getRequesterStatus(int rid){
		return requesterStatuses[rid];
	}
	
	public boolean isOnlyOneLargest(){
		return heapValues[0] > heapValues[1] && (numberOfRequesters <= 2 || heapValues[0] > heapValues[2]);
	}
	
	public boolean isTheLargest(int rid){
		return ridToHeapIdMap[rid] == 0;
	}
	
	public boolean allTheSame(){
		return allTheSame(0);
	}
	
	private boolean allTheSame(int k){
		int childK1 = 2 * k + 1;
		int childK2 = 2 * k + 2;
		if (childK1 < numberOfRequesters && heapValues[k] != heapValues[childK1])
			return false;
		if (childK2 < numberOfRequesters && heapValues[k] != heapValues[childK2])
			return false;
		if (childK1 < numberOfRequesters && !allTheSame(childK1))
			return false;
		if (childK2 < numberOfRequesters && !allTheSame(childK2))
			return false;
		return true;
	}
	
	private void makeHeap() {
		for (int i = 0; i < numberOfRequesters; i++) {
			bubbleHeapAtPosition(i);
		}
	}
	
	//heap mapping : 0->1,2 , 1->3,4, 2->5,6
	//(k+1)/2 - 1 ---> k --> 2k + 1, 2k + 2 
	private void bubbleHeapAtPosition(int k) {
		if (k == 0) { //stopping condition
			return;
		}
		else{
			int parentK = (k+1) / 2 - 1;
			if (heapValues[k] > heapValues[parentK]){
				swap(k,parentK);
				bubbleHeapAtPosition(parentK);
			}
			
		}
	}
	
	//heap mapping : 0->1,2 , 1->3,4, 2->5,6
	//(k+1)/2 - 1 ---> k --> 2k + 1, 2k + 2 
	private void reverseBubbleHeapAtPosition(int k) {
		if (2 * k + 1 >= numberOfRequesters) { //stopping condition
			return;
		}
		else if (2 * k + 2 >= numberOfRequesters){ //only one child is available
			int childK1 = 2 * k + 1;
			if (heapValues[k] < heapValues[childK1]){
				swap(k,childK1);
			}
		}
		else {
			int childK1 = 2 * k + 1;
			int childK2 = 2 * k + 2;
			if (heapValues[childK1] > heapValues[k] && heapValues[childK1] >= heapValues[childK2]){
				swap(k,childK1);
				reverseBubbleHeapAtPosition(childK1);
			}
			else if (heapValues[childK2] > heapValues[k] && heapValues[childK2] >= heapValues[childK1]){
				swap(k,childK2);
				reverseBubbleHeapAtPosition(childK2);
			} 
		}
	}
	
	private void swap(int i,int j) {
		long longTmp = heapValues[i]; heapValues[i] = heapValues[j]; heapValues[j] = longTmp;
		int u = heapIdToRID[i]; 
		int v = heapIdToRID[j];
		int intTmp = heapIdToRID[i]; heapIdToRID[i] = heapIdToRID[j]; heapIdToRID[j] = intTmp;
		intTmp = ridToHeapIdMap[u]; ridToHeapIdMap[u] = ridToHeapIdMap[v]; ridToHeapIdMap[v] = intTmp;
	}
	
	
	
	static public long getStatusScore(SpanStatus stat){
		return stat.docId * 1000L + stat.getStatus();
	}
	
	static public long getStatusScore(int docId, int status){
		return docId * 1000L + status;
	}
	
}
