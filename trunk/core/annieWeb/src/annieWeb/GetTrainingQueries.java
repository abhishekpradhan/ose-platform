package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.DatabaseManager;
import ose.database.TrainingQueryInfo;
import ose.database.TrainingQueryInfoManager;
import ose.query.OQuery;

/**
 * Servlet implementation class for Servlet: GetQueries
 *
 */
 public class GetTrainingQueries extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = -6810954756280589006L;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GetTrainingQueries() {
		super();
	}
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Class path : " + System.getProperty("java.class.path"));
		int domainId = -1;
		if (request.getParameter("domainId") != null){
			domainId = Integer.parseInt(request.getParameter("domainId"));
		}
		else{
			System.out.println("domainId required");
			return;
		}
		
		int fieldId = -1;
		if (request.getParameter("fieldId") != null){
			fieldId = Integer.parseInt(request.getParameter("fieldId"));
		}
		else{
			System.out.println("fieldId required");
			return;
		}
		
		TrainingQueryInfoManager man = new TrainingQueryInfoManager(DatabaseManager.getDatabaseManager());
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		JSONArray result = new JSONArray();
		try {
			for (TrainingQueryInfo qinfo : man.query("select * from TrainingQueryInfo where fieldId = " + fieldId + " order by AttributeId")) {
				JSONObject item = new JSONObject();
				item.put("QueryId", qinfo.getTrainingQueryId());
				OQuery query = new OQuery(domainId);
				query.setFieldValue(fieldId, qinfo.getValue());
				item.put("QueryString", query.getJsonString());
				item.put("Description", qinfo.getValue());
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
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}   	  	    
}