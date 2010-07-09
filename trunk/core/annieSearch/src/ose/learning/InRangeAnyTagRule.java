package ose.learning;

import ose.parser.QueryTreeParser;
import ose.parser.RangeFunctionHandler;
import ose.processor.cascader.Constraint;
import ose.processor.cascader.RangeConstraint;

public class InRangeAnyTagRule implements TagRuleClassifier {

	public int getClassification(String tags, String fieldQuery) {
		if (tags == null)
			return -1;
		QueryTreeParser parser = new QueryTreeParser(fieldQuery);
		RangeFunctionHandler handler = new RangeFunctionHandler();
		Constraint constraint = handler.parseNode(parser.parseQueryNode().getFirstChild());
		if (constraint instanceof RangeConstraint) {
			RangeConstraint range = (RangeConstraint) constraint;
			for (String tag : tags.split(" ")) {
				if (range.satisfy(tag))
					return 1;
			}
		}
		return 0;
	}

}
