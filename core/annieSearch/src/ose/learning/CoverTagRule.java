package ose.learning;

/*
 * return 1 if tokenized fieldValue is a subset of 
 *             tokenized tags 
 */

public class CoverTagRule implements TagRuleClassifier {

	public int getClassification(String tags, String fieldQuery) {
		if (tags == null)
			return -1;
		if (fieldQuery.trim().length() == 0)
			return 1;
		for (String token : fieldQuery.split(" ")) {
			if (!cover(tags, token))
				return 0;
		}
		return 1;
	}
	
	private boolean cover(String tags, String token){
		for (String tag : tags.split(" ")) {
			if (tag.equals(token))
				return true;
		}
		return false;
	}

}
