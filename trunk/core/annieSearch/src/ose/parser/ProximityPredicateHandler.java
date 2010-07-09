package ose.parser;

import java.util.ArrayList;
import java.util.List;

import ose.processor.cascader.AndPredicate;
import ose.processor.cascader.PhrasePredicate;
import ose.processor.cascader.ProximityPredicate;
import ose.processor.cascader.QueryPredicate;

public class ProximityPredicateHandler implements QueryPredicateHandler {

	public QueryPredicate parseNode(ParsingNode node) throws IllegalAccessException, InstantiationException{
		if (node instanceof ParentNode) {			
			ParentNode pNode = (ParentNode) node;
			if (pNode.getChildren().size() != 4)
				throw new InstantiationException("ProximityPredicate takes 4 arguments");
			ParsingNode child0 = pNode.getChildren().get(0);
			QueryPredicateHandler handler0 = OSQueryParser.getPredicateHandler(child0.getNodeName());
			ParsingNode child1 = pNode.getChildren().get(1);
			QueryPredicateHandler handler1 = OSQueryParser.getPredicateHandler(child1.getNodeName());
			ParsingNode child2 = pNode.getChildren().get(2);
			ParsingNode child3 = pNode.getChildren().get(3);
			return new ProximityPredicate(node.getNodeName(), 
					handler0.parseNode(child0),
					handler1.parseNode(child1),
					((LiteralNode) child2).getNumber(),
					((LiteralNode) child3).getNumber()
					);
		}
		return null;
	}

}
