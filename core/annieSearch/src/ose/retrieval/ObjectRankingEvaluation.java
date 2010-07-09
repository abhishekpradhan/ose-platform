/**
 * 
 */
package ose.retrieval;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import ose.database.Feedback;
import ose.database.FeedbackManager;
import ose.utils.JsonIO;

/**
 * @author Pham Kim Cuong
 *
 */
public class ObjectRankingEvaluation implements RankingEvaluation{
	
	private List<Integer> relevantDocIds;  
	private List<Integer> notRelevantDocIdsInFeedback;
	private List<Integer> relevantPosition;
	private final Integer [] AP_RANK = new Integer[] {5,10,20,50,100,200,300};
	private double [] precisionAt; 
	private double avgPres;
	private int queryId, indexId;
	
	public ObjectRankingEvaluation(int queryId, int indexId){
		avgPres = -1;
		this.queryId = queryId;
		this.indexId = indexId;
	}
	
	public double evaluate(Integer [] rankedDocIds){
		try {
			avgPres = -1;
			relevantDocIds = new ArrayList<Integer>();
			relevantPosition = new ArrayList<Integer>();
			precisionAt = new double[AP_RANK.length];
			FeedbackManager fbMan = new FeedbackManager();
			List<Feedback> irrelevants = fbMan.query("SELECT * from Feedback WHERE QueryId = " + queryId + " AND IndexId = " + indexId + " AND Relevant = 0");
			Set<Integer> irSet = convertListToSetId(irrelevants);
			notRelevantDocIdsInFeedback = new ArrayList<Integer>();
			List<Feedback> allFeedbacks = fbMan.query("SELECT * from Feedback WHERE QueryId = " + queryId + " AND IndexId = " + indexId + " AND Relevant = 1");
			if (allFeedbacks.size() > 0){
				Set<Integer> allRelevant = convertListToSetId(allFeedbacks);
				double sumPrecision = 0.0;
				int countRelevant = 0;
				int numSofar = 0;
				int posWithFB = 0;
				
				for (Integer docId : rankedDocIds) {
					numSofar += 1;
					if (allRelevant.contains(docId) || irSet.contains(docId)){
						posWithFB += 1;
						if (allRelevant.contains(docId)){
							countRelevant += 1;
							sumPrecision += 1.0 * countRelevant / posWithFB;
							relevantDocIds.add(docId);
							relevantPosition.add(numSofar);						
						}
						for (int i = 0; i < AP_RANK.length; i++) {
							if (posWithFB == AP_RANK[i]){
								precisionAt[i] = countRelevant * 1.0 /posWithFB;
							}
						}
						if (irSet.contains(docId)){
							notRelevantDocIdsInFeedback.add(numSofar);
						}
					}
					
				}
				if (countRelevant > 0) 
					avgPres = sumPrecision/countRelevant;
				return avgPres;
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
		json.put("averagePrecision",avgPres); 
		json.put("relevantDocs", relevantDocIds);
		json.put("relevantPosition",relevantPosition); 
		json.put("nonRelevantDocs",notRelevantDocIdsInFeedback);
		json.put("queryId",queryId);
		json.put("indexId",indexId);
		return json;
	}

	public void prettyPrint(){
		System.out.println("averagePrecision : " + avgPres);
		for (int i = 0; i < precisionAt.length; i++) {
			System.out.println("\t Pres@" + AP_RANK[i] + " : " + precisionAt[i] * 100 + "%");
		}
		System.out.println("relevantDocs : " + JsonIO.toJSONString(relevantDocIds)); 
		System.out.println("relevantPosition : " + JsonIO.toJSONString(relevantPosition)); 
		System.out.println("nonRelevantDocs : " + JsonIO.toJSONString(notRelevantDocIdsInFeedback));
		
	}
	
	private Set<Integer> convertListToSetId(List<Feedback> fbList){
		SortedSet<Integer> result = new TreeSet<Integer>();
		for (Feedback fb : fbList) {
			result.add(fb.getDocId());
		}
		return result;
	}
}
