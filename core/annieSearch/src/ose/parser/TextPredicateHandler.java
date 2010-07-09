package ose.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ose.processor.cascader.QueryPredicate;
import ose.processor.cascader.TextPredicate;

public class TextPredicateHandler implements QueryPredicateHandler {
	
	public TextPredicateHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public QueryPredicate parseNode(ParsingNode node) {
		if (node instanceof ParentNode) {
			ParentNode textNode = (ParentNode) node;
			List<String> terms = new ArrayList<String>();
			for (ParsingNode child : textNode.getChildren()) {
				if (child instanceof LiteralNode) {
					LiteralNode litNode = (LiteralNode) child;
					terms.addAll( tokenisedTerms(litNode.getValue()) );
				}
				else {
					System.err.println("Unknown child node " + child);
				}
			}
			return new TextPredicate(textNode.getNodeName(), terms);
		}
		else
			return null;
	}
	
	private List<String> tokenisedTerms(String text){		
		List<String> terms = new ArrayList<String>();
		if (text == null)
			return terms;
		
		StringTokenizer tokenizer = new StringTokenizer(text);
		while (tokenizer.hasMoreTokens()){
			terms.add(tokenizer.nextToken());
		}
		return terms;
	}
}
