package ose.processor.cascader;

import ose.parser.ParentNode;
import ose.parser.ParsingNode;

public class BooleanFeaturePredicateHandler extends FeatureGeneratorHandler {
	
	public QueryPredicate parseNode(ParsingNode node)
			throws IllegalAccessException, InstantiationException {
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.isGeneratorNode()) {
				String generatorName = pNode.getNodeName();
				return new BooleanFeaturePredicate(generatorName,
						parseChildNode(pNode.getFirstChild()));
			}
		}
		System.err.println("Unknown node " + node);
		return null;
	}
}
