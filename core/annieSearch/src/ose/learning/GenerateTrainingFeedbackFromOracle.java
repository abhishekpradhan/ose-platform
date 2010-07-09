package ose.learning;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import ose.database.TrainingFeedback;
import ose.database.TrainingFeedbackManager;
import ose.database.TrainingQueryInfo;
import ose.database.TrainingQueryInfoManager;
import ose.learning.OracleFeedback.AnnotatedDoc;
import ose.query.OQuery;


public class GenerateTrainingFeedbackFromOracle {
	private int domainId;
	private int indexId;
	private OQuery query;
	private TrainingFeedbackManager fbMan ;
	
	OracleFeedback oracle ;
	
	public GenerateTrainingFeedbackFromOracle(int domainId,int indexId) throws SQLException{
		this.domainId = domainId;
		this.indexId = indexId;
		
		
		fbMan = new TrainingFeedbackManager();
//		fbMan.setBatchUpdate(true);
		oracle = new OracleFeedback(domainId, indexId);
		oracle.fetchTagsFromDatabase();
	}
	
	public void generateForQuery(int queryId) throws IOException, SQLException{
		TrainingQueryInfo queryInfo = new TrainingQueryInfoManager().getById(queryId);
		if (queryInfo == null)
			throw new RuntimeException("No query for " + queryId + " is found. ");
		
		query = new OQuery(domainId );
		query.setFieldValue(queryInfo.getFieldId(), queryInfo.getValue());
		
		List<TrainingFeedback> batch = new ArrayList<TrainingFeedback>();
		
		Map<Integer, AnnotatedDoc> annotatedDocs = oracle.getAnnotatedDocs();
		for (Entry<Integer,AnnotatedDoc> entry : annotatedDocs.entrySet()) {
			boolean relevant = oracle.getOracleFeedback(query, entry.getKey());
			TrainingFeedback feedback = new TrainingFeedback(queryId, entry.getKey(), indexId, domainId, relevant);
			System.out.println("Inserting " + feedback + "\t" + entry.getValue());
			batch.add(feedback);
			
			if (batch.size() == 500){
				fbMan.insertBatch(batch);
				batch.clear();
			}
		}
		if (batch.size() > 0)
			fbMan.insertBatch(batch); //commit left-over batch;
		
		System.out.println("Number of annotated documents : " + annotatedDocs.size());
		
	}

	public void generateForField(int fieldId) throws SQLException, IOException{
		List<TrainingQueryInfo> queriesForField = new TrainingQueryInfoManager().getTrainingQueryForFieldId(fieldId);
		for (TrainingQueryInfo trainingQueryInfo : queriesForField) {
			generateForQuery(trainingQueryInfo.getTrainingQueryId());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		int queryId = 0;
		Set<Integer> fieldIds = new HashSet<Integer>();
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
			else if (args[i].equals("-field")){
				String arg = args[i+1];
				for (String ids : arg.split(",")){
					if (ids.trim().length() == 0) continue;
					fieldIds.add(Integer.parseInt(ids));
				}
			}
		}
		
		if (domainId == 0 || indexId == 0){
			System.out.println("Usage : GenerateTrainingFeedbackFromOracle.java -domain [domainId] -index [indexId] -query [query id]");
			System.out.println("Usage : GenerateTrainingFeedbackFromOracle.java -domain [domainId] -index [indexId] -field [field id]");
			System.out.println("	  : generate for all queries for the field");
			System.exit(1);
		}
		GenerateTrainingFeedbackFromOracle generator = new GenerateTrainingFeedbackFromOracle(domainId,indexId);
		if (fieldIds.size() == 0){
			System.out.println("Hello, i'm generating training oracle feedback for domain " + domainId + " index " + indexId + " query " + queryId);	
			generator.generateForQuery(queryId);
		}
		else {
			for (Integer fieldId : fieldIds){
				System.out.println("Hello, i'm generating training oracle feedback for domain " + domainId + " index " + indexId + " all queries in field " + fieldId);	
				generator.generateForField(fieldId);
			}
		}
	}
}
