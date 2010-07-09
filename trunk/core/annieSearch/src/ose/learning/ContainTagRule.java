package ose.learning;

/*
 * TODO : buggy : same effect as Cover because tags 
 * is always tokenized
 * 
 * return 1 if fieldValue is a subset of 
 *             tokenized tags 
 */

public class ContainTagRule implements TagRuleClassifier {

	public int getClassification(String tags, String fieldQuery) {
		if (tags == null)
			return -1;
		for (String tag : tags.split(" ")) {
			if (tag.equals(fieldQuery))
				return 1;
		}
		return 0;
	}

}
