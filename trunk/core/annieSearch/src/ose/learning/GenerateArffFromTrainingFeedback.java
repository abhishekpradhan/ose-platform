package ose.learning;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.TrainingFeedback;
import ose.database.TrainingFeedbackManager;
import ose.database.TrainingQueryInfo;
import ose.database.TrainingQueryInfoManager;
import ose.parser.OSQueryParser;
import ose.processor.cascader.FeatureQuery;
import ose.query.FeatureValue;
import ose.query.OQuery;
import ose.weka.ArffWriter;


public class GenerateArffFromTrainingFeedback {
	private int domainId;
	private int indexId;
	OracleFeedback oracle ;
	
	private IndexReader reader;
	
	public GenerateArffFromTrainingFeedback(int domainId,int indexId) throws SQLException, IOException{
		this.domainId = domainId;
		this.indexId = indexId;
		
		oracle = new OracleFeedback(domainId, indexId);
		oracle.fetchTagsFromDatabase();
		
	}
	
	public void generateForField(int fieldId, String arffFilePath) throws SQLException, IOException {
		
		FieldInfo fieldInfo = new FieldInfoManager().getFieldInfoForId(fieldId);
		if (fieldInfo == null){
			throw new RuntimeException("field does not exist");
		}
		
		Set<Integer> excludedNonDomainDocIds = new HashSet<Integer>();
		
		if (! fieldInfo.getName().equals("other")){
			System.out.println("This is not 'other' field, ignoring non-domain documents");
			FieldInfo otherField = new FieldInfoManager().getFieldInfoByNameAndDomain(domainId, "other");
			List<TrainingQueryInfo> otherQuery = new TrainingQueryInfoManager().getTrainingQueryForFieldId(otherField.getFieldId());
			if (otherQuery.size() != 1){
				throw new RuntimeException("there are not exactly one training query for 'other' field " + otherQuery.size());
			}
			int otherQueryId = otherQuery.get(0).getTrainingQueryId();
			List<TrainingFeedback> feedbacks = new TrainingFeedbackManager().getAllFeedbackForQuery(otherQueryId, indexId);
			for (TrainingFeedback trainingFeedback : feedbacks) {
				if (!trainingFeedback.getRelevant()){
					excludedNonDomainDocIds.add(trainingFeedback.getDocId());
				}
			}
			System.out.println("Exclude " + excludedNonDomainDocIds.size() + " non-domain doc ids");
		}
		
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		reader = IndexReader.open(iinfo.getIndexPath());
		
		String templateFeatureQuery = getFeatureQueryForField(fieldId);
		if (templateFeatureQuery == null) {
			System.out.println("No feature founds, skip field " + fieldId );
			return;
		}
		OQuery oquery = new OQuery(domainId);
		InstanceSet instanceSet = new InstanceSet();
		
		List<TrainingQueryInfo> queriesForField = new TrainingQueryInfoManager().getTrainingQueryForFieldId(fieldId);
		for (TrainingQueryInfo trainingQueryInfo : queriesForField) {
			List<TrainingFeedback> feedbacks = new TrainingFeedbackManager().getAllFeedbackForQuery(trainingQueryInfo.getTrainingQueryId(), indexId);
			oquery.setFieldValue(fieldId, trainingQueryInfo.getValue());
			String featureQuery = FeatureQuery.instantiate(templateFeatureQuery, oquery.getFieldValueMap());
			for (TrainingFeedback fb : feedbacks) {
				if (! excludedNonDomainDocIds.contains( fb.getDocId() )){
					Instance inst = getFeatureInstanceForDoc(fb.getDocId(), featureQuery);
					inst.setClassification(fb.getRelevant()?1:0);
					instanceSet.addInstance(inst);
				}
			}
		}
		reader.close();
		
		ArffWriter writer = new ArffWriter();
		writer.writeToFile(arffFilePath, instanceSet, templateFeatureQuery.replace("\t", "\n"));
		System.out.println(arffFilePath + " written to disk : " + instanceSet.getNumberOfInstances() + " examples. ");
		
	}

	private void debug() throws Exception {
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		reader = IndexReader.open(iinfo.getIndexPath());
		int fieldId = 11;
		int docId = 9490;
		OQuery oquery = new OQuery(domainId);
		String templateFeatureQuery = getFeatureQueryForField(fieldId);
		List<TrainingQueryInfo> queriesForField = new TrainingQueryInfoManager().getTrainingQueryForFieldId(fieldId);
		for (TrainingQueryInfo trainingQueryInfo : queriesForField) {
			oquery.setFieldValue(fieldId, trainingQueryInfo.getValue());
			String featureQuery = FeatureQuery.instantiate(templateFeatureQuery, oquery.getFieldValueMap());
			Instance inst = getFeatureInstanceForDoc(docId, featureQuery);
			System.out.println("Instance " + inst);
		}
		reader.close();
	}
	
	private Instance getFeatureInstanceForDoc(int docId, String featureQueryStr){
		try {
			OSQueryParser parser = new OSQueryParser();
			FeatureQuery featureQuery = parser.parseFeatureQuery(featureQueryStr);
//			System.out.println("Features : " + featureQuery.toString());
			List<FeatureValue> values = featureQuery.getFeaturesForDoc(reader, docId);
			values.add(0, new IntegerFeatureValue(docId));
			return new VectorInstance(values.toArray(new FeatureValue[]{}));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getFeatureQueryForField( int fieldId) {
		FeatureInfoManager fieldMan = new FeatureInfoManager();
		try {
			List<FeatureInfo> features = fieldMan.getFeaturesForField(fieldId);
			if (features.size() == 0)
				return null;
			String featureQuery = "";
			for (FeatureInfo featureInfo : features) {
				featureQuery = featureQuery + " \t" + featureInfo.getTemplate();
			}
			
			return featureQuery;
		} catch (SQLException e) {
			return null;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		int fieldId = 0;
		String arffFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-index")){
				indexId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-field")){
				fieldId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-output")){
				arffFile = args[i+1];
			}
		}
		
		if (domainId == 0 || indexId == 0 || arffFile == null){
			System.out.println("Usage : GenerateArffFromTrainingFeedback.java -domain [domainId] -index [indexId] -field [field id] -output [arff output file]");
			System.out.println("	  : generate arff from all examples from queries in this field");
			System.exit(1);
		}
		GenerateArffFromTrainingFeedback generator = new GenerateArffFromTrainingFeedback(domainId,indexId);
		System.out.println("Hello, i'm generating training oracle feedback for domain " + domainId + " index " + indexId + " all queries in field " + fieldId);	
		generator.generateForField(fieldId, arffFile);
//		generator.debug();
	}
}
