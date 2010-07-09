package ose.processor.shallow;

import java.util.HashMap;
import java.util.Map;

import ose.index.IndexFieldConstant;
import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryTreeParser;
import ose.processor.QueryParser;
import ose.processor.TreeNode;
import ose.processor.splock.BooleanFeatureNode;
import ose.processor.splock.DisjunctiveNode;
import ose.processor.splock.LuceneTermNode;
import ose.processor.splock.ProcessingNode;
import ose.processor.splock.SelectionNode;
import ose.processor.splock.TopNode;

public class ShallowParser implements QueryParser{

	static Map<String, Class<? extends TreeNode> > nameToNodeClass;
	
	static {
		nameToNodeClass = new HashMap<String, Class<? extends TreeNode>>();
		
		nameToNodeClass.put(null, DisjunctiveNode.class); //root node 
		nameToNodeClass.put(IndexFieldConstant.FIELD_BODY, LuceneTermNode.class);
		nameToNodeClass.put(IndexFieldConstant.FIELD_HTMLTITLE, LuceneTermNode.class);
		nameToNodeClass.put(LuceneTermNode.PREDICATE_NUMBER_BODY, LuceneTermNode.class);
		nameToNodeClass.put(LuceneTermNode.PREDICATE_NUMBER_TITLE, LuceneTermNode.class);
		nameToNodeClass.put(LiteralNode.NODE_LITERAL, LuceneTermNode.class);
		nameToNodeClass.put("Selection", SelectionNode.class);
		
		nameToNodeClass.put("Top", TopNode.class);
		nameToNodeClass.put("Phrase", PhraseNode.class);
		nameToNodeClass.put("And", ConjunctiveNode.class);
		nameToNodeClass.put("Or", DisjunctiveNode.class);		
		nameToNodeClass.put("Proximity", ProximityNode.class);
		
		
		nameToNodeClass.put("%BooleanFeature", BooleanFeatureNode.class);
	}
	
	public TreeNode parse(String query){
		QueryTreeParser parser = new QueryTreeParser(query);
		ParentNode root = parser.parseQueryNode();
//		System.out.println("Query Tree");
//		System.out.println(root);
		ProcessingNode treeRoot = (ProcessingNode) parse(root);
		if (treeRoot.getChildren().size() == 1) { //if only feature is found, just assign that node as the root.
			return treeRoot.getChildren().get(0);
		}
		else
			return treeRoot;
	}
	
	public TreeNode parse(ParsingNode rootNode){
		TreeNode aTree = parseNode(rootNode);
		return swapShallowNode(aTree);
	}
	
	public TreeNode parseNode(ParsingNode rootNode){
		TreeNode root = getTreeNodeByName(rootNode.getNodeName());
		root = root.parse(rootNode, this);
		return root;
	}
	
	public TreeNode getTreeNodeByName(String predName)  {
		Class<? extends TreeNode> handlerClass = nameToNodeClass.get( predName );
		try {
			if (handlerClass != null){
				return handlerClass.newInstance();					
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private TreeNode swapShallowNode(TreeNode aTree) {
		
		return aTree;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String query = "And(Token(camera),Token(digital)) And(Token(canon),Token(camera))";
		ShallowParser parser = new ShallowParser();
		TreeNode node = parser.parse(query) ;
		System.out.println(node);
		System.out.println("Done.");
	}

}
