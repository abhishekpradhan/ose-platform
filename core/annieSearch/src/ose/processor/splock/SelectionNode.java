package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.index.IndexReader;

import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.processor.cascader.Constraint;

public class SelectionNode extends ProcessingNode {

	protected Constraint postingConstraint; //constraint on each posting of an inverted list.
	protected TreeNode theChild = null;
	
	
	public SelectionNode() {
		super("Select");
	}
	
	public void setChild(TreeNode child) {
		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		children.add(child);
		super.setChildren(children);
	}
	
	public void setConstraint(Constraint c) {
		postingConstraint = c;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(nodeName + "[");
		buffer.append(postingConstraint.toString());
		buffer.append( "]");
		for (TreeNode child : children){
			buffer.append( "(");
			buffer.append(child);
			buffer.append( ")");
		}

		return buffer.toString();
	}
	
	@Override
	public boolean equalsNode(TreeNode node) {
		if (node == null)
			return false;
		if (node instanceof SelectionNode) {
			SelectionNode lNode = (SelectionNode) node;
			if (postingConstraint == null && lNode.postingConstraint != null)
				return false;
			if ( postingConstraint != null && !postingConstraint.equals(lNode.postingConstraint))
				return false;
			
			if (children.size() != lNode.children.size())
				return false;
			for (int i = 0 ; i < children.size() ; i ++){
				TreeNode child = children.get(i);
				if ( !child.equalsNode(lNode.children.get(i)) )
					return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException{
		super.initialize(reader);
		if (children.size() != 1)
			throw new RuntimeException("this node should have only one child but instead had " + children.size());
		theChild = children.get(0);
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException{
		while (true){
			status = theChild.nextDoc();
			if (status.isAvailable()){
				if (nextSpan(true))
					return status;
			}
			else
				return status;
		} 
	}
	
	@Override
	public boolean nextSpan() throws IOException{
		if (nextSpan(false)){
			return true;
		}
		else{
			currentSpan = null;
			return false;
		}
	}
	
	public boolean nextSpan(boolean theChildNextSpanCalled) throws IOException {
		if (!theChildNextSpanCalled){
			if (!theChild.nextSpan())
				return false;
		}
			
		do {
			if (postingConstraint.satisfy(theChild.getCurrentSpan().getAttribute(LuceneTermNode.ATTRIBUTE_NUMERIC))){
				currentSpan = theChild.getCurrentSpan().clone();
				return true;
			}
		} while (theChild.nextSpan());
		currentSpan = null;
		return false;
	}
	
	@Override
	public SpanStatus skipTo(int docID) throws IOException {
		status = theChild.skipTo(docID);
		if (status.isAvailable()){
			while (true){
				if (status.isAvailable()){
					if (nextSpan(true))
						return status;
				}
				else
					return status;
				status = nextDoc();
			}
		}
		return status;
	}
	
	public SpanStatus lazySkipTo(int docID) throws IOException {
		status = theChild.lazySkipTo(docID);
		if (status.isAvailable()){
			if (nextSpan(true))
				return status;
			else {
				status.setStatus(SpanStatus.STATUS_NO_MORE_SPAN);
				return status;
			}
		}
		else
			return status;
	}
}
