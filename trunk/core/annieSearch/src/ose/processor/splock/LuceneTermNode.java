package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

import common.profiling.Profile;

import ose.index.IndexFieldConstant;
import ose.index.Utils;
import ose.parser.ConstraintHandler;
import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.QueryParser;
import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.processor.cascader.Constraint;

/*
 * Each node corresponds to one inverted term in Lucene index. 
 */

public class LuceneTermNode extends LeafNode{
	public static final String ATTRIBUTE_NUMERIC = "_numeric";
	public static final String PREDICATE_NUMBER_TITLE = "Number_title";
	public static final String PREDICATE_NUMBER_BODY = "Number_body";
	
	protected String field, text;
	protected TermPositions termPos;
	protected boolean gotFirstSpan;
	
	private int spanPosition;
	
	private Profile profiler = Profile.getProfile("leafIO");
	protected Profile nextCounter = Profile.getProfile("IONextCount");
	protected Profile skipToCounter = Profile.getProfile("IOSkipToCount");
	protected Profile nextPosCounter = Profile.getProfile("IONextPosCount");
	
	public LuceneTermNode() {
		termPos = null;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	/*
	 * parse 
	 * 	Token(xxx) or Token(x,y,z..)
	 * 	HTMLTitle(xxx) or HTMLTitle(x,y,z)
	 * 	Number_body(_range(xxx,yyy)) or Number_body()
	 * 	Number_title(_range(xxx,yyy)) or Number_body()
	 */	
	@Override
	public TreeNode parse(ParsingNode node, QueryParser parser) {		
		String nodeName = node.getNodeName();
		if (node instanceof LiteralNode) {
			LiteralNode lNode = (LiteralNode) node;
			setField(IndexFieldConstant.FIELD_BODY); //the default field
			setText(lNode.getValue());
			return this;
		}
		else if (node instanceof ParentNode) {
			ParentNode parent = (ParentNode) node;
			
			if (IndexFieldConstant.FIELD_BODY.equalsIgnoreCase(nodeName) || IndexFieldConstant.FIELD_HTMLTITLE.equalsIgnoreCase(nodeName)){
				String fieldName = nodeName;
				List<String> terms = new ArrayList<String>();
				for (ParsingNode child : parent.getChildren()) {
					if (child instanceof LiteralNode) {
						LiteralNode litNode = (LiteralNode) child;
						terms.add( litNode.getValue() );
					}
					else {
						System.err.println("Unknown child node " + child);
					}
				}
				if (terms.size() == 0) {
					throw new RuntimeException(nodeName + " expects at least 1 argument");
				}
				else if (terms.size() == 1) {
					setField(fieldName);
					setText(terms.get(0));
					return this;
				}
				else {
					List<TreeNode> children = new ArrayList<TreeNode>();
					for (String term : terms){
						LuceneTermNode child = new LuceneTermNode();
						child.setField(fieldName);
						child.setText(term);
						children.add(child);
					}
					DisjunctiveNode disjNode = new DisjunctiveNode();
					disjNode.setChildren(children);
					return disjNode;
				}
			}
			else if (PREDICATE_NUMBER_BODY.equalsIgnoreCase(nodeName) || PREDICATE_NUMBER_TITLE.equalsIgnoreCase(nodeName)){
				String fieldName;
				if (PREDICATE_NUMBER_BODY.equalsIgnoreCase(nodeName))
					fieldName = "Token";
				else
					fieldName = "HTMLTitle";
				if (parent.getChildren().size() == 0){
					setField(fieldName);
					setText(IndexFieldConstant.TERM_NUMBER);
					return this;
				}
				else if (parent.getChildren().size() == 1){
					Constraint constraint = new ConstraintHandler().parseNode(parent.getFirstChild());
					if (constraint != null){
												
						setField(fieldName);
						setText(IndexFieldConstant.TERM_NUMBER);
						
						//this is because we want to use the implementation of Selection depending on the parser's implementation;
						SelectionNode selNode = (SelectionNode) parser.getTreeNodeByName("Selection"); 
						 
						selNode.setConstraint(constraint);

						selNode.setChild(this);
						
						return selNode; 
					}
					else {
						System.err.println("Something wrong with the constraint ");
					}
				}
			}
		}
		
		throw new RuntimeException(" Bad node : " + node);
	}
	
//	@Override
//	public TermPositions getInvertedList(IndexReader reader) throws IOException{
//		if (termPos == null){
//			termPos = reader.termPositions(new Term(field, text));
//		}
//		return termPos ; 
//	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer() ;
		buffer.append("Leaf : " + field) ;
		buffer.append("(" + text + ")");
		return buffer.toString();
	}
	
	@Override
	public boolean equalsNode(TreeNode node) {
		if (node == null)
			return false;
		if (node instanceof LuceneTermNode) {
			LuceneTermNode lNode = (LuceneTermNode) node;
			return field.equals(lNode.field)  &&  text.equals(lNode.text) ;
		}
		return false;
	}
	
	@Override
	public SpanStatus nextDoc() throws IOException {
		profiler.start();
		if (termPos == null){
			throw new RuntimeException("Must initialize first @" + this.hashCode());
		}
		spanPosition = 0;
		nextCounter.increment();
		if (termPos.next()){
			status = SpanStatus.available(termPos.doc());
			gotFirstSpan = false; //lazy set payload
		}
		else {
			status = SpanStatus.done();
		}
		profiler.end();
		return status;
	}

	/**
	 * @throws IOException
	 */
	private void setCurrentSpanAndPayload() throws IOException {
		nextPosCounter.increment();
		currentSpan = new Span(termPos.doc(), termPos.nextPosition(), 0);
		if (text.startsWith(IndexFieldConstant.TERM_NUMBER)){
			double value = Utils.getPayloadNumber(termPos);
			currentSpan.setAttribute(ATTRIBUTE_NUMERIC, value);
		}
	}
	
	@Override
	public SpanStatus skipTo(int docID) throws IOException{
		profiler.start();
		spanPosition = 0;
		skipToCounter.increment();
		//WARNING : since this call always advances, this might cause a miss if docID < status.docId
		if (termPos.skipTo(docID)){
			status = SpanStatus.available(termPos.doc());
			gotFirstSpan = false; // lazy set payload
		}
		else{ 
			status = SpanStatus.done();
		}
		profiler.end();
		return status;
	}
	
	/*
	 * for lucene term, this is the same as skipTo because the cost of moving to the next
	 * element is fixed 
	 */
	public SpanStatus lazySkipTo(int docID) throws IOException {
		return skipTo(docID);
	}
	
	@Override
	public boolean nextSpan() throws IOException {
		profiler.start();
		if (++spanPosition < termPos.freq()){
			setCurrentSpanAndPayload();
			profiler.end();
			return true;
		}
		else{
			currentSpan = null;
			profiler.end();
			return false;
		}
	}
	
	@Override
	public Span getCurrentSpan() {
		profiler.start();
		try {
			if (!gotFirstSpan){
				gotFirstSpan = true;
				setCurrentSpanAndPayload();
			}
			if (currentSpan != null)
				currentSpan.endPos = currentSpan.startPos;
			profiler.end();
			return currentSpan;
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		termPos = reader.termPositions(new Term(field, text));
		status = SpanStatus.invalid();
		currentSpan = null; 
	}
}
