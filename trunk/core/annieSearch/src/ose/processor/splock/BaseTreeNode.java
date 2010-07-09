package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.parser.ParsingNode;
import ose.processor.QueryParser;
import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public abstract class BaseTreeNode implements TreeNode {
	protected List<TreeNode> parents;
	
	/*
	 * for derived classes.
	 */
	protected SpanStatus status;
	protected Span currentSpan;
	
	
	protected BaseTreeNode() {
		parents = new ArrayList<TreeNode>();
	}
	
	protected BaseTreeNode(TreeNode parent) {
		this();
		parents.add( parent );
	}
	
	public void addParent(TreeNode parent) {
		parents.add( parent );
	}
	
	public void removeParent(TreeNode parent) {
		parents.remove( parent );
	}
	
	public void clearParent() {
		parents.clear();
	}
	
	public List<TreeNode> getParents(){
		return parents;
	}
	
	
	
	abstract public TreeNode parse(ParsingNode node, QueryParser parser);
	abstract public boolean equalsNode(TreeNode node);
	abstract public void initialize(IndexReader reader) throws IOException ;
	
	/*
	 * FeatureIterator interface
	 */
	public String getClue(){
		throw new UnsupportedOperationException();
	}
	
	public Span getCurrentSpan() {
		return currentSpan;
	}
	
	public int getCurrentDocID(){
		if (status != null)
			return status.docId;
		else
			return -1;
	}

	public SpanStatus getCurrentStatus() {
		return status;
	}
	
	public SpanStatus nextDoc() throws IOException {
		throw new UnsupportedOperationException();
	}

	public SpanStatus skipTo(int target) throws IOException {
		throw new UnsupportedOperationException();
	}

	public boolean nextSpan() throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public SpanStatus lazySkipTo(int docID) throws IOException {
		throw new UnsupportedOperationException();
	}
}