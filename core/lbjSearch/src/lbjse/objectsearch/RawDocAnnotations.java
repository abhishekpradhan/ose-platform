package lbjse.objectsearch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.database.DocInfo;
import ose.database.DocInfoManager;
import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.TagRuleInfo;
import ose.database.TagRuleInfoManager;
import ose.learning.ContainTagRule;
import ose.learning.CoverTagRule;
import ose.learning.EmptyTagRule;
import ose.learning.InRangeAnyTagRule;
import ose.learning.TagRuleClassifier;

public class RawDocAnnotations {
	
	private Query seedQuery;
	private Map<Integer,Query> annotatedDocs;
	private Map<String, String> tagRuleMap;
	private int domainId;
	private int indexId = -1;
	
	static private Map<String, TagRuleClassifier> tagRuleClassifierMap;
	
	static {
		tagRuleClassifierMap = new HashMap<String, TagRuleClassifier>();
		tagRuleClassifierMap.put("contain", new ContainTagRule());
		tagRuleClassifierMap.put("cover", new CoverTagRule());
		tagRuleClassifierMap.put("inrange_any", new InRangeAnyTagRule());
		tagRuleClassifierMap.put("empty", new EmptyTagRule());
	}
	
	public RawDocAnnotations(int domainId, int indexId) {
		this.domainId = domainId;
		this.indexId = indexId;
		seedQuery = new Query(domainId);
		annotatedDocs = new HashMap<Integer, Query>();
		tagRuleMap = new HashMap<String, String>();
	}
	
	public void addTagsFromDatabase() throws SQLException {
		DocTagManager tagMan = new DocTagManager();
		List<DocTag> allTags = tagMan.getAllTagForIndexDomain(indexId, domainId);
		for (DocTag tag : allTags) {
			String fieldName = seedQuery.getFieldNameFromId(tag.getFieldId());
			if (!annotatedDocs.containsKey(tag.getDocId())){
				Query annotDoc = new Query();
				annotDoc.setFieldValue(fieldName, tag.getValue());
				annotatedDocs.put(tag.getDocId(), annotDoc);
			}
			else {
				Query annotDoc = annotatedDocs.get(tag.getDocId());
				String old = annotDoc.getFieldValue(fieldName);
				if (old == null){
					annotDoc.setFieldValue(fieldName, tag.getValue());
				}
				else{
					annotDoc.setFieldValue(fieldName, old + " " + tag.getValue());
				}
			}
		}
		System.out.println("\tNumber of tags found : " + allTags.size());
		System.out.println("\tNumber of annotated documents found : " + annotatedDocs.keySet().size());
	}
	
	public void addTagRulesFromDatabase() throws SQLException{
		TagRuleInfoManager triman = new TagRuleInfoManager();
		List<TagRuleInfo> tagRuleList = triman.query("select TagRuleInfo.* from DomainInfo, FieldInfo, TagRuleInfo " +
				"where DomainInfo.DomainId = FieldInfo.DomainId " +
				"  and FieldInfo.FieldId = TagRuleInfo.FieldId " +
				"  and DomainInfo.DomainId = " + domainId);
		for (TagRuleInfo tagRuleInfo : tagRuleList) {
			tagRuleMap.put(seedQuery.getFieldNameFromId(tagRuleInfo.getFieldId()), tagRuleInfo.getValue());
		}
		System.out.println("\tNumber of tag rules found : " + tagRuleList.size());
	}
	
	public List<Integer> getAllDocIds() {
		return new ArrayList<Integer>(annotatedDocs.keySet());
	}
	
	public boolean satisfyTagRule(String fieldName, String fieldValue, String tags){
		String fieldRule = getTagRuleForField(fieldName);
		//TODO : hack here. need full-fledge rule parsers later on
		String [] tokens = fieldRule.split("[^A-Za-z0-9_]");
		if (tokens.length > 0 ){
			String ruleName = tokens[0];
			TagRuleClassifier classifier = tagRuleClassifierMap.get(ruleName);
			if (classifier == null)
				return false;
			else
				return classifier.getClassification(tags, fieldValue) == 1;
		}
		return false;
	}
	
	private String getTagRuleForField(String fieldName) {
		return tagRuleMap.get(fieldName);
	}
	
	public DocInfo getDocInfoForId(int docId){
		try {
			return new DocInfoManager().getDocInfoForId(docId, indexId);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
