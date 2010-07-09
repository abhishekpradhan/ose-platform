package ose.retrieval;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.ModelInfo;
import ose.database.ModelInfoManager;
import ose.parser.OSQueryParser;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.LogisticRankingQuery;
import ose.processor.cascader.OSHits;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class ObjectSearcher{
	 public static final String OSFIELD_OTHER = "other";

	public ObjectSearcher() {
	}   	
	
	public ResultPresenter getResultForQuery(int indexId, int domainId, String objectQuery) throws Exception{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		OSQueryParser parser = new OSQueryParser();
		List<String> fieldNames = new FieldInfoManager().getFieldNamesForDomain(domainId);
		FeatureQuery osQuery = parser.parseFeatureQuery(getFeatureQueryForObjectQuery(domainId, objectQuery) );
		List<ModelInfo> models = new ModelInfoManager().getModelsForDomainId(domainId);
		List<String> paths = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for (ModelInfo modelInfo : models){
			paths.add(modelInfo.getPath());
			weights.add(modelInfo.getWeight());
		}
		LogisticRankingQuery query = new LogisticRankingQuery(osQuery.getPredicates(), paths.toArray(new String[]{}), 
				fieldNames.toArray(new String[]{}),
				weights.toArray(new Double[]{}));

		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		
		long startTime = System.currentTimeMillis();			
		OSHits result = query.search(reader);			
		long endTime = System.currentTimeMillis();
		
		ResultPresenter scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		int i = 0;
		for (Document doc : result) {			
			scoredResult.addDocScoreFeatures(result.getDocID(), result.score(), result.docFeatures());
			i += 1;
		}
		System.out.println("Total documents : " + i + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
		return scoredResult;
	}

	
	
	private Map<String, String> convertObjectQueryJsonToMap(String objectQuery) throws JSONException{
		Map<String, String> res = new HashMap<String, String>();
		JSONObject oquery = new JSONObject(objectQuery);
		JSONArray names = oquery.names();
		for (int i = 0 ; i < names.length() ; i++){
			String fieldName = names.getString(i);
			String fieldValue = oquery.getString(fieldName);
			res.put(fieldName, fieldValue);
		}
		return res;
	}
	
	private Map<Integer, String> getFieldIdNameMapForDomain(int domainId) throws SQLException{
		Map<Integer, String> idNameMap = new HashMap<Integer, String>();
		List<FieldInfo> fields = new FieldInfoManager().query("select * from FieldInfo " +
				"where domainId = " + domainId + 
				" order by fieldId");
		for (FieldInfo fieldInfo : fields) {
			idNameMap.put(fieldInfo.getFieldId(), fieldInfo.getName());
		}
		return idNameMap;
	}
	
	private String getFeatureQueryForObjectQuery(int domainId, String objectQuery) throws SQLException, JSONException{
		Map<String, String> objectQueryMap = convertObjectQueryJsonToMap(objectQuery);
		Map<Integer, String> fieldIdToNameMap = getFieldIdNameMapForDomain(domainId);
		List<FeatureInfo> features = new FeatureInfoManager().query("select FeatureInfo.* from FeatureInfo, FieldInfo" +
				" where IsDeleted = 0 " +
				"   and FeatureInfo.FieldId = FieldInfo.FieldId" +
				"   and FieldInfo.DomainId = " + domainId + 
				" order by FeatureId");
		String fquery = "";
		Map<Integer, Boolean> fieldSkipped = new HashMap<Integer, Boolean>(); 
		for (FeatureInfo featureInfo : features) {
			int fieldId = featureInfo.getFieldId();
			if (fieldSkipped.containsKey(fieldId)) continue;
			if (!fieldIdToNameMap.containsKey(fieldId)){
				throw new RuntimeException("fieldId " + fieldId + " does not belong to domain " + domainId);
			}
			String featureValue = featureInfo.getTemplate();
			String fieldName = fieldIdToNameMap.get(fieldId);
			if (fieldName.equals(OSFIELD_OTHER)){
				fquery += featureValue + " ";
			}
			else {
//				if (!objectQueryMap.containsKey(fieldName)){
//					throw new RuntimeException("fieldName " + fieldName + " is not found in the query " + objectQuery);
//				}
				String fieldValue = objectQueryMap.get(fieldName);
				if (fieldValue  == null || fieldValue.trim().length() == 0){
					fquery += "%Null() ";
					fieldSkipped.put(fieldId, true);
				}
				else{
					
					featureValue = featureValue.replaceAll("(?!\\W)" + fieldName.toUpperCase() + "(?=\\W)", fieldValue);
					fquery += featureValue + " ";
				}
			}
		}
		System.out.println("Fquery : " + fquery);
		return fquery;
	}
	
	public static void main(String[] args) throws Exception{
		int indexId = 8;
		int domainId = 1;
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		ObjectSearcher servlet = new ObjectSearcher();
		String objectQuery = "{'brand':'','model':'','mpix':'_range(4,22)','zoom':'','price':'_range(0,300)'}";
		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  objectQuery);
		System.out.println(" --- " + result.showJSONResult(iinfo));
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		System.out.println(result.showResult(reader));
		reader.close();
	}
}