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

public class EvalFeatureQuery {

	Set<String> leafFeatureSet = new TreeSet<String>();
	int countLeaf = 0;
	
	public EvalFeatureQuery() {
		// TODO Auto-generated constructor stub
	}
	
	public void runQuery(int indexId, String featureQuery) throws Exception{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery osQuery = parser.parseFeatureQuery(featureQuery );
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		long startTime = System.currentTimeMillis();			
		OSHits result = osQuery.search(reader);			
		long endTime = System.currentTimeMillis();
		ResultPresenter scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		int i = 0;
		for (Document doc : result) {			
			i += 1;
		}
		System.out.println("Total documents : " + i + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
	}
	
	public void runQuery(int indexId, int domainId, String objectQuery) throws Exception{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		OSQueryParser parser = new OSQueryParser();
		List<String> fieldNames = new FieldInfoManager().getFieldNamesForDomain(domainId);
		String featureQuery = getFeatureQueryForObjectQuery(domainId, objectQuery);
		FeatureQuery osQuery = parser.parseFeatureQuery(featureQuery );
		printAllLeaves(featureQuery);
		if (true) return;
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
		EvalFeatureQuery evaluator = new EvalFeatureQuery();
		String objectQuery = "{'model':'','moni':'_range(12.1,15.6)', 'price':'_range(563.83,1597.94)', 'brand':'lenovo', 'hdd':'_range(119.95,384.05)', 'proc':'_range(1.95,2.55)'}";
//		evaluator.runQuery(10000, 3	, objectQuery);
		String featureQuery1 = "%BooleanFeature(HTMLTitle('\"',in,inch)) %BooleanFeature(HTMLTitle(gb)) %BooleanFeature(HTMLTitle(ghz)) %BooleanFeature(HTMLTitle(laptop)) %BooleanFeature(HTMLTitle(lenovo)) %BooleanFeature(Number_body(_range(1.95,2.55))) %BooleanFeature(Number_body(_range(119.95,384.05))) %BooleanFeature(Number_body(_range(12.1,15.6))) %BooleanFeature(Number_body(_range(563.83,1597.94))) %BooleanFeature(Number_title(_range(1.95,2.55))) %BooleanFeature(Number_title(_range(119.95,384.05))) %BooleanFeature(Number_title(_range(12.1,15.6))) %BooleanFeature(Phrase('price :')) %BooleanFeature(Phrase(processor speed)) %BooleanFeature(Phrase(screen,size)) %BooleanFeature(Token('\"',in,inch)) %BooleanFeature(Token('$')) %BooleanFeature(Token(availability)) %BooleanFeature(Token(descriptions,description,specification,specifications)) %BooleanFeature(Token(display)) %BooleanFeature(Token(drive,hdd)) %BooleanFeature(Token(gb)) %BooleanFeature(Token(ghz)) %BooleanFeature(Token(laptop)) %BooleanFeature(Token(lenovo)) %BooleanFeature(Token(manufacturer)) %BooleanFeature(Token(price)) %BooleanFeature(Token(product)) %BooleanFeature(Token(rpm)) %BooleanFeature(Token(warranty)) %BooleanFeature(Token(widescreen,wxga,xga,tft))";

		String featureQuery2 = "%BooleanFeature(HTMLTitle(lenovo)) %BooleanFeature(Proximity(Token(lenovo),Token(manufacturer),-3,3)) %BooleanFeature(Token(lenovo)) %BooleanFeature(Proximity(Number_body(_range(12.1,15.6)),Token('\"',in,inch),-2,0)) %BooleanFeature(Proximity(Number_title(_range(12.1,15.6)),HTMLTitle('\"',in,inch),-2,0)) %BooleanFeature(Proximity(Number_body(_range(12.1,15.6)),Or(Phrase(screen size),Token(display)),0,7)) %BooleanFeature(Proximity(Number_body(_range(12.1,15.6)),Token(widescreen,wxga,xga,tft),-8,0)) %BooleanFeature(Proximity(Number_body(_range(119.95,384.05)),Token(gb),-2,0)) %BooleanFeature(Proximity(Number_title(_range(119.95,384.05)),HTMLTitle(gb),-2,0)) %BooleanFeature(Proximity(Number_body(_range(119.95,384.05)),Token(rpm),-5,5)) %BooleanFeature(Proximity(Number_body(_range(119.95,384.05)),Token(drive,hdd),-5,5)) %BooleanFeature(Proximity(Phrase('processor speed'),Number_body(_range(1.95,2.55)),-4,0)) %BooleanFeature(Proximity(Number_body(_range(1.95,2.55)),Token(ghz),-2,0)) %BooleanFeature(Proximity(Number_title(_range(1.95,2.55)),HTMLTitle(ghz),-2,0)) %BooleanFeature(Phrase(Token('$'),Number_body(_range(563.83,1597.94)))) %BooleanFeature(Proximity(Token(price),Phrase(Token('$'),Number_body(_range(563.83,1597.94))),-4,3)) %BooleanFeature(Phrase(Top(Token('$'),3),Number_body(_range(563.83,1597.94)))) %BooleanFeature(Proximity(Top(Phrase('price :'),1),Number_body(_range(563.83,1597.94)),-10,0)) %BooleanFeature(HTMLTitle(laptop))  %BooleanFeature(Token(availability))  %BooleanFeature(Token(laptop))  %BooleanFeature(Phrase(Token(product),Token(descriptions,description,specification,specifications)))  %BooleanFeature(Token(warranty))  %BooleanFeature(Token(manufacturer))";
		
		String featureQuery3 = "%BooleanFeature(Phrase(Token(display),Number_body(_range(100,200)))) %BooleanFeature(Phrase(Number_body(_range(100,200)),Token(camera)))";
//		evaluator.runQuery(10000, featureQuery1);
//		evaluator.runQuery(10000, featureQuery2);
		evaluator.runQuery(10000, featureQuery3);
	}

}
