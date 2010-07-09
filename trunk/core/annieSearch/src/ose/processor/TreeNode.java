package ose.processor;

import java.util.List;

import ose.parser.ParsingNode;

public interface TreeNode extends FeatureIterator{
	
	
	public List<TreeNode>  getParents();
	public void addParent(TreeNode parent);
	public void removeParent(TreeNode parent);
	public void clearParent();
	public TreeNode parse(ParsingNode node, QueryParser parser);
	public boolean equalsNode(TreeNode node);
	
	public String toString();

	
}
