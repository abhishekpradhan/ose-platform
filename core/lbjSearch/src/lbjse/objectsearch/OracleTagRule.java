package lbjse.objectsearch;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.learning.ContainTagRule;
import ose.learning.CoverTagRule;
import ose.learning.EmptyTagRule;
import ose.learning.InRangeAnyTagRule;
import ose.learning.TagRuleClassifier;


public abstract class OracleTagRule {

	protected static Map<String, TagRuleClassifier> tagRuleClassifierMap ;
	
	static {
		tagRuleClassifierMap = new HashMap<String, TagRuleClassifier>();
		tagRuleClassifierMap.put("contain", new ContainTagRule());
		tagRuleClassifierMap.put("cover", new CoverTagRule());
		tagRuleClassifierMap.put("inrange_any", new InRangeAnyTagRule());
		tagRuleClassifierMap.put("empty", new EmptyTagRule());
	}
	
	abstract public boolean satisfyTagRule(String fieldName, String fieldValue, List<String> tags);
	
}
