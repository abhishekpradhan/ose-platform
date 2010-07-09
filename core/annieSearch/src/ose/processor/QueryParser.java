package ose.processor;

import ose.parser.ParsingNode;

public interface QueryParser {

	public TreeNode parse(String query);
	public TreeNode parse(ParsingNode rootNode);
	public TreeNode getTreeNodeByName(String predName);
	
}
