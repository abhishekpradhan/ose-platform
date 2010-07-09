package annieWeb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbjse.learn.LearnFromConcreteFeatures;

import org.apache.hadoop.conf.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;
import ose.database.lbjse.LbjseFeatureTemplate;
import ose.database.lbjse.LbjseFeatureTemplateManager;
import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseSearchFeature;
import ose.database.lbjse.LbjseSearchFeatureManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;

/**
 * Servlet implementation class for Servlet: LearnerServices
 *
 */
 public class LearnerServices extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   Configuration conf;
   private String rankedFeatureJSONFile = null;
	
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public LearnerServices() {
		super();
		conf = new Configuration();
		conf.addResource("dbconfig.xml");
		rankedFeatureJSONFile = conf.get("servlet.features.json");
		System.out.println("Got feature file : " + rankedFeatureJSONFile);
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		try {
			if ("listData".equals(request.getParameter("opt"))){
				listData(response);
			}
			else if ("listTrainingSession".equals(request.getParameter("opt"))){
				listTrainingSession(request, response);
			}
			else if ("getFeatureTemplate".equals(request.getParameter("opt"))){
				getFeatureTemplate(request, response);
			}
			else if ("addFeatureTemplate".equals(request.getParameter("opt"))){
				addFeatureTemplate(request, response);
			}
			else if ("delFeatureTemplate".equals(request.getParameter("opt"))){
				delFeatureTemplate(request, response);
			}
			else if ("getQueryValue".equals(request.getParameter("opt"))){
				getQueryValue(request, response);
			}
			else if ("addQueryValue".equals(request.getParameter("opt"))){
				addQueryValue(request, response);
			}
			else if ("delQueryValue".equals(request.getParameter("opt"))){
				delQueryValue(request, response);
			}
			else if ("getSearchFeature".equals(request.getParameter("opt"))){
				getSearchFeature(request, response);
			}
			else if ("addSearchFeature".equals(request.getParameter("opt"))){
				addSearchFeature(request, response);
			}
			else if ("delSearchFeature".equals(request.getParameter("opt"))){
				delFeatureTemplate(request, response);
			}
			else if ("getRankedFeatures".equals(request.getParameter("opt"))){
				getRankedFeatures(request, response);
			}
			else {
				System.out.println("Unknown option : " + request.getParameter("opt"));
			}
		} catch (Exception ex) {
			printError(response, ex);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void listTrainingSession(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {		
		LbjseTrainingSessionManager man = new LbjseTrainingSessionManager();
		PrintWriter out = response.getWriter();
		JSONArray result = new JSONArray();
		for (LbjseTrainingSession session : man.query("select * from lbjseTrainingSession")){
		
			JSONObject item = new JSONObject();
			try {
				item.put("SessionId", session.getId());
				item.put("DomainId", session.getDomainId());
				item.put("FieldId", session.getFieldId());
				item.put("Description", session.getDescription());
				item.put("FeatureGeneratorClass", session.getFeatureGeneratorClass());
				item.put("ClassifierClass", session.getClassifierClass());
				item.put("CurrentPerformance", strToJson(session.getCurrentPerformance()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result.put(item);
		}
		out.write(result.toString());
		out.close();
	}
	
	static public JSONObject strToJson(String s){
		try {
			return new JSONObject(s);
		} catch (JSONException e) {
		} catch (NullPointerException e){
		}
		return null;
	}
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void getFeatureTemplate(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("sessionId"));
		LbjseFeatureTemplateManager man = new LbjseFeatureTemplateManager();
		PrintWriter out = response.getWriter();
		JSONArray items = new JSONArray();
		try {
			for (LbjseFeatureTemplate featureTemplate:  man.getFeatureTemplateForSession(sessionId) ){
				JSONObject item = new JSONObject();
				item.put("Id", featureTemplate.getId());
				item.put("SessionId", featureTemplate.getSessionId());
				item.put("Template", featureTemplate.getTemplate());
				item.put("Weight", featureTemplate.getWeight());
				items.put(item);
			}
			JSONObject result =  new JSONObject();
			result.put("items", items);
			out.write(result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void addFeatureTemplate(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("SessionId"));
//		double weight = Double.parseDouble(request.getParameter("Weight"));
		String template = request.getParameter("Template");
		LbjseFeatureTemplateManager man = new LbjseFeatureTemplateManager();
		LbjseFeatureTemplate object = new LbjseFeatureTemplate(sessionId, template, 1.0d);
		man.insert(object);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("TemplateId", object.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void delFeatureTemplate(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int templateId = Integer.parseInt(request.getParameter("TemplateId"));
		LbjseFeatureTemplateManager man = new LbjseFeatureTemplateManager();
		man.update("delete lbjseFeatureTemplate where TemplateId = " + templateId);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("status", "ok");
			item.put("message", "feature template deleted");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void getQueryValue(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("sessionId"));
		LbjseQueryValueManager man = new LbjseQueryValueManager();
		PrintWriter out = response.getWriter();
		JSONArray items = new JSONArray();
		try {
			for (LbjseQueryValue value:  man.getQueryValueForSession(sessionId) ){
				JSONObject item = new JSONObject();
				
				item.put("Id",value.getValueId());
				item.put("SessionId", value.getSessionId());
				item.put("Value", value.getValue());
				items.put(item);
			
			}
			JSONObject result =  new JSONObject();
			result.put("items", items);
			out.write(result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void addQueryValue(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("SessionId"));
		String value = request.getParameter("Value");
		LbjseQueryValueManager man = new LbjseQueryValueManager();
		LbjseQueryValue object = new LbjseQueryValue(-1,sessionId, value);
		man.insert(object);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("ValueId", object.getValueId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void delQueryValue(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int valueId = Integer.parseInt(request.getParameter("ValueId"));
		LbjseQueryValueManager man = new LbjseQueryValueManager();
		man.update("delete lbjseQueryValue where Id = " + valueId);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("status", "ok");
			item.put("message", "feature template deleted");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}

	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void getSearchFeature(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("sessionId"));
		LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
		PrintWriter out = response.getWriter();
		JSONArray items = new JSONArray();
		try {
			for (LbjseSearchFeature feature:  man.getFeaturesForSession(sessionId) ){
				JSONObject item = new JSONObject();
					item.put("FeatureId", feature.getFeatureId());
					item.put("SessionId", feature.getSessionId());
					item.put("Value", feature.getValue());
					items.put(item);
			}
			JSONObject result =  new JSONObject();
			result.put("items", items);
			out.write(result.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void addSearchFeature(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("SessionId"));
		String value = request.getParameter("Value");
		LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
		LbjseSearchFeature object = new LbjseSearchFeature(-1,sessionId, value);
		man.insert(object);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("FeatureId", object.getFeatureId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws SQLException
	 * @throws IOException
	 */
	private void delSearchFeature(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int featureId = Integer.parseInt(request.getParameter("FeatureId"));
		LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
		man.update("delete lbjseSearchFeature where FeatureId = " + featureId);
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("status", "ok");
			item.put("message", "feature template deleted");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}
	
	/**
	 * @param response
	 * @param ex
	 * @throws IOException
	 */
	private void printError(HttpServletResponse response, Exception ex)
			throws IOException {
		PrintWriter out = response.getWriter();
		JSONObject item = new JSONObject();
		try {
			item.put("status", "error");
			item.put("message", ex.getMessage());
			ex.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(item.toString());
		out.close();
	}

	/**
	 * @param response
	 * @throws IOException
	 */
	private void listData(HttpServletResponse response) throws IOException {
		LbjseDataManager dataMan = new LbjseDataManager();
		PrintWriter out = response.getWriter();
		JSONArray result = new JSONArray();
		try {
			for (LbjseData data : dataMan.query("select * from lbjseData")) {
				JSONObject item = new JSONObject();
				item.put("Id", data.getId());
				item.put("IndexId", data.getIndexId());
				item.put("Path", data.getPath());
				item.put("Date", data.getDateCreated().toString());
				item.put("Description", data.getDescription());
				result.put(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.write(result.toString());
		out.close();
	}
		
	static private List<JSONObject> rankedFeatures = null;
	private void getRankedFeatures(HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {
		int sessionId = Integer.parseInt(request.getParameter("sessionId"));
		int dataId = Integer.parseInt(request.getParameter("dataId"));
		try {
			if (request.getParameter("nocache") != null || rankedFeatures == null){
				BufferedReader reader = new BufferedReader(new FileReader(rankedFeatureJSONFile + "_" + sessionId + "_" + dataId + ".json"));
				StringBuffer buffer = new StringBuffer();
				while (true){
					String line = reader.readLine();
					if (line == null) 
						break;
					buffer.append(line);
				}
				
				JSONObject obj = new JSONObject(buffer.toString());
				JSONArray arr = obj.getJSONArray("items");
				rankedFeatures = new ArrayList<JSONObject>();
				for (int i = 0; i < arr.length(); i++) {
					rankedFeatures.add(arr.getJSONObject(i));
				}
				System.out.println("read " + rankedFeatures.size() + " items");
			}
			
			
			int start = 0;
			int nresult = 100;
			if (request.getParameter("start") != null){
				start = Integer.parseInt(request.getParameter("start"));
			}
			if (request.getParameter("nresult") != null){
				nresult = Integer.parseInt(request.getParameter("nresult"));
			}
			if (start + nresult >= rankedFeatures.size())
				start = rankedFeatures.size() - nresult - 1;
			if (start < 0) { //adjust start
				nresult += start;
				start = 0;
			}
			JSONArray returnedItems = new JSONArray();
			for (int i = 0; i < nresult; i++) {
				returnedItems.put(rankedFeatures.get(start+i));
			}
			JSONObject obj = new JSONObject();
			obj.put("items", returnedItems);
			PrintWriter out = response.getWriter();
			out.write(obj.toString(1));
			out.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}   	  	    
}