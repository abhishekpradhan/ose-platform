package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.processor.QueryParser;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;

public abstract class ProcessingNode extends  BaseTreeNode {
	protected List<TreeNode> children;
	protected String nodeName;
	
	protected ProcessingNode(String nodeName ) {
		children = new ArrayList<TreeNode>();
		this.nodeName = nodeName;
	}
	
	public String getNodeName() {
		return nodeName;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
		for (TreeNode child : children )
			child.addParent(this);
	}
	
	public List<TreeNode> getChildren() {
		return children;
	}
	
	public void swapChild(TreeNode oldChild, TreeNode newChild ){
		for (int i = 0 ; i < children.size() ; i++){
			if (children.get(i) == oldChild){
				children.set(i, newChild);
				newChild.addParent(this);
				return;
			}
		}
		throw new RuntimeException("could not find child to swap at this node : " + this.toString());
	}
	
	public TreeNode parse(ParsingNode node, QueryParser parser) {
		if (node instanceof ParentNode) {
//			nodeName = node.getNodeName();
			ParentNode parent = (ParentNode) node;
			children.clear();
			for (ParsingNode childNode : parent.getChildren()){
				TreeNode child = parser.parse(childNode);
				child.addParent(this);
				children.add(child);
			}
			return this;
		}
		else{
			throw new RuntimeException("Don't know how to parse " + node);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(nodeName + "(");
		boolean first = true;
		for (TreeNode child : children){
			if (!first)
				buffer.append(",");
			buffer.append(child.toString());
			first = false;
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public boolean equalsNode(TreeNode node) {
		if (node == null)
			return false;
		if (node instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) node;
			if (!nodeName.equals(pNode.getNodeName()))
				return false;
			if (children.size() != pNode.getChildren().size())
				return false;
			for (int i = 0 ; i < children.size(); i++)
				if (!children.get(i).equalsNode(pNode.getChildren().get(i)))
					return false;
			return true;
		}
		return false;
	}
	
	@Override
	public void initialize(IndexReader reader) throws IOException {
		for (TreeNode child : children){
			if (child instanceof BaseTreeNode) {
				BaseTreeNode bNode = (BaseTreeNode) child;
				bNode.initialize(reader);
			}
		}
		status = SpanStatus.invalid();
		currentSpan = null;
	}
	
	
}
