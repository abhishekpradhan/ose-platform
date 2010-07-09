package ose.parser;

import ose.processor.cascader.FeatureGeneratorHandler;
import ose.processor.cascader.LogFeaturePredicate;
import ose.processor.cascader.QueryPredicate;

public class UnaryFeaturePredicateHandler extends FeatureGeneratorHandler {
	public QueryPredicate parseNode(ParsingNode node)
	throws IllegalAccessException, InstantiationException {
	if (node instanceof ParentNode) {
		ParentNode pNode = (ParentNode) node;
		if (pNode.isGeneratorNode()){
			String generatorName = pNode.getNodeName();
			if ("%Log".equals(generatorName)){
				if (pNode.getChildren().size() == 1){
					return new LogFeaturePredicate(generatorName, parseChildNode(pNode.getChild(0)));
				}
			}
		}
	}
	System.err.println("Unknown node " + node );
	return null;
	}
}
