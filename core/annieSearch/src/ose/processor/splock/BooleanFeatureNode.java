package ose.processor.splock;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.DocFeature;
import ose.processor.QueryParser;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public class BooleanFeatureNode extends ProcessingNode {
	protected TreeNode child;
	
	public BooleanFeatureNode() {
		super("%BooleanFeature");
	}
	
	@Override
	public TreeNode parse(ParsingNode node, QueryParser parser) {
		if (node instanceof ParentNode) {
//			nodeName = node.getNodeName();
			ParentNode parent = (ParentNode) node;
			children.clear();
			if (parent.getChildren().size() != 1)
				throw new RuntimeException("BooleanFeatureNode expects 1 parameter, " + parent.getChildren().size() + " given.");
			child = parser.parse(parent.getChild(0));
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
		if (status.isAvailable()){
			DocFeature fSpan = new DocFeature(status.docId);
			fSpan.setFeatureValue(1);
			fSpan.setFeatureSpan(child.getCurrentSpan().clone());
			currentSpan = fSpan;
			return status;
		}
		return status;
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		currentSpan = null; //never return more than one span
		return false;
	}
	
	@Override
	public SpanStatus skipTo(int target) throws IOException {
		status = child.skipTo(target);
		if (status.isAvailable()){
			DocFeature fSpan = new DocFeature(status.docId);
			fSpan.setFeatureValue(1);
			fSpan.setFeatureSpan(child.getCurrentSpan().clone());
			currentSpan = fSpan;
			return status;
		}
		return status;
	}
		
	public SpanStatus lazySkipTo(int docID) throws IOException {
		status = child.lazySkipTo(docID);
		if (status.isAvailable()){
			DocFeature fSpan = new DocFeature(status.docId);
			fSpan.setFeatureValue(1);
			fSpan.setFeatureSpan(child.getCurrentSpan().clone());
			currentSpan = fSpan;
			return status;
		}
		return status;
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		super.initialize(reader);
		child = children.get(0);
	}
}
