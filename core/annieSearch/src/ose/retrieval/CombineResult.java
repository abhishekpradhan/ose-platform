/**
 * 
 */
package ose.retrieval;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.IndexInfo;
import ose.index.IndexFieldConstant;
import ose.learning.DoubleFeatureValue;
import ose.learning.VectorInstance;
import ose.query.FeatureValue;

/**
 * @author Pham Kim Cuong
 *
 */
public class CombineResult {
	
	ArrayList<Double> /* lowerBound, upperBound,*/ weight;
	ArrayList<Boolean> isNullPredicate; 
	Map<Integer, VectorInstance> instanceMap;
	Map<Integer, Map<Integer, List<FeatureValue> > > segmentedInstanceMap;
	Map<Integer, Double> docScore;
	
	private int startIndex ;
	private int queryIdForEvaluation;
	private int indexIdForEvaluation;
	private int howManyToReturn = 10;
	
	public CombineResult(int numberOfFields) {
		weight = new ArrayList<Double>(numberOfFields);
		isNullPredicate = new ArrayList<Boolean>(numberOfFields);
		for (int i = 0; i < numberOfFields; i++) {
			weight.add(0.0);
			isNullPredicate.add(false);
		}
		
		instanceMap = new HashMap<Integer, VectorInstance>();
		segmentedInstanceMap = new HashMap<Integer, Map<Integer,List<FeatureValue>>>();
		docScore = new HashMap<Integer, Double>();
		indexIdForEvaluation = -1;
		queryIdForEvaluation = -1;
	}
	
