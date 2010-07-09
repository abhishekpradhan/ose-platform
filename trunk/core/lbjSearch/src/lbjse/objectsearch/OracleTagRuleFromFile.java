package lbjse.objectsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.learning.TagRuleClassifier;

public class OracleTagRuleFromFile extends OracleTagRule {

	static private Map<String, String> tagRuleForField;
	
	private Map<String, String> tagRule;
	
	static {
		tagRuleForField = new HashMap<String, String>();
		tagRuleForField.put("brand", "contain");
		tagRuleForField.put("model", "cover");
		tagRuleForField.put("mpix", "inrange_any");
		tagRuleForField.put("zoom", "inrange_any");
		tagRuleForField.put("price", "inrange_any");
		tagRuleForField.put("other", "empty");
		
		tagRuleForField.put("name", "cover");
		tagRuleForField.put("dept", "cover");
		tagRuleForField.put("univ", "cover");
		tagRuleForField.put("area", "cover");
		
		tagRuleForField.put("moni", "inrange_any");
		tagRuleForField.put("hdd", "inrange_any");
		tagRuleForField.put("proc", "inrange_any");
	}
	
	//default tag rule
	public OracleTagRuleFromFile() {
		tagRule = tagRuleForField;
	}
	
	public OracleTagRuleFromFile(String confFileName) {
		throw new RuntimeException("Not implemented yet");
	}
	
	@Override
	public boolean satisfyTagRule(String fieldName, String fieldValue,
			List<String> tags) {
		String ruleName = tagRule.get(fieldName);
		TagRuleClassifier classifier = tagRuleClassifierMap.get(ruleName);
		if (classifier == null)
			throw new RuntimeException("no rule is found");
		else {
			StringBuffer mergedTags = new StringBuffer();
			for (String tag : tags)
				mergedTags.append(" " + tag);
			return classifier.getClassification(mergedTags.toString(), fieldValue) == 1;
		}
	}

}
