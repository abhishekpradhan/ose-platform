package ose.parser;

import ose.processor.cascader.QueryPredicate;
import ose.processor.cascader.TopPredicate;

/* 
 * Handle Phrase() and And() predicates
 */
public class TopPredicateHandler implements QueryPredicateHandler {
	
	
	public QueryPredicate parseNode(ParsingNode node) throws IllegalAccessException, InstantiationException{
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.getChildren().size() != 2)
				throw new InstantiationException("TopPredicate takes 2 arguments");
			ParsingNode child0 = pNode.getChildren().get(0);
			QueryPredicateHandler handler0 = OSQueryParser.getPredicateHandler(child0.getNodeName());
			ParsingNode child1 = pNode.getChildren().get(1);
			return new TopPredicate(node.getNodeName(), 
					handler0.parseNode(child0),
					((LiteralNode) child1).getNumber().intValue()
					);
		}
		return null;
	}
	
}
