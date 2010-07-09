package ose.parser;

import ose.processor.cascader.NumberPredicate;
import ose.processor.cascader.QueryPredicate;

public class NumberPredicateHandler implements QueryPredicateHandler {
	
	public NumberPredicateHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public QueryPredicate parseNode(ParsingNode node) throws IllegalAccessException, InstantiationException{
		if (node instanceof ParentNode) {
			ParentNode parentNode = (ParentNode) node;
			ParsingNode child = parentNode.getFirstChild();
			if (child instanceof ParentNode) {
				ParentNode pNode = (ParentNode) child;
				if (pNode.isFunctionNode()){
					return new NumberPredicate(node.getNodeName(), parentNode.getNodeName(), (new ConstraintHandler()).parseNode(pNode) );
				}
			}
			else if (child == null){
				return new NumberPredicate(node.getNodeName(), parentNode.getNodeName(), null );
			}
			System.err.println("Unknown child node " + child);
			return null;
		}
		else{
			System.err.println("Unknown node " + node);
			return null;
		}
	}
}
