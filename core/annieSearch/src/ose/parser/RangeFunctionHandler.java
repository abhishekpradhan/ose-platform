package ose.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.processor.cascader.Constraint;
import ose.processor.cascader.RangeConstraint;
import sjm.parse.tokens.Token;

public class RangeFunctionHandler extends ConstraintHandler{
	static private Map<String, Double> CONSTANT_MAP;
	
	static {
		CONSTANT_MAP = new HashMap<String, Double>();
		CONSTANT_MAP.put("neg_infinity", -100000000.0);
		CONSTANT_MAP.put("infinity", 100000000.0);
	}
	
	public RangeFunctionHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public Constraint parseNode(ParsingNode node){
		ParentNode pNode = (ParentNode) node;
		List<ParsingNode> children = pNode.getChildren();
		if (children.size() == 2){
			return new RangeConstraint(convertParsingNodeToNumber( children.get(0) ) , convertParsingNodeToNumber( children.get(1) ) );
		}
		System.err.println("not a range function node " + node);
		return null;
	}
	
	public static Double convertParsingNodeToNumber( ParsingNode node){
		if (node instanceof LiteralNode) {
			LiteralNode litNode = (LiteralNode) node;
			return convertStringToNumber(litNode.getValue());
		}
		System.err.println("Not a number node " + node);
		return null;
	}
	
	static private Double convertStringToNumber(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return CONSTANT_MAP.get(s);
		}
		
	}
	
	static public RangeConstraint parse(String constraintStr){
		QueryTreeParser parser = new QueryTreeParser(constraintStr);
		RangeFunctionHandler handler = new RangeFunctionHandler();
		return (RangeConstraint) handler.parseNode(parser.parseQueryNode().getFirstChild());
	}
}

