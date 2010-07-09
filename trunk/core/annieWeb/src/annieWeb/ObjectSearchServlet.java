package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.DomainInfo;
import ose.database.DomainInfoManager;
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
import ose.query.Constant;
import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.RankingEvaluation;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class ObjectSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 

	static private final long serialVersionUID = 12312;
	 
	public ObjectSearchServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		String objectQuery = request.getParameter("oquery");
		objectQuery = new String(objectQuery.getBytes("8859_1"), "UTF8");
		if (objectQuery == null){ 
			System.out.println("No query given");
			return;
		}
		String indexIdStr = request.getParameter("indexId");
		if (indexIdStr == null){
			System.out.println("No index given");
			return;
		}
		int indexId = Integer.parseInt(indexIdStr);
		String domainIdStr = request.getParameter("domainId");
		if (domainIdStr == null){
			System.out.println("No domain given");
			return;
		}
		int domainId = Integer.parseInt(domainIdStr);
		int startIndex = -1;
		if (request.getParameter("start") != null){
			startIndex = Integer.parseInt(request.getParameter("start"));
		}
		int nRecords = -1;
		if (request.getParameter("nRecords") != null){
			nRecords = Integer.parseInt(request.getParameter("nRecords"));
		}
		
		int queryId = -1;
		if (request.getParameter("queryId") != null){
			queryId = Integer.parseInt(request.getParameter("queryId"));
		}
		try {
			ResultPresenter resultPrinter = getResultForQuery(indexId, domainId, objectQuery);
			
			PrintWriter out = response.getWriter();	
			if (startIndex != -1)
				resultPrinter.setStartIndex(startIndex);
			if (nRecords != -1)
				resultPrinter.setHowManyToReturn(nRecords);
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			if (queryId !=  -1)
				resultPrinter.addEvaluation(new ObjectRankingEvaluation(queryId, indexId));
			resultPrinter.showJSONResult(iinfo, out);
			out.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("Done !");
		}
		
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

	
	
	static public Map<String, String> convertObjectQueryJsonToMap(String objectQuery) throws JSONException{
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
		Set<Integer> orderedKeys = new TreeSet<Integer>(fieldIdToNameMap.keySet());
		String fquery = "";
		Map<Integer, Boolean> fieldSkipped = new HashMap<Integer, Boolean>();
		
		for (Integer fieldId : orderedKeys){
			List<FeatureInfo> features = new FeatureInfoManager().getFeatureForFieldId(fieldId);
			
			if (features == null || features.size() == 0){
				fquery += "%Null() ";
			}
			else {
				for (FeatureInfo featureInfo : features) {				
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
			}
		}
		
		System.out.println("Fquery : " + fquery);
		return fquery;
	}
	
	public static void main(String[] args) throws Exception{
		int indexId = 602;
		int domainId = 6;
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		ObjectSearchServlet servlet = new ObjectSearchServlet();
		String objectQuery = "{'cnum':'','cont':'','dept':'','loca':'','name':'','prof':'','sem':'','univ':'','year':''}";
		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  objectQuery);
		System.out.println(" --- " + result.showJSONResult(iinfo));
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		System.out.println(result.showResult(reader));
		reader.close();
	}
}