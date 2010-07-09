package ose.learning;

public class EmptyTagRule implements TagRuleClassifier {

	public int getClassification(String tags, String fieldQuery) {
		if (tags == null || tags.trim().length() == 0)
			return 1;
		return 0;
	}

}
