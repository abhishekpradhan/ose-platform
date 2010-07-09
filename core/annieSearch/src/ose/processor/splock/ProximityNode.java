package ose.processor.splock;

import java.io.IOException;

import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.QueryParser;
import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public class ProximityNode extends ConjunctiveNode {
	protected int lower, upper;
	private int currentPosition;
	
	public ProximityNode() {
		super("Proximity");
	}
	
	@Override
	public TreeNode parse(ParsingNode node, QueryParser parser) {
		if (node instanceof ParentNode) {
//			nodeName = node.getNodeName();
			ParentNode parent = (ParentNode) node;
			children.clear();
			if (parent.getChildren().size() != 4)
				throw new RuntimeException("ProximityNode expects 4 parameters, " + parent.getChildren().size() + " given.");
			for (int i = 0 ; i < 2 ; i ++){
				TreeNode child = parser.parse(parent.getChild(i));
				child.addParent(this);
				children.add(child);
			}
			ParsingNode lowerNode = parent.getChild(2);
			ParsingNode upperNode = parent.getChild(3);
			if (lowerNode instanceof LiteralNode && upperNode instanceof LiteralNode) {
				lower = ((LiteralNode) lowerNode).getNumber().intValue();
				upper = ((LiteralNode) upperNode).getNumber().intValue();
			}
			else {
				throw new RuntimeException("ProximityNode numeric numbers for param 3 & 4, ");
			}
			return this;
		}
		else{
			throw new RuntimeException("Don't know how to parse " + node);
		}
	}
	
	//Copied from PhraseNode
	@Override
	public SpanStatus nextDoc() throws IOException {
		while (true){
			status = super.nextDoc();
			if (status.isAvailable()){
				currentPosition = -1;
				if (nextSpan(false)){
					return status;
				}
			}
			else
				return status;
		}
	}
	
	//Copied from PhraseNode
	@Override
	public boolean nextSpan() throws IOException {
		return nextSpan(true);
	}
	
	//Copied from PhraseNode
	
	public boolean nextSpan(boolean needToAdvance) throws IOException {
		
		currentSpan = null;
		TreeNode firstChild = children.get(0);
		TreeNode secondChild = children.get(1);
		
		if (needToAdvance){
			currentPosition += 1;
			if (firstChild.getCurrentSpan() != null && firstChild.getCurrentSpan().startPos + 1> currentPosition)
				currentPosition = firstChild.getCurrentSpan().startPos + 1;
			if (secondChild.getCurrentSpan() != null && secondChild.getCurrentSpan().startPos + 1> currentPosition)
				currentPosition = secondChild.getCurrentSpan().startPos + 1;
		}
		
		if (! skipToPosition(firstChild, currentPosition) ) 
			return false;
		if (! skipToPosition(secondChild, currentPosition) ) 
			return false;
		
		while (true){
			int posA = firstChild.getCurrentSpan().startPos;
			int posB = secondChild.getCurrentSpan().startPos;
			currentPosition = Math.min(posA, posB);
			if (posA - posB >= lower && posA - posB <= upper){
				setCurrentSpan(posA,posB);
				return true;
			}
			//advance either A or B so that the constraint is satisfied 
			else if (posA - posB < lower){
				// --> posA < lower + posB --> move A 
				if (!skipToPosition(firstChild, lower + posB))
					return false;
			}
			else if (posA - posB > upper){ // --> posB < posA - upper --> move B
				if (!skipToPosition(secondChild, posA - upper)) 
					return false;
			}
			else {
				throw new RuntimeException("impossible proximity constraint");
			}
		}
	}

	/**
	 * @param firstChild
	 * @throws IOException
	 */
	private boolean skipToPosition(TreeNode firstChild, int currentPosition) throws IOException {
		while (true){
			if (firstChild.getCurrentSpan() == null)
				return false;
			else if (firstChild.getCurrentSpan().startPos < currentPosition){
				if (!firstChild.nextSpan())
					return false;
			}
			else 
				return true;
		}
		
	}
	
	private void setCurrentSpan(int posA, int posB) {
		currentSpan = new Span(status.docId, Math.min(posA, posB), Math.max(posA, posB));
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		status = super.skipTo(target);
		if (status.isAvailable()){
			currentPosition = -1;
			if (nextSpan(false)){
				return status;
			}
			status = nextDoc();
		}
		
		return status;
	}
}
