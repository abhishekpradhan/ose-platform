package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.index.IndexReader;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

/*
 * This node is used to connect a ParentNode and ChildNode. By de-coupling this, we can know the "caller" of nextDoc(), nextSpan()... methods
 * The child node is often a ShareNode, who needs to know the calling parent node.  
 *  
 */
public class ConnectorNode extends ProcessingNode{
	private static final String DEBUGGING_CHILD = "Share(Select[563.83..1597.94](Leaf : Token(_number)))";
	private TreeNode theCaller;
	private ShareSkipToNode theChild;
	static private boolean bDebug = false;
	
	public ConnectorNode(TreeNode parent, ShareSkipToNode child){
		super("Connector");
		theCaller = parent;
		children.add(child);
		theChild = child;
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
//		if (bDebug && theChild.toString().equals(DEBUGGING_CHILD))
//			System.out.println("----- " + this.hashCode() + " calling nextDoc() from " +  theCaller);
		status = theChild.nextDoc(theCaller);
		
//		if (bDebug && theChild.toString().equals(DEBUGGING_CHILD))
//			System.out.println("\t" + this.hashCode() + " got " + status + " --- " + currentSpan);
		return status;
	}
	
	@Override
	public boolean nextSpan() throws IOException {
//		if (bDebug)
//			System.out.println(this.hashCode() + " calling nextSpan() " );
		if (theChild.nextSpan(theCaller)){
			currentSpan = theChild.getCurrentSpan(theCaller);
//			if (bDebug)
//			System.out.println("\t" + this.hashCode() + " got " + hasSpan + " " + currentSpan);
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
//		if (bDebug && theChild.toString().equals(DEBUGGING_CHILD))
//			System.out.println(this.hashCode() + " calling skipTo( " + target  + ") from " +  theCaller );
		status = theChild.skipTo(target, theCaller);
		
//		if (bDebug && theChild.toString().equals(DEBUGGING_CHILD))
//			System.out.println("\t" + this.hashCode() + " got " + status + " --- " + currentSpan);
		return status;
	}
	
	public SpanStatus lazySkipTo(int target) throws IOException {
		status = theChild.lazySkipTo(target, theCaller);
		
//		if (bDebug && theChild.toString().equals(DEBUGGING_CHILD))
//			System.out.println("\t" + this.hashCode() + " got " + status + " --- " + currentSpan);
		return status;
	}
	
	@Override
	public Span getCurrentSpan() {
		currentSpan = theChild.getCurrentSpan(theCaller);
		return currentSpan ;
	}
}
