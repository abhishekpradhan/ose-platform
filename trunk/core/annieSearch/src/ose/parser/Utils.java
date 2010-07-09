package ose.parser;

import ose.processor.cascader.RangeConstraint;

public class Utils {

	static public RangeConstraint parseRangeConstraint(String constraint) {
		QueryTreeParser parser = new QueryTreeParser(constraint);
		RangeFunctionHandler handler = new RangeFunctionHandler();
		return (RangeConstraint) handler.parseNode(parser.parseQueryNode().getFirstChild());
	}
	
}
