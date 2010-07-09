package ose.learning;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.Feedback;
import ose.database.FeedbackManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;
import ose.database.TagRuleInfo;
import ose.database.TagRuleInfoManager;
import ose.query.OQuery;


public class OracleFeedback {

	private List<FieldInfo> fields;
	
	private int domainId;
	private int indexId;
	private static Map<String, TagRuleClassifier> tagRuleClassifierMap ;
	private Map<Integer,AnnotatedDoc> annotatedDocs; 
	private OQuery query;
	private FeedbackManager fbMan ;
	private TagRuleInfoManager triman;
	private DocTagManager tagMan ;
	
	static {
		tagRuleClassifierMap = new HashMap<String, TagRuleClassifier>();
		tagRuleClassifierMap.put("contain", new ContainTagRule());
		tagRuleClassifierMap.put("cover", new CoverTagRule());
		tagRuleClassifierMap.put("inrange_any", new InRangeAnyTagRule());
		tagRuleClassifierMap.put("empty", new EmptyTagRule());
	}
	
	public OracleFeedback(int domainId,int indexId) throws SQLException{
		this.domainId = domainId;
		this.indexId = indexId;
		
		
		initFieldIds();
		
		fbMan = new FeedbackManager();
//		fbMan.setBatchUpdate(true);
		triman = new TagRuleInfoManager();
		tagMan = new DocTagManager();
		
		
	}
	
	private void initFieldIds() throws SQLException{
		FieldInfoManager man = new FieldInfoManager();
		fields = man.query("select * from FieldInfo where DomainId = " + domainId);
	}
	
	private int getFieldIdFromName(String fieldName){
		for (FieldInfo field : fields) {
			if (field.getName().equals(fieldName))
				return field.getFieldId();
		}
		return -1;
	}
	
	public void generate(int queryId) throws IOException, SQLException{
		QueryInfo queryInfo = new QueryInfoManager().queryByKey(queryId);
		if (queryInfo == null)
			throw new RuntimeException("No query for " + queryId + " is found. ");
		
		query = new OQuery(domainId,  queryInfo.getQueryString() );
		
		fetchTagsFromDatabase();
		
		TagRuleInfo otherRule = triman.getTagRuleInfoForFieldId(getFieldIdFromName("other"));
		if (otherRule == null)
			throw new RuntimeException("No tag rule for other is found !");
		
		List<Feedback> batch = new ArrayList<Feedback>();
		
		for (Entry<Integer,AnnotatedDoc> entry : annotatedDocs.entrySet()) {
			int isObjectPage = getClassificationFromRule("other", otherRule.getValue(), 
					new HashMap<String, String>(), entry.getValue());
//			if (entry.getKey() == 935){
//				System.out.println("aha");
//			}
			if (isObjectPage == 1) {
				
				boolean relevant = getOracleFeedback(query, entry.getKey());
				Feedback feedback = new Feedback(queryId, entry.getKey(), indexId, domainId, relevant);
				System.out.println("Inserting " + feedback + "\t" + entry.getValue());
				batch.add(feedback);
				
			}
			else {
				Feedback feedback = new Feedback(queryId, entry.getKey(), indexId, domainId, false);
				System.out.println("Inserting " + feedback+ "\t" + entry.getValue());
				batch.add(feedback);
			}
			if (batch.size() == 500){
				fbMan.insertBatch(batch);
				batch.clear();
			}
		}
		if (batch.size() > 0)
			fbMan.insertBatch(batch); //commit left-over batch;
		
		System.out.println("Number of annotated documents : " + annotatedDocs.size());
		
	}

	/**
	 * @throws SQLException
	 */
	public void fetchTagsFromDatabase() throws SQLException {
		List<DocTag> allTags = tagMan.getAllTagForIndexDomain(indexId, domainId);
		System.out.println("Got " + allTags.size() + " tags");
		annotatedDocs = new HashMap<Integer, AnnotatedDoc>();
		for (DocTag tag : allTags) {
			String fieldName = getFieldName(fields, tag.getFieldId());
			if (!annotatedDocs.containsKey(tag.getDocId())){
				AnnotatedDoc annotDoc = new AnnotatedDoc(fieldName, tag.getValue());
				annotatedDocs.put(tag.getDocId(), annotDoc);
			}
			else {
				annotatedDocs.get(tag.getDocId()).addTag(fieldName, tag.getValue());
			}
		}
	}
	
	public Map<Integer, AnnotatedDoc> getAnnotatedDocs() {
		return annotatedDocs;
	}
	
	private int getClassificationFromRule(String fieldName, String rule, Map<String, String> query, AnnotatedDoc doc){
		String tags = doc.getTagForField(fieldName);
		String fieldQuery = query.get(fieldName);
		String [] tokens = rule.split("[^A-Za-z0-9_]");
		if (tokens.length > 0 ){
			String ruleName = tokens[0];
			TagRuleClassifier classifier = tagRuleClassifierMap.get(ruleName);
			if (classifier == null)
				return 0;
			else
				return classifier.getClassification(tags, fieldQuery);
		}
		return 0;
	}
	
	public boolean getOracleFeedback(OQuery query, int docId) throws SQLException{
		for (Entry<String, String> fieldNameValuePair : query.getFieldValueMap().entrySet()) {
			if (fieldNameValuePair.getValue().trim().length() > 0 && !matchDocQueryField(docId, fieldNameValuePair.getKey(), query)){
				return false;
			}
		}
		return true;
	}
	
	private boolean matchDocQueryField(int docId, String fieldName, OQuery query) throws SQLException{
		TagRuleInfo tagRule = triman.getTagRuleInfoForFieldId(getFieldIdFromName(fieldName));
		return 1 == getClassificationFromRule(fieldName, tagRule.getValue(), query.getFieldValueMap(), annotatedDocs.get(docId));
	}
	
	private String getFieldName(List<FieldInfo> fields, int fieldId) {
		for (FieldInfo fieldInfo : fields) {
			if (fieldInfo.getFieldId() == fieldId)
				return fieldInfo.getName();
		}
		return null;
	}
	
	class AnnotatedDoc{
		Map<String, String> fieldTags ;
		
		public AnnotatedDoc(String fieldName, String value) {
			fieldTags = new HashMap<String, String>();
			fieldTags.put(fieldName, value);
		}
		
		public void addTag(String fieldName, String value){
			if (!fieldTags.containsKey(fieldName)){
				fieldTags.put(fieldName, value);
			}
			else{
				String old = fieldTags.get(fieldName);
				fieldTags.put(fieldName, old + " " + value);
			}
		}
		
		public String getTagForField(String fieldName){
			return fieldTags.get(fieldName);
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return fieldTags.toString();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		int queryId = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-index")){
				indexId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-query")){
				queryId = Integer.parseInt(args[i+1]);
			}
		}
		
		if (domainId == 0 || indexId == 0){
			System.out.println("Usage : OracleFeedback.java -domain [domainId] -index [indexId] -query [query id]");
			System.exit(1);
		}
		System.out.println("Hello, i'm generating oracle feedback for domain " + domainId + " index " + indexId + " query " + queryId);
	
		OracleFeedback generator = new OracleFeedback(domainId,indexId);
		generator.generate(queryId);
	}
}
