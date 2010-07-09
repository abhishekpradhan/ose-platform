package ose.parser;

import ose.processor.cascader.FeatureGeneratorHandler;
import ose.processor.cascader.NullPredicate;
import ose.processor.cascader.QueryPredicate;

public class EmptyFeaturePredicateHandler extends FeatureGeneratorHandler {
	public QueryPredicate parseNode(ParsingNode node)
	throws IllegalAccessException, InstantiationException {
		String nodeName = node.getNodeName();
		if ("%Null".equals(nodeName)){
			return new NullPredicate();
		}
		System.err.println("Unknown node " + node );
		return null;
	}
}
