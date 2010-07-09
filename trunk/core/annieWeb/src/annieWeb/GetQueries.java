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
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;

/**
 * Servlet implementation class for Servlet: GetQueries
 *
 */
 public class GetQueries extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GetQueries() {
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
		
		QueryInfoManager man = new QueryInfoManager(DatabaseManager.getDatabaseManager());
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		JSONArray result = new JSONArray();
		try {
			for (QueryInfo qinfo : man.query("select * from QueryInfo where DomainId = " + domainId + " order by Description")) {
				JSONObject item = new JSONObject();
				item.put("QueryId", qinfo.getQueryId());
				item.put("QueryString", qinfo.getQueryString());
				item.put("Description", qinfo.getDescription());
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