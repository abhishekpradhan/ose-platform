package ose.learning;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONException;

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
import ose.learning.OracleFeedback.AnnotatedDoc;
import ose.query.OQuery;


public class QueryGenerator {

	private int domainId;
	private int indexId;
	private String attributeFile;
	private OQuery query;
	
	public QueryGenerator(int domainId,int indexId, String fileName) throws SQLException{
		this.domainId = domainId;
		this.indexId = indexId;
		attributeFile = fileName;
		
	}
	
	public void generate() throws IOException, SQLException, JSONException{
		int numberOfQueries = 400;
		Map<String, List<String> > candidateValues = new HashMap<String, List<String>>();
		List<OQuery> queries = new ArrayList<OQuery>();
		
		BufferedReader reader = new BufferedReader(new FileReader(attributeFile));
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		
		while (true){
			String field = reader.readLine();
			if (field == null) break;
			String [] splitted = field.split(" ");
			String fieldName = splitted[1];
			String fieldType = splitted[2];
			List<String> list = new ArrayList<String>();
			
			String valueLine = reader.readLine();
			if (valueLine== null) break;
			String [] values = valueLine.split(",");
			for (int i = 0; i < numberOfQueries; i++) {
				if (fieldType.equals("number")){
					int lower = random.nextInt(values.length);
					int higher = lower + random.nextInt(values.length - lower);
					list.add("_range(" + values[lower] + "," + values[higher] +")");
				}
				else{
					list.add(values[ random.nextInt(values.length)]);
				}
			}
			candidateValues.put(fieldName, list);
		}
		
//		Set<String> restriction = new HashSet<String>(Arrays.asList(new String [] {"brand","moni", "hdd","proc","price"}));
		Set<String> restriction = new HashSet<String>(Arrays.asList(new String [] {"brand","moni", "hdd","proc"}));
//		Set<String> restriction = new HashSet<String>(Arrays.asList(new String [] {"brand","moni","price"}));
//		Set<String> restriction = new HashSet<String>(Arrays.asList(new String [] {"model","price"}));
		OracleFeedback oracle = new OracleFeedback(domainId, indexId);
		oracle.fetchTagsFromDatabase();
		Map<Integer, AnnotatedDoc> allDocs = oracle.getAnnotatedDocs();
		System.out.println("Number of docs " + allDocs.size());
		for (int i = 0; i < numberOfQueries; i++) {
			OQuery query = new OQuery();
			for (String field : candidateValues.keySet()){
				if (restriction.contains(field))
					query.setFieldValue(field, candidateValues.get(field).get(i));
			}
			int nmatch = 0;
			
			for (Integer docId : allDocs.keySet()){
				if (oracle.getOracleFeedback(query, docId))
					nmatch += 1;
			}
			if (nmatch > 0)
				System.out.println("Count : " + nmatch + "\t OQuery " + query.getJsonString());
			queries.add(query);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		String attributeFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-index")){
				indexId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-attributeFile")){
				attributeFile = args[i+1];
			}
		}
		
		if (domainId == 0 || indexId == 0){
			System.out.println("Usage : QueryGenerator.java -domain [domainId] -index [indexId] -attributeFile [attribute file]");
			System.exit(1);
		}
		System.out.println("Hello, i'm generating random query for domain " + domainId + " index " + indexId + " attrFile " + attributeFile);
	
		QueryGenerator generator = new QueryGenerator(domainId,indexId,attributeFile);
		generator.generate();
	}
}
