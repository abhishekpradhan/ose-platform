package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;

import ose.processor.ShareIterator;
import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.utils.CommonUtils;


public class ShareSkipToNode extends ProcessingNode implements ShareIterator{

	private List<TreeNode> grandParents; //because ShareSkipNode is accessed via a ConnectorNode, its caller are grandParents nodes
	
	
	//span buffer and the position in the buffer that requester is upto
	private int [] requestedNextSpans;
	private List<Span> spanBuffer;
	
	//map caller to a number
	private Map<TreeNode, Integer> callerToLIDMap;
	
	//keep track of what requester is upto
	private RequestScheduler reqScheduler;
	
	private TreeNode theChild;
	
	public ShareSkipToNode(TreeNode sharedNode) {
		super("Share");
		children.add(sharedNode);
	}
	
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		
		if (children.size() != 1)
			throw new RuntimeException("this node should have only one child but instead had " + children.size());
		theChild = children.get(0);
		
		grandParents = new ArrayList<TreeNode>();
		for (TreeNode parent : parents){
			grandParents.addAll(parent.getParents());
		}
		
		callerToLIDMap = new HashMap<TreeNode, Integer>();
		int k = 0;
		for (TreeNode caller : grandParents) {
			callerToLIDMap.put(caller, k);
			k += 1;
		}
		
		reqScheduler = new RequestScheduler(grandParents.size());
		for (int i = 0; i < grandParents.size(); i++) {
			reqScheduler.addRequestStatus(i, SpanStatus.onhold(0));
		}
		reqScheduler.initializeHeap();
		
