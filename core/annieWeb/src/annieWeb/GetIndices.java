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
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;

/**
 * Servlet implementation class for Servlet: GetIndices
 *
 */
 public class GetIndices extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GetIndices() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Class path : " + System.getProperty("java.class.path"));
		IndexInfoManager man = new IndexInfoManager(DatabaseManager.getDatabaseManager());
		PrintWriter out = response.getWriter();
		try {
			JSONArray result = new JSONArray();
			for (IndexInfo iinfo : man.query("select * from IndexInfo")) {
				JSONObject obj = new JSONObject();
				obj.put("IndexId", iinfo.getIndexId());
				obj.put("Name", iinfo.getName());
				obj.put("Description", iinfo.getDescription());
				result.put(obj);
			}
			out.println(result.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e){
			e.printStackTrace();
		}
		out.close();
	}  	  	  	    
}