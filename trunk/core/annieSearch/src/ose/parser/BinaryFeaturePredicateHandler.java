package ose.parser;

import ose.processor.cascader.DivideByPredicate;
import ose.processor.cascader.FeatureGeneratorHandler;
import ose.processor.cascader.QueryPredicate;

public class BinaryFeaturePredicateHandler extends  FeatureGeneratorHandler{

	public QueryPredicate parseNode(ParsingNode node)
			throws IllegalAccessException, InstantiationException {
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.isGeneratorNode()){
				String generatorName = pNode.getNodeName();
				if ("%DivideBy".equals(generatorName)){
					if (pNode.getChildren().size() == 2){
						return new DivideByPredicate(generatorName, parseChildNode(pNode.getChild(0)),
								parseChildNode(pNode.getChild(1)));
					}
				}
			}
		}
		System.err.println("Unknown node " + node );
		return null;
	}

}
