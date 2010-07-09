package ose.learning;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.smartcardio.ATR;

import org.apache.lucene.index.IndexReader;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.TagRuleInfo;
import ose.database.TagRuleInfoManager;
import ose.database.TrainingQueryInfo;
import ose.database.TrainingQueryInfoManager;
import ose.parser.OSQueryParser;
import ose.processor.cascader.FeatureQuery;
import ose.query.FeatureValue;
import ose.weka.ArffWriter;


public class TrainingDataGenerator {

	private List<FieldInfo> fields;
	
	private int domainId;
	private int indexId;
	private IndexInfo iinfo;
	private String arffPath;
	private static Map<String, TagRuleClassifier> tagRuleClassifierMap ;
	IndexReader reader;
	
	static {
		tagRuleClassifierMap = new HashMap<String, TagRuleClassifier>();
		tagRuleClassifierMap.put("contain", new ContainTagRule());
		tagRuleClassifierMap.put("cover", new CoverTagRule());
		tagRuleClassifierMap.put("inrange_any", new InRangeAnyTagRule());
		tagRuleClassifierMap.put("empty", new EmptyTagRule());
	}
	
	public TrainingDataGenerator(int domainId,int indexId, String arffPath) throws SQLException{
		this.domainId = domainId;
		this.indexId = indexId;
		this.arffPath = arffPath;
		iinfo = new IndexInfoManager().getIndexForId(indexId);
		initFieldIds();
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
	
	public void generate() throws IOException, SQLException{
		TrainingQueryInfoManager tqiMan = new TrainingQueryInfoManager();
		TagRuleInfoManager triman = new TagRuleInfoManager();
		DocTagManager tagMan = new DocTagManager();
		List<DocTag> allTags = tagMan.getAllTagForIndexDomain(indexId, domainId);
		System.out.println("Got " + allTags.size() + " tags");
		Map<Integer,AnnotatedDoc> annotatedDocs = new HashMap<Integer, AnnotatedDoc>();
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
		
		
		TagRuleInfo otherRule = triman.getTagRuleInfoForFieldId(getFieldIdFromName("other"));
		if (otherRule == null)
			throw new RuntimeException("No tag rule for other is found !");
		
		reader = IndexReader.open(iinfo.getIndexPath());

		/* split annotatedDocs into object pages and non-object page by checking rules for "other" */
		Map<Integer,AnnotatedDoc> objectPages = new HashMap<Integer, AnnotatedDoc>();
		
		for (Entry<Integer,AnnotatedDoc> entry : annotatedDocs.entrySet()) {
			int isObjectPage = getClassificationFromRule("other", otherRule.getValue(), 
					new HashMap<String, String>(), entry.getValue());
			if (isObjectPage == 1) {
				objectPages.put(entry.getKey(), entry.getValue());
			}
		}
		
		System.out.println("Number of annotated documents : " + annotatedDocs.size());
		System.out.println("Number of annotated object pages : " + objectPages.size());
		
		for (FieldInfo fieldInfo : fields) {
			String aComment = null;
			System.out.println("Generate examples for field " + fieldInfo);
			InstanceSet instanceSet = new InstanceSet();
			
			TagRuleInfo tagRule = triman.getTagRuleInfoForFieldId(fieldInfo.getFieldId());
			if (tagRule == null) {
				System.out.println("No rule found for field  " + fieldInfo + " skipped.");
				continue;
			}
			
			Map<Integer,AnnotatedDoc> trainingDocs;
			if (fieldInfo.getName().equals("other"))
				trainingDocs = annotatedDocs;
			else 
				trainingDocs = objectPages;
			
			List<TrainingQueryInfo> attrValues = tqiMan.query("Select * from TrainingQueryInfo where FieldId = " + fieldInfo.getFieldId());
			System.out.println("\tNumber of queries " + attrValues.size());
			System.out.println("\tNumber of docs " + trainingDocs.size());
			if (attrValues.size() == 0 || trainingDocs.size() == 0){
				throw new RuntimeException("Can not generate : empty training set");
			}
			
			for (TrainingQueryInfo trainingQueryInfo : attrValues) {
				Map<String, String> query = new HashMap<String, String>();				
				query.put( fieldInfo.getName() , trainingQueryInfo.getValue());
				
				String featureQueryStr = getFeatureQueryForField(query,fieldInfo.getFieldId());
				if (aComment == null) aComment = featureQueryStr;
				if (featureQueryStr == null)
					continue;
				
					
				for (Entry<Integer,AnnotatedDoc> entry : trainingDocs.entrySet()) {
				
					Instance inst = getFeatureInstanceForField(entry.getKey(), featureQueryStr);
					if (inst == null)
						continue;
					int classification = getClassificationFromRule(fieldInfo.getName(), tagRule.getValue(), query, entry.getValue());
					if (classification != -1){
						inst.setClassification(classification);
						instanceSet.addInstance(inst);
					}
				}
				
			}
						
			String arffFilePath = arffPath + "/" + fieldInfo.getName() + ".arff";
			ArffWriter writer = new ArffWriter();
			writer.writeToFile(arffFilePath, instanceSet, aComment);
			System.out.println(arffFilePath + " written to disk : " + instanceSet.getNumberOfInstances() + " examples. ");
		}
		reader.close();
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
	
	private Instance getFeatureInstanceForField(int docId, String featureQueryStr){
		try {
			OSQueryParser parser = new OSQueryParser();
			FeatureQuery featureQuery = parser.parseFeatureQuery(featureQueryStr);
			List<FeatureValue> values = featureQuery.getFeaturesForDoc(reader, docId);
			values.add(0, new IntegerFeatureValue(docId));
			return new VectorInstance(values.toArray(new FeatureValue[]{}));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String getFeatureQueryForField(Map<String, String> query, int fieldId) {
		FeatureInfoManager fieldMan = new FeatureInfoManager();
		try {
			List<FeatureInfo> features = fieldMan.getFeaturesForField(fieldId);
			if (features.size() == 0)
				return null;
			String featureQuery = "";
			for (FeatureInfo featureInfo : features) {
				featureQuery = featureQuery + " " + featureInfo.getTemplate();
			}
			
			return FeatureQuery.instantiate(featureQuery, query);
		} catch (SQLException e) {
			return null;
		}
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
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		String dir = "arff";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-index")){
				indexId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-path")){
				dir = args[i+1];
			}
		}
		
		if (domainId == 0 || indexId == 0){
			System.out.println("Usage : TrainingDataGenerator.java -domain [domainId] -index [indexId] -path [arff dir path]");
			System.exit(1);
		}
		System.out.println("Hello, i'm generating training examples for domain " + domainId + " index " + indexId);
	
		TrainingDataGenerator generator = new TrainingDataGenerator(domainId,indexId,dir);
		generator.generate();
	}
}
