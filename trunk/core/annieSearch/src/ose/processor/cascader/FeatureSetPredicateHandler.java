package ose.processor.cascader;

import java.util.ArrayList;
import java.util.List;

import ose.parser.OSQueryParser;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryPredicateHandler;

/**
 * @author Pham Kim Cuong
 *
 */
public class FeatureSetPredicateHandler implements QueryPredicateHandler{
	
	public QueryPredicate parseNode(ParsingNode node)
	throws IllegalAccessException, InstantiationException {
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.isGeneratorNode()){
				List<QueryPredicate> featureGenerators = new ArrayList<QueryPredicate>();
				String generatorName = pNode.getNodeName();
				for (ParsingNode child : pNode.getChildren()) {
					featureGenerators.add(parseChildNode(child));
				}
				return new FeatureSetPredicate(generatorName, featureGenerators);
			}
		}
		System.err.println("Unknown node " + node );
		return null;
	}
	
	private QueryPredicate parseChildNode(ParsingNode childNode) throws InstantiationException , IllegalAccessException{
		String subPredicateName = childNode.getNodeName();
		QueryPredicateHandler predicateHandlerClass = OSQueryParser.getPredicateHandler(subPredicateName);
		if (predicateHandlerClass != null){
			return predicateHandlerClass.parseNode(childNode);
		}
		else return null;
	}
}
