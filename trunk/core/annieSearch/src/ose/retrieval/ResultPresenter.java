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

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.IndexInfo;
import ose.index.IndexFieldConstant;
import ose.query.FeatureValue;

/**
 * @author Pham Kim Cuong
 *
 */
public class ResultPresenter {
	
	Map<Integer, List<FeatureValue> > docFeaturesMap;
	Map<Integer, Double> docScore;
	List<Integer> docIdList;
	private int startIndex ;
	private int howManyToReturn = 10;
	
	private RankingEvaluation evaluator = null;
	private boolean bReOrder = true;
	
	public ResultPresenter() {
		docScore = new HashMap<Integer, Double>();
		docFeaturesMap = new HashMap<Integer, List<FeatureValue>>();
		docIdList = new ArrayList<Integer>();
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	public void setReOrder(boolean reOrder) {
		bReOrder = reOrder;
	}
	
	public void setHowManyToReturn(int howManyToReturn) {
		this.howManyToReturn = howManyToReturn;
	}
	
	public void addDocScoreFeatures(int doc, double score, List<FeatureValue> fvalues ){
		docIdList.add(doc);
		docScore.put(doc, score);
		docFeaturesMap.put(doc, fvalues);
	}
	
	public String showResult(IndexReader reader) throws IOException{
		StringWriter writer = new StringWriter();
		showResult(reader, writer);
		writer.close();
		return writer.toString();
	}
	
	public void showResult(IndexReader reader, Writer writer) throws IOException{
		Integer[] order = getRankedOrder();
		writer.write("Result:\n");
		for (int i = 0; i < order.length; i++) {
			int docId = order[i];
			writer.write((i+1) + "\t Doc ID : " + docId + "\n");
			writer.write("\t Score : " + twoDecimal( docScore.get(docId) ) + "\n");
			writer.write("\t Features : " + docFeaturesMap.get(docId) + "\n");
			writer.write("\t Title : " + reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_TITLE) + "\n");
			writer.write("\t URL : " + reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_ID) + "\n\n");			
			if (i > howManyToReturn) break;
		}
		writer.write("\n");
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
		JSONArray jsonItems = new JSONArray();
		Integer[] order = getRankedList(indexPath, indexId, jsonItems);
		JSONObject resultJSON = new JSONObject();		
		resultJSON.put("records", jsonItems);		
		resultJSON.put("startIndex", startIndex );		
		resultJSON.put("sort","Score"); 
		resultJSON.put("dir","asc");
		resultJSON.put("recordsReturned" , Math.min(order.length - startIndex + 1, howManyToReturn) );
//		resultJSON.put("totalRecords" , Math.min(order.length - startIndex + 1, howManyToReturn) );
		resultJSON.put("totalRecords", order.length );
		if (evaluator != null){
			evaluator.evaluate(order);
			resultJSON.put("evaluation", evaluator.getJSONObject());
		}
		writer.write(resultJSON.toString());
		
	}

	/**
	 * @param indexPath
	 * @param indexId
	 * @param jsonItems
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws JSONException
	 */
	public Integer[] getRankedList(String indexPath, int indexId,
			JSONArray jsonItems) throws CorruptIndexException, IOException,
			JSONException {
		Integer[] order = getRankedOrder();
		IndexReader reader = IndexReader.open(indexPath);
		
		for (int i = startIndex; i < order.length; i++) {
		
			int docId = order[i];
			jsonItems.put(makeJsonObjectFromDocId(docId, i, reader, indexId));
			if (i - startIndex + 1 >= howManyToReturn)
				break;
		}
		reader.close();
		return order;
	}
	
	private JSONObject makeJsonObjectFromDocId(int docId, int i , IndexReader reader, int indexId) 
		throws JSONException, CorruptIndexException, IOException{
		JSONObject json = new JSONObject();
		json.put("No", i+1);
		json.put("DocId", docId);
		json.put("Score", twoDecimal( docScore.get(docId) ) );
		json.put("Features", docFeaturesMap.get(docId) );
		json.put("Title", reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_TITLE));
		json.put("CacheUrl", getCacheURL(indexId, docId));
		json.put("Url", reader.document(docId).get(IndexFieldConstant.FIELD_DOCUMENT_ID));
		
		return json;
	}
	
	public Integer [] getRankedOrder() {
		Integer[] order = docIdList.toArray(new Integer[]{});
		if ( bReOrder == true)
			Arrays.sort(order,new OrderComparator());
		return order;
	}
	
	public void addEvaluation(RankingEvaluation evaluator){
		this.evaluator = evaluator;
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
