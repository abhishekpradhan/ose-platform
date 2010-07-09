package ose.processor.splock;

import java.io.IOException;

import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.DocFeature;
import ose.processor.QueryParser;
import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.processor.cascader.DocPositionIterator;

public class LogisticNode extends ProcessingNode {
	protected String modelName;
	protected TreeNode child;
	protected boolean bSpanAvailable;
	
	public LogisticNode() {
		super("#Logistic");
	}
	
	@Override
	public TreeNode parse(ParsingNode node, QueryParser parser) {
		if (node instanceof ParentNode) {
//			nodeName = node.getNodeName();
			ParentNode parent = (ParentNode) node;
			children.clear();
			if (parent.getChildren().size() != 2)
				throw new RuntimeException("TopNode expects 2 parameters, " + parent.getChildren().size() + " given.");
			
			ParsingNode modelNode = parent.getChild(0);
			if (modelNode instanceof LiteralNode ){
				modelName = ((LiteralNode) modelNode).getValue();
			}
			child = parser.parse(parent.getChild(1));
			child.addParent(this);
			children.add(child);
			return this;
		}
		else{
			throw new RuntimeException("Don't know how to parse " + node);
		}
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		status = child.nextDoc();
		bSpanAvailable = status.isAvailable();
		return status;
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		if (bSpanAvailable){
			DocFeature fSpan = new DocFeature(status.docId);
			Span childSpan = child.getCurrentSpan().clone();
			fSpan.setFeatureValue( getLogRegressedValue(childSpan ) );
			fSpan.setFeatureSpan(childSpan );
			currentSpan = fSpan;
			bSpanAvailable = false; //only allow calling this once
			return true;
		}
		else {
			currentSpan = null;
			return false;
		}
	}
	
	private double getLogRegressedValue(Span compositeSpan) {
		//TODO
		return 0.0;
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		status = child.skipTo(target);
		bSpanAvailable = status.isAvailable();
		return status;
	}
	
	public SpanStatus lazySkipTo(int docID) throws IOException {
		throw new RuntimeException("Not implemented yet");
	}
}
