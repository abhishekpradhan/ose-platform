package lbjse.objectsearch;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.TagRuleInfo;
import ose.database.TagRuleInfoManager;
import ose.learning.TagRuleClassifier;

public class OracleTagRuleFromDatabase extends OracleTagRule {
	
	private int domainId ;
	private Map<String, String> tagRuleMap;
	private Map<Integer, String> fieldIdToNameMap;
	
	public OracleTagRuleFromDatabase(int domainId) throws SQLException{
		this.domainId = domainId;
		initFieldNameFromDatabase();
		addTagRulesFromDatabase();
	}
	
	private void initFieldNameFromDatabase() throws SQLException{
		fieldIdToNameMap = new HashMap<Integer, String>();
		List<FieldInfo> fieldInfos = new FieldInfoManager().query("select * from FieldInfo " +
				"where DomainId = " + domainId);
		for (FieldInfo fieldInfo : fieldInfos) {
			fieldIdToNameMap.put(fieldInfo.getFieldId(), fieldInfo.getName());
		}
	}
	
	public void addTagRulesFromDatabase() throws SQLException{
		TagRuleInfoManager triman = new TagRuleInfoManager();
		List<TagRuleInfo> tagRuleList = triman.query("select TagRuleInfo.* from DomainInfo, FieldInfo, TagRuleInfo " +
				"where DomainInfo.DomainId = FieldInfo.DomainId " +
				"  and FieldInfo.FieldId = TagRuleInfo.FieldId " +
				"  and DomainInfo.DomainId = " + domainId);
		tagRuleMap =  new HashMap<String, String>();
		
		for (TagRuleInfo tagRuleInfo : tagRuleList) {
			tagRuleMap.put(fieldIdToNameMap.get(tagRuleInfo.getFieldId()), tagRuleInfo.getValue());
		}
		System.out.println("\tNumber of tag rules found : " + tagRuleList.size());
	}
	
	@Override
	public boolean satisfyTagRule(String fieldName, String fieldValue,
			List<String> tags) {
		String fieldRule = tagRuleMap.get(fieldName);
		//TODO : hack here. need full-fledge rule parsers later on
		String [] tokens = fieldRule.split("[^A-Za-z0-9_]");
		if (tokens.length > 0 ){
			String ruleName = tokens[0];
			TagRuleClassifier classifier = tagRuleClassifierMap.get(ruleName);
			if (classifier == null)
				return false;
			else{
				StringBuffer mergedTags = new StringBuffer();
				for (String tag : tags)
					mergedTags.append(" " + tag);
				return classifier.getClassification(mergedTags.toString(), fieldValue) == 1;
			}
		}
		return false;	}

}
