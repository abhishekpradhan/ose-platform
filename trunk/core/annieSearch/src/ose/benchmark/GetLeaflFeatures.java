package ose.benchmark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryTreeParser;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.LogisticRankingQuery;
import ose.processor.cascader.OSHits;
import ose.query.Constant;
import ose.retrieval.ResultPresenter;

public class GetLeaflFeatures {

	Set<String> leafFeatureSet = new TreeSet<String>();
	int countLeaf = 0;
	
	public void showLeafFeatures(int indexId, int domainId, String objectQuery) throws Exception{
		String featureQuery = getFeatureQueryForObjectQuery(domainId, objectQuery);
		printAllLeaves(featureQuery);
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
			if (fieldName.equals(Constant.OSFIELD_OTHER)){
				fquery += featureValue + " ";
			}
			else {
				if (!objectQueryMap.containsKey(fieldName)){
					throw new RuntimeException("fieldName " + fieldName + " is not found in the query " + objectQuery);
				}
				String fieldValue = objectQueryMap.get(fieldName);
				if (fieldValue.trim().length() == 0){
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
	
	private void printAllLeaves(String featureQuery){
		QueryTreeParser parser = new QueryTreeParser(featureQuery);
		ParentNode node = parser.parseQueryNode();
		printAllLeaves(node);
		System.out.println("Total leaf : " + countLeaf);
		int count = 0;
		for (String f : leafFeatureSet){
			count += 1;
			System.out.println("--- " + count + " : " + f);
		}
		
		for (String f : leafFeatureSet){
			System.out.print(" %BooleanFeature(" + f + ")");
		}
		System.out.println();
		
	}
	
	private void printAllLeaves(ParentNode node){
		boolean isLeafFeature = true;
		for (ParsingNode child : node.getChildren()){
			if (child instanceof ParentNode && !child.getNodeName().startsWith("_range")) {
				ParentNode pnode = (ParentNode) child;
				printAllLeaves(pnode);
				isLeafFeature = false;
			}
		}
		
		if (isLeafFeature){
//			System.out.println("---- " + node.reconstructString());
			leafFeatureSet.add(node.reconstructString());
			countLeaf += 1;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		GetLeaflFeatures evaluator = new GetLeaflFeatures();
		String objectQuery = "{'model':'','moni':'_range(12.1,15.6)', 'price':'_range(563.83,1597.94)', 'brand':'lenovo', 'hdd':'_range(119.95,384.05)', 'proc':'_range(1.95,2.55)'}";
		int indexId = 10000;
		int domainId = 3;
		evaluator.showLeafFeatures(indexId, domainId, objectQuery);
	}

}
