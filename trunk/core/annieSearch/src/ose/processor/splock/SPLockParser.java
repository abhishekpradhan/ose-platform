package ose.processor.splock;

import java.util.HashMap;
import java.util.Map;

import ose.index.IndexFieldConstant;
import ose.parser.LiteralNode;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryTreeParser;
import ose.processor.QueryParser;
import ose.processor.TreeNode;

public class SPLockParser implements QueryParser{

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
		nameToNodeClass.put("AndL", LeftPushConjunctiveNode.class);
		nameToNodeClass.put("PhraseL", LeftPushPhraseNode.class);
		nameToNodeClass.put("ProximityL", LeftPushProximityNode.class);
		
		
		nameToNodeClass.put("%BooleanFeature", BooleanFeatureNode.class);
//		nameToNodeClass.put("%CountNumber", BaseTreeNode.class);
//		nameToNodeClass.put("%FeatureSet", BaseTreeNode.class);
//		nameToNodeClass.put("%Log", BaseTreeNode.class);
//		nameToNodeClass.put("%Null", BaseTreeNode.class);
		
		nameToNodeClass.put("#Logistic", LogisticNode.class);
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
		TreeNode root = generateTreeNodeByName(rootNode.getNodeName());
		root = root.parse(rootNode, this);
		return root;
	}
	
	static public TreeNode generateTreeNodeByName(String predName)  {
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
	
	public TreeNode getTreeNodeByName(String predName) {
		return generateTreeNodeByName(predName);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String query = "And(Token(camera),Token(digital)) And(Token(canon),Token(camera))";
		SPLockParser parser = new SPLockParser();
		TreeNode node = parser.parse(query) ;
		System.out.println(node);
		System.out.println("Done.");
	}

}
