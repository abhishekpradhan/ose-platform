package ose.processor.splock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.processor.TreeNode;

public class PreProcessor {
	private Map<Integer, List<TreeNode>> listOfNodesAtDepthMap;
	
	public PreProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseTreeNode deduplicate(BaseTreeNode treeRoot){
		removeDuplicateBranches(treeRoot);
//		printTree(0,treeRoot);
		augmentWithShareSkipToNodes(treeRoot);
		return treeRoot;
	}
	
	private void augmentWithShareSkipToNodes(TreeNode node) {
		if ( !(node instanceof ShareSkipToNode) && node.getParents().size() > 1){
			List<TreeNode> parents = new ArrayList<TreeNode>(node.getParents());
			node.clearParent();
			ShareSkipToNode shareNode = new ShareSkipToNode(node);
			node.addParent(shareNode);
			
			for (TreeNode parent : parents){
				ConnectorNode connector = new ConnectorNode(parent, shareNode);
				shareNode.addParent(connector);
				((ProcessingNode) parent).swapChild(node, connector);
			}
			
		}
		
		if (node instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) node;
			List<TreeNode> listOfChildren = new ArrayList<TreeNode>(pNode.getChildren()); //clone this so that the next for is robust from changes.
			for (TreeNode child : listOfChildren){
				augmentWithShareSkipToNodes(child);
			}
		}		
	}
	
	private void removeDuplicateBranches(TreeNode queryTree) {
		listOfNodesAtDepthMap = new HashMap<Integer, List<TreeNode>>();
		int depth = addNodeAtDepth(queryTree);
		System.out.println("Depth of the tree : " + depth);
		
		for (int d = 0; d <= depth ; d++){
			List<TreeNode> nodes = listOfNodesAtDepthMap.get(d);
			System.out.println("De-dup " + nodes.size() + " nodes at level " + d);
			dedupBranches(nodes);
		}
	}
	
	private void dedupBranches(List<TreeNode> nodes){
		for (int i = 0 ; i < nodes.size() ; i++){
			TreeNode coreNode = nodes.get(i);
			if (coreNode == null )
				continue;
			if (coreNode instanceof BooleanFeatureNode) {
				continue;
			}
			for (int j = i + 1 ; j < nodes.size() ; j ++){
				if (coreNode.equalsNode(nodes.get(j))){
					TreeNode duplicatedNode = nodes.get(j);
//					System.out.println("--- removing " + duplicatedNode.hashCode() + " " + duplicatedNode );
					for (TreeNode parent : duplicatedNode.getParents()){
						((ProcessingNode)parent).swapChild(duplicatedNode, coreNode);						
					}
					if (duplicatedNode instanceof ProcessingNode) {
						ProcessingNode pNode = (ProcessingNode) duplicatedNode;
						for (TreeNode child : pNode.getChildren()){
							child.removeParent(duplicatedNode);
						}
					}
					nodes.set(j, null); //remove the duplicated node
				}
			}
		}
	}
	
	private int addNodeAtDepth(TreeNode node){
		int depth = 0;
		if (node instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) node;
			for (TreeNode child : pNode.getChildren()){
				int d = addNodeAtDepth(child);
				if (d + 1 > depth) depth = d + 1;
			}
		}		
		if (!listOfNodesAtDepthMap.containsKey(depth)){
			listOfNodesAtDepthMap.put(depth, new ArrayList<TreeNode>());
		}
		listOfNodesAtDepthMap.get(depth).add(node);
		return depth;
	}
}
