/**
 * 
 */
package ose.processor.cascader;

import java.util.HashMap;
import java.util.Map;

import ose.parser.OSQueryParser;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryPredicateHandler;

/**
 * @author Pham Kim Cuong
 *
 */
public class FeatureGeneratorHandler implements QueryPredicateHandler {
	
	public QueryPredicate parseNode(ParsingNode node)
			throws IllegalAccessException, InstantiationException {
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.isGeneratorNode()){
				String generatorName = pNode.getNodeName();
				ParentNode childNode = (ParentNode) pNode.getFirstChild();
				return new CountNumberPredicate(generatorName, parseChildNode(childNode));
			}
		}
		System.err.println("Unknown node " + node );
		return null;
	}

	protected QueryPredicate parseChildNode(ParsingNode childNode) throws InstantiationException , IllegalAccessException{
		String subPredicateName = childNode.getNodeName();
		QueryPredicateHandler predicateHandlerClass = OSQueryParser.getPredicateHandler(subPredicateName);
		if (predicateHandlerClass != null){
			return predicateHandlerClass.parseNode(childNode);
		}
		else return null;
	}
}
