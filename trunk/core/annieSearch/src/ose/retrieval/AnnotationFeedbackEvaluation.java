/**
 * 
 */
package ose.retrieval;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import ose.database.TrainingFeedback;
import ose.database.TrainingFeedbackManager;
import ose.utils.JsonIO;

/**
 * @author Pham Kim Cuong
 *
 */
public class AnnotationFeedbackEvaluation implements RankingEvaluation{
	
	private List<Integer> relevantDocIds;  
	private List<Integer> notRelevantDocIdsInFeedback;
	private List<Integer> relevantPosition;
	private int queryId, indexId;
	
	public AnnotationFeedbackEvaluation(int queryId, int indexId){
		this.queryId = queryId;
		this.indexId = indexId;
	}
	
	public double evaluate(Integer [] rankedDocIds){
		try {
			relevantDocIds = new ArrayList<Integer>();
			relevantPosition = new ArrayList<Integer>();
			TrainingFeedbackManager fbMan = new TrainingFeedbackManager();
			List<TrainingFeedback> irrelevants = fbMan.query("SELECT * from TrainingFeedback WHERE QueryId = " + queryId + " AND IndexId = " + indexId + " AND Relevant = 0");
			Set<Integer> irSet = convertListToSetId(irrelevants);
			notRelevantDocIdsInFeedback = new ArrayList<Integer>();
			List<TrainingFeedback> allFeedbacks = fbMan.query("SELECT * from TrainingFeedback WHERE QueryId = " + queryId + " AND IndexId = " + indexId + " AND Relevant = 1");
			if (allFeedbacks.size() > 0){
				Set<Integer> allRelevant = convertListToSetId(allFeedbacks);
				int countRelevant = 0;
				int numSofar = 0;
				
				for (Integer docId : rankedDocIds) {
					numSofar += 1;
					if (allRelevant.contains(docId) || irSet.contains(docId)){
						if (allRelevant.contains(docId)){
							countRelevant += 1;
							relevantDocIds.add(docId);
							relevantPosition.add(numSofar);						
						}
						if (irSet.contains(docId)){
							notRelevantDocIdsInFeedback.add(docId);
						}
					}
					
				}
				return 1.0;
			} else {
				return -0.1; //this means no relevant docs to evaluate
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean isRelevant(int docId){
		return (relevantDocIds != null && relevantDocIds.contains(docId));
	}
	
	public JSONObject getJSONObject() throws JSONException{
		JSONObject json = new JSONObject();
		json.put("relevantDocs", relevantDocIds);
		json.put("relevantPosition",relevantPosition); 
		json.put("nonRelevantDocs",notRelevantDocIdsInFeedback);
		json.put("queryId",queryId);
		json.put("indexId",indexId);
		return json;
	}

	public void prettyPrint(){
		System.out.println("relevantDocs : " + JsonIO.toJSONString(relevantDocIds)); 
		System.out.println("relevantPosition : " + JsonIO.toJSONString(relevantPosition)); 
		System.out.println("nonRelevantDocs : " + JsonIO.toJSONString(notRelevantDocIdsInFeedback));
		
	}
	
	private Set<Integer> convertListToSetId(List<TrainingFeedback> fbList){
		SortedSet<Integer> result = new TreeSet<Integer>();
		for (TrainingFeedback fb : fbList) {
			result.add(fb.getDocId());
		}
		return result;
	}
}
