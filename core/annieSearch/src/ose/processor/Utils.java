package ose.processor;

import ose.processor.splock.ProcessingNode;
import ose.utils.CommonUtils;

public class Utils {
	
	static public void printTree(TreeNode queryTree) {
		printTree(0,queryTree);
	}
	
	static private void printTree(int level, TreeNode queryTree) {
		System.out.println( CommonUtils.tabString(level) + "[@" + queryTree.hashCode() + ", " + queryTree.getParents().size() + "]" + queryTree);
		if (queryTree instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) queryTree;
			for (TreeNode child : pNode.getChildren()){
				printTree(level + 1 , child);
			}
		}
	}
}