	public void setWeight(int nth, double w){
		weight.set(nth, w);
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	public void setHowManyToReturn(int howManyToReturn) {
		this.howManyToReturn = howManyToReturn;
	}
	
	public int getQueryIdForEvaluation() {
		return queryIdForEvaluation;
	}
	
	public void setQueryIdForEvaluation(int queryIdForEvaluation) {
		this.queryIdForEvaluation = queryIdForEvaluation;
	}
	
	public int getIndexIdForEvaluation() {
		return indexIdForEvaluation;
	}
	
	public void setIndexIdForEvaluation(int indexIdForEvaluation) {
		this.indexIdForEvaluation = indexIdForEvaluation;
	}
	
	public VectorInstance addInstance(int doc, VectorInstance instance, Map<Integer, List<FeatureValue> > segmentedFeatures){
		
		VectorInstance pruned = new VectorInstance(instance);
		double s = getCombinedScore(pruned);
		docScore.put(doc, s);
		instanceMap.put(doc, pruned);
		segmentedInstanceMap.put(doc, segmentedFeatures);
		return pruned;
	}
	
	public void setNullKey(int key){
		isNullPredicate.set(key,true);
	}
	
	public VectorInstance addInstanceWithFeatures(int doc, List<FeatureValue> fvalues ){
		double s = 0;
		for (FeatureValue value : fvalues) {
			if (value != null) s += value.toNumber();
		}
		docScore.put(doc, s);
		VectorInstance inst = new VectorInstance(new FeatureValue[]{});
		instanceMap.put(doc, inst);
		segmentedInstanceMap.put(doc, new HashMap<Integer, List<FeatureValue>>());
		return inst;
	}
	
	public double getCombinedScore(VectorInstance instance){
		int n = weight.size();
		double score = 0.0;
		for (int i = 0; i < n; i++) {
			if (isNullPredicate.get(i)) {
				instance.setFeature(i, new DoubleFeatureValue(1.0));
			}
			else{
				FeatureValue f = instance.getFeature(i);
				double v ;
				if (f == null){
					v = 0;
				}
				else{
					v = 1 / (1 + Math.exp(-f.toNumber()));
				}
				
				instance.setFeature(i, new DoubleFeatureValue(v));
				double alpha = weight.get(i);
				score +=  Math.log( alpha *  v  + (1 - alpha) * 0.5 );
			}
		}
		return Math.exp( score );
	}
	
	public String showResult(IndexReader reader) throws IOException{
		StringWriter writer = new StringWriter();
		showResult(reader, writer);
		writer.close();
		return writer.toString();
	}
	
	public void showResult(IndexReader reader, Writer writer) throws IOException{
		Integer[] order = docScore.keySet().toArray(new Integer[]{});
		Arrays.sort(order,new OrderComparator());
		writer.write("Result:\n");
		for (int i = 0; i < order.length; i++) {
			int docId = order[i];
			writer.write((i+1) + "\t Doc ID : " + docId + "\n");
			writer.write("\t Score : " + twoDecimal( docScore.get(docId) ) + "\n");
			writer.write("\t Title : " + reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_TITLE) + "\n");
			writer.write("\t URL : " + reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_ID) + "\n\n");			
			if (i > howManyToReturn) break;
		}
		writer.write("\n");
	}
	
	public void showFeatureMap(Writer writer, Map<Integer, List<FeatureValue> >  featureMap) throws IOException{
		SortedSet<Integer> keys = new TreeSet<Integer>(featureMap.keySet());
		writer.write("{");
		for (Integer key : keys) {
			writer.write(key + ":" + featureMap.get(key)+ ",");
		}
		writer.write("}");
	}
	
	public String showJSONResult(IndexInfo iinfo) throws IOException, SQLException, JSONException{
		StringWriter writer = new StringWriter();
		showJSONResult(iinfo, writer);
		writer.close();
		return writer.toString();
	}
	
	public void showJSONResult(IndexInfo iinfo, Writer writer) throws IOException , SQLException, JSONException{
		String indexPath = iinfo.getIndexPath();
		int indexId = iinfo.getIndexId();
		showJSONResult(writer, indexPath, indexId);
	}

	public void showJSONResult(Writer writer,
			String indexPath, int indexId) throws CorruptIndexException,
			IOException , JSONException{
		IndexReader reader = IndexReader.open(indexPath);
		Integer[] order = getRankedOrder();
		ObjectRankingEvaluation evaluation = new ObjectRankingEvaluation(queryIdForEvaluation, indexIdForEvaluation);
		evaluation.evaluate(order);		
		JSONArray jsonItems = new JSONArray();
		if (startIndex == -1){//show relevant docs only
			for (int i = 0; i < order.length; i++) {
				int docId = order[i];
				if (!evaluation.isRelevant(docId)) continue;
				
				jsonItems.put(makeJsonObjectFromDocId(docId, i, reader, indexId));
			}		
		}
		else{
			for (int i = startIndex; i < order.length; i++) {
			
				int docId = order[i];
				jsonItems.put(makeJsonObjectFromDocId(docId, i, reader, indexId));
				if (i - startIndex + 1 >= howManyToReturn)
					break;
			}		
		}
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("items", jsonItems);
		resultJSON.put("evaluation", evaluation.getJSONObject());
		resultJSON.put("queryId", queryIdForEvaluation );
		resultJSON.put("indexId", indexIdForEvaluation );
		resultJSON.put("startIndex", startIndex );
		
		resultJSON.put("recordsReturned" , Math.min(order.length - startIndex + 1, howManyToReturn) );
		resultJSON.put("totalRecords", order.length );
		writer.write(resultJSON.toString());
		reader.close();
	}
	
	private JSONObject makeJsonObjectFromDocId(int docId, int i , IndexReader reader, int indexId) 
		throws JSONException, CorruptIndexException, IOException{
		JSONObject json = new JSONObject();
		json.put("No", i+1);
		json.put("DocId", docId);
		json.put("Score", twoDecimal( docScore.get(docId) ) );
		json.put("Title", reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_TITLE));
		json.put("CacheUrl", getCacheURL(indexId, docId));
		json.put("Url", reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_ID));
		
		return json;
	}
	
	public Integer [] getRankedOrder() {
		Integer[] order = docScore.keySet().toArray(new Integer[]{});
		Arrays.sort(order,new OrderComparator());
		return order;
	}
	
	private static String getCacheURL(int indexId, int docId){
		return "/annieWeb/CacheServlet?indexId=" + indexId + "&docId=" + docId;	
	}
	
	private double twoDecimal(double s) {
		return ((int) (s * 1000)) / 1000.0;
	}
	
	class OrderComparator implements Comparator<Integer>{
		public int compare(Integer o1, Integer o2) {
			double t = - (docScore.get(o1) - docScore.get(o2) ) ; //reverse order
			if (t < 0)
				return -1;
			else if (t > 0)
				return 1;
			else
				return 0;
		}
	}

}
