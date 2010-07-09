package lbjse.objectsearch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.database.DocInfo;
import ose.database.DocInfoManager;
import ose.database.DocTag;
import ose.database.DocTagManager;

public class DocAnnotations {
	
	private Query seedQuery;
	private Map<Integer,Query> annotatedDocs;
	private int domainId;
	private int indexId = -1;
	private OracleTagRule oracleTagRule;
	
	public DocAnnotations(int domainId, int indexId) {
		this.domainId = domainId;
		this.indexId = indexId;
		seedQuery = new Query(domainId);
		annotatedDocs = new HashMap<Integer, Query>();
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
		oracleTagRule = new OracleTagRuleFromDatabase(domainId);
	}
	
	public boolean satisfyTagRule(String fieldName,String  fieldValue,String tags){
		return oracleTagRule.satisfyTagRule(fieldName, fieldValue, Arrays.asList(tags.split(" ")));
	}
	
	public List<Integer> getAllDocIds() {
		return new ArrayList<Integer>(annotatedDocs.keySet());
	}
	
	public DocumentFromDatabase getAnnotatedDocumentFromId(int docId){
		return new DocumentFromDatabase(docId, this);
	}
	
	public String getAnnotationForField(int docId, String fieldName) {
		if (!annotatedDocs.containsKey(docId))
			return null;
		
		return annotatedDocs.get(docId).getFieldValue(fieldName);
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
