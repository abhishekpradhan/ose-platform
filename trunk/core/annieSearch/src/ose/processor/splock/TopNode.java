package ose.processor.splock;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.QueryParser;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public class TopNode extends ProcessingNode {
	protected int topK;
	protected TreeNode child;
	protected int currentPos;
	
	public TopNode() {
		super("Top");
	}
	
	@Override
	public TreeNode parse(ParsingNode node, QueryParser parser) {
		if (node instanceof ParentNode) {
//			nodeName = node.getNodeName();
			ParentNode parent = (ParentNode) node;
			children.clear();
			if (parent.getChildren().size() != 2)
				throw new RuntimeException("TopNode expects 2 parameters, " + parent.getChildren().size() + " given.");
			child = parser.parse(parent.getChild(0));
			child.addParent(this);
			children.add(child);
			ParsingNode kNode = parent.getChild(1);
			if (kNode instanceof LiteralNode ){
				topK = ((LiteralNode) kNode).getNumber().intValue();
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
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		currentPos = 1; //since getPosition is called before nextPosition. 
		status = child.nextDoc();
		if (status.isAvailable()){
			currentSpan = child.getCurrentSpan().clone();
		}
		else{
			currentSpan = null;
		}
			
		return status;
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		if (currentPos < topK){
			currentPos += 1;
			if ( child.nextSpan() ){
				currentSpan = child.getCurrentSpan().clone();
				return true;
			}
			else{
				currentSpan = null;
				return false;
			}
		}
		else
			return false;
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		currentPos = 1; 
		status = child.skipTo(target); 
		if (status.isAvailable()){
			currentSpan = child.getCurrentSpan().clone();
		}
		else{
			currentSpan = null;
		}
		return status;
	}
	
	public SpanStatus lazySkipTo(int docID) throws IOException {
		throw new RuntimeException("Not implemented yet");
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		child = children.get(0);
	}
}
