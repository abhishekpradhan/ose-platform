package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;

/**
 * Servlet implementation class for Servlet: FeatureInfoServlet
 *
 */
 public class FeatureInfoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public FeatureInfoServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");	
		PrintWriter out = response.getWriter();	
		
		int domainId = 0;
		if (request.getParameter("domainId") != null){
			domainId = Integer.parseInt(request.getParameter("domainId"));
		}
		else return;
		
		String action = request.getParameter("action");
		
		if (action == null){
			return ;
		}
		else if (action.equals("GetFeaturesFromDomain")){
			getFeaturesFromDomain(domainId, out);
		}
		else if (action.equals("add")){
			int fieldId;
			if (request.getParameter("fieldId") != null){
				fieldId = Integer.parseInt(request.getParameter("fieldId"));
			}
			else {
				out.print("FieldId is required");
				return;
			}
			
			String template;
			if (request.getParameter("template") != null){
				template = request.getParameter("template");
				template = new String(template.getBytes("8859_1"), "UTF8");
			}
			else {
				out.print("(feature) template is required");
				return;
			}
			
			double weight;
			if (request.getParameter("weight") != null){
				weight = Double.parseDouble( request.getParameter("weight") );
			}
			else {
				out.print("weight is required");
				return;
			}

			addFeatureForDomain(domainId,fieldId, template, weight, out);
		}
		else if (action.equals("removeAll")){
			removeAllFeatureFromDomain(domainId, out);
		}
		
		out.close();

	}

	private void removeAllFeatureFromDomain(int domainId, PrintWriter out) {
		try {
			FeatureInfoManager man = new FeatureInfoManager();
			int res = man.update("update FeatureInfo " +
					  "set IsDeleted = 1 " + 
			          "where FieldId in (" + 
			                "select FieldId from FieldInfo " +
			                "where DomainId = " + domainId + ")");
			out.print("ok : " + res + " row(s) deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.print("failed");
		}
	}
	
	private void getFeaturesFromDomain(int domainId, PrintWriter out) {
		try {
			JSONArray jsonFieldArray = new JSONArray();
			FieldInfoManager fieldMan = new FieldInfoManager();
			FeatureInfoManager featurefoMan = new FeatureInfoManager();
			List<FieldInfo> fields = fieldMan.query(
					"select * from FieldInfo " + 
					"where DomainId =  " + domainId);
			for (FieldInfo fieldInfo : fields) {
				
				JSONArray jsonFeatureArray = new JSONArray();
				System.out.println("-- " + fieldInfo);
				List<FeatureInfo> features = featurefoMan.query(
						"select * from FeatureInfo " + 
						"where IsDeleted = 0 and FieldId =  " + fieldInfo.getFieldId());
				for (FeatureInfo featureInfo : features) {
					System.out.println("   ++ " + featureInfo);
					JSONObject jsonFeature = new JSONObject();
					jsonFeature.put("FeatureId", featureInfo.getFieldId());
					jsonFeature.put("Template", featureInfo.getTemplate());
					jsonFeature.put("Weight", featureInfo.getWeight());
					jsonFeatureArray.put(jsonFeature);
				}
				JSONObject jsonField = new JSONObject();
				jsonField.put("field", fieldInfo.getName());
				jsonField.put("features", jsonFeatureArray);
				jsonFieldArray.put(jsonField);
			}
			JSONObject json = new JSONObject();
			json.put("result", jsonFieldArray);
			out.print(json.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}   	  	    
	
	private void addFeatureForDomain(int domainId, int fieldId, String template, double weight, PrintWriter out) {
		FeatureInfo feature = new FeatureInfo(fieldId, template, weight);
		FeatureInfoManager man =new FeatureInfoManager();
		try{
			man.insert(feature);
			out.print(feature.getFeatureId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}