		requestedNextSpans = new int [ grandParents.size() ];
		spanBuffer = new ArrayList<Span>();
		
	}
	
	/*
	 * allow if caller holds the smallest request 
	 */
	public SpanStatus nextDoc(TreeNode caller) throws IOException {
		
		Integer callerLid = callerToLIDMap.get(caller);
		if (callerLid == null){
			throw new RuntimeException("Unknown caller : " + caller);
		}

		SpanStatus callerStatus = reqScheduler.getRequesterStatus(callerLid);
		
		if (callerStatus.isOnHold()){
			if (callerStatus.docId <= status.docId && status.isAvailable()){ //already there, current span can be taken from bufferSpan
				reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_AVAILABLE);
				return callerStatus.clone();
			}
			else if (callerStatus.docId >= status.docId) {
				if (callerStatus.equals(reqScheduler.getSmallest())){
					return moveTheChild(callerStatus, callerLid, theChild.nextDoc());
				}
				else {
					return SpanStatus.onhold(callerStatus.docId);
				}
			}
			else if (callerStatus.docId < status.docId){
				reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_ON_HOLD);
				return callerStatus.clone();
			}
		}
		else if (callerStatus.isAvailable()){
			if (status.isAvailable() && status.docId == callerStatus.docId){
				if (reqScheduler.isTheSmallest(callerLid) && reqScheduler.isOnlyOneSmallest()){
					return moveTheChild(callerStatus, callerLid, theChild.nextDoc());
				}
				else{
					reqScheduler.updateStatus(callerLid, callerStatus.docId, SpanStatus.STATUS_NO_MORE_SPAN);
					return SpanStatus.onhold(callerStatus.docId + 1); //waiting on the nest one. 
				}
			}
		}
		else if (callerStatus.isNoMoreSpan()){
			if (callerStatus.docId < status.docId && status.isAvailable()){ //already there, current span can be taken from bufferSpan
				reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_AVAILABLE);
				return callerStatus.clone();
			}
			else {
				if (callerStatus.equals(reqScheduler.getSmallest())){
					return moveTheChild(callerStatus, callerLid, theChild.nextDoc());
				}
				else{
					return SpanStatus.onhold(callerStatus.docId + 1); //waiting on the nest one. 
				}
			}
		}
		else if (callerStatus.isDone()){
			return callerStatus.clone();
		}
		printDebug(1);
		throw new RuntimeException("Unhandled case");
	}

	/**
	 * @param callerStatus
	 * @return
	 * @throws IOException
	 */
	private SpanStatus moveTheChild(SpanStatus callerStatus, int callerLid, SpanStatus responseFromChild) throws IOException {
		status = responseFromChild;
		
		if (status.isAvailable()){
			resetSpanBuffer();
			currentSpan = theChild.getCurrentSpan().clone();
			spanBuffer.add(currentSpan);
			reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_AVAILABLE);
		}
		else {
			currentSpan = null;
			if (status.docId > callerStatus.docId){
				reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_ON_HOLD);
			}
			else {
				reqScheduler.updateStatus(callerLid, callerStatus.docId, SpanStatus.STATUS_ON_HOLD);
			}
		}
		return status.clone();
	}
	
	public SpanStatus skipTo(int target, TreeNode caller) throws IOException {
		return skipTo(target, caller, false);
	}
	
	public SpanStatus lazySkipTo(int target, TreeNode caller) throws IOException {
		return skipTo(target, caller, true);
	}
	
	private SpanStatus skipTo(int target, TreeNode caller, boolean lazy) throws IOException {
//		System.out.println(" ---> " + target + "   " + caller);
		Integer callerLid = callerToLIDMap.get(caller);
		if (callerLid == null){
			throw new RuntimeException("Unknown caller : " + caller);
		}

		SpanStatus callerStatus = reqScheduler.getRequesterStatus(callerLid);
		
		if (target < callerStatus.docId){
			throw new RuntimeException("skipTo target < last request");
		}
		
		if (target <= status.docId){
			if (status.isAvailable()){ //already there, current span can be taken from bufferSpan
				reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_AVAILABLE);
				return callerStatus.clone();
			}
			else if (status.isOnHold()){
				if (callerStatus.equals(reqScheduler.getSmallest() )){
					if (lazy)
						return moveTheChild(callerStatus, callerLid, theChild.lazySkipTo(target));
					else
						return moveTheChild(callerStatus, callerLid, theChild.nextDoc());
				}
				else {
					if (callerStatus.docId < status.docId){
						reqScheduler.updateStatus(callerLid, status.docId, SpanStatus.STATUS_ON_HOLD);
					}
					else{
						reqScheduler.updateStatus(callerLid, callerStatus.docId, SpanStatus.STATUS_ON_HOLD);
					}
					return callerStatus.clone();
				}
			}
			else {
				reqScheduler.updateStatus(callerLid, status.docId, status.getStatus());
				return status.clone();
			}
		}
		else {
			if (callerStatus.equals(reqScheduler.getSmallest()) && target <= reqScheduler.getSecondSmallest().docId ){
//				reqScheduler.updateStatus(callerLid, target, callerStatus.getStatus());
				if (lazy)
					return moveTheChild(callerStatus, callerLid, theChild.lazySkipTo(target));
				else
					return moveTheChild(callerStatus, callerLid, theChild.skipTo(target));
			}
			else {
				reqScheduler.updateStatus(callerLid, target, SpanStatus.STATUS_ON_HOLD);
				return callerStatus.clone();
			}
		}
