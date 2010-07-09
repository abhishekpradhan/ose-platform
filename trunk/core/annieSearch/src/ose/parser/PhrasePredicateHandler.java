package ose.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ose.processor.cascader.AndPredicate;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.OrPredicate;
import ose.processor.cascader.PhrasePredicate;
import ose.processor.cascader.QueryPredicate;
import ose.processor.cascader.TextPredicate;

/* 
 * Handle Phrase() and And() predicates
 */
public class PhrasePredicateHandler implements QueryPredicateHandler {
	
	
	public QueryPredicate parseNode(ParsingNode node) throws IllegalAccessException, InstantiationException{
		if (node instanceof ParentNode) {
			List<QueryPredicate> predicateList = new ArrayList<QueryPredicate>();
			ParentNode pNode = (ParentNode) node;
			for (ParsingNode child : pNode.getChildren()) {
				if (child instanceof LiteralNode) {
					LiteralNode litChild = (LiteralNode) child;
					predicateList.addAll(convertMacroToTokenPredicate(litChild.getValue()));
				} 
				else{
					QueryPredicateHandler handler = OSQueryParser.getPredicateHandler(child.getNodeName());				
					predicateList.add(handler.parseNode(child));
				}
			}
			
			if (predicateList.size() == 0){
				throw new RuntimeException("PhrasePredicate : No child found???");
			}
			else if (predicateList.size() == 1){
				return predicateList.get(0);
			}
			else {
				if (node.getNodeName().toLowerCase().equals("and"))
					return new AndPredicate(node.getNodeName(), predicateList);
				else if (node.getNodeName().toLowerCase().equals("or"))
					return new OrPredicate(node.getNodeName(), predicateList);
				else
					return new PhrasePredicate(node.getNodeName(), predicateList);
			}
		}
		return null;
	}
	
	private List<QueryPredicate> convertMacroToTokenPredicate(String text){
		StringTokenizer tokenizer = new StringTokenizer(text);
		List<QueryPredicate> tokenPredicates = new ArrayList<QueryPredicate>();
		while (tokenizer.hasMoreTokens()){
			//TODO : make this more general than just "Token"
			tokenPredicates.add(new TextPredicate("Token",Arrays.asList(new String[]{tokenizer.nextToken()})));
		}
		return tokenPredicates;
	}
}