//		throw new RuntimeException("Unhandled case");
	}
	
	
	
	public boolean nextSpan(TreeNode caller) throws IOException {
		if (status.isAvailable()){
			
			Integer callerLid = callerToLIDMap.get(caller);
			if (callerLid == null){
				throw new RuntimeException("Unknown caller : " + caller);
			}

			requestedNextSpans[callerLid] += 1;
			if (requestedNextSpans[callerLid] > spanBuffer.size() ){
				return false;
			}
			else if (requestedNextSpans[callerLid] == spanBuffer.size() ){//if this is the leading span
				boolean hasSpan = theChild.nextSpan();
				if (hasSpan){
					currentSpan = theChild.getCurrentSpan().clone();
					spanBuffer.add(currentSpan);
					return true;
				} else {
					currentSpan = null;
					return false;
				}
			}
			else { //just get it from the buffer
				return true;
			}
		}
		else
			return false;
	};
	
	public Span getCurrentSpan(TreeNode caller) {
		Integer callerLid = callerToLIDMap.get(caller);
		if (callerLid == null){
			throw new RuntimeException("Unknown caller : " + caller);
		}

		if (reqScheduler.getRequesterStatus(callerLid).isAvailable()){
			if (requestedNextSpans[callerLid] >= spanBuffer.size()){
				return null; //finished
			}
			else{
				return spanBuffer.get(requestedNextSpans[callerLid]);
			}
		}
		else 
			return null; //not available
	}
	
	public void printDebug(int level) {
		System.out.println( CommonUtils.tabString(level) + "status = " + status + " -- " + this.hashCode());
		for (int i = 0 ; i < grandParents.size() ; i ++){
			TreeNode grandPap = grandParents.get(i);
			System.out.println( CommonUtils.tabString(level) + "---" + grandPap + " : " + reqScheduler.getRequesterStatus(i) + " , " + requestedNextSpans[i]);
		}
	}
	
	private void resetSpanBuffer() {
		spanBuffer.clear();
		for (int i = 0; i < requestedNextSpans.length; i++) {
			requestedNextSpans[i] = 0;
		}
	}
}

class RequestScheduler {
	private int numberOfRequesters;
	private long [] heapValues;
	private int [] ridToHeapIdMap;
	private int [] heapIdToRID ;
	private SpanStatus[] requesterStatuses;
	
	public RequestScheduler(int numberOfRequesters) {
		this.numberOfRequesters = numberOfRequesters;
		requesterStatuses = new SpanStatus[numberOfRequesters];
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
			if (heapValues[k] < heapValues[parentK]){
				swap(k,parentK);
			}
			bubbleHeapAtPosition(parentK);
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
			if (heapValues[k] > heapValues[childK1]){
				swap(k,childK1);
			}
		}
		else {
			int childK1 = 2 * k + 1;
			int childK2 = 2 * k + 2;
			if (heapValues[childK1] < heapValues[k] && heapValues[childK1] <= heapValues[childK2]){
				swap(k,childK1);
				reverseBubbleHeapAtPosition(childK1);
			}
			else if (heapValues[childK2] < heapValues[k] && heapValues[childK2] <= heapValues[childK1]){
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
	
	public void updateStatus(int rid, int docId, int status){
		requesterStatuses[rid].docId = docId;
		requesterStatuses[rid].setStatus(status);
		int i = ridToHeapIdMap[rid];
		long oldValue = heapValues[i];
		heapValues[i] = getStatusScore(docId, status);
		if (heapValues[i] < oldValue)
			bubbleHeapAtPosition(i);
		else if (heapValues[i] > oldValue)
			reverseBubbleHeapAtPosition(i);
	}
	
	public void addRequestStatus(int rid, SpanStatus status){
		requesterStatuses[rid] = status;
	}
	
	public SpanStatus getSmallest(){
		return requesterStatuses[heapIdToRID[0]];
	}
	
	public SpanStatus getSecondSmallest(){
		if (numberOfRequesters <= 2 || heapValues[1] <= heapValues[2])
			return requesterStatuses[heapIdToRID[1]];
		else
			return requesterStatuses[heapIdToRID[2]];
	}
	
	public SpanStatus getRequesterStatus(int rid){
		return requesterStatuses[rid];
	}
	
	public boolean isOnlyOneSmallest(){
		return heapValues[0] < heapValues[1] && (numberOfRequesters <= 2 || heapValues[0] < heapValues[2]);
	}
	
	public boolean isTheSmallest(int rid){
		return ridToHeapIdMap[rid] == 0;
	}
	
	static public long getStatusScore(SpanStatus stat){
		return stat.docId * 1000L + stat.getStatus();
	}
	
	static public long getStatusScore(int docId, int status){
		return docId * 1000L + status;
	}
	
}
