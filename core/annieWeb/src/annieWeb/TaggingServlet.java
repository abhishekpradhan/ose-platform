package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.DatabaseManager;
import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: TaggingServlet
 *
 */
 public class TaggingServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static private final long serialVersionUID = 62345L;
	  
	 public TaggingServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");		
		PrintWriter out = response.getWriter();
		
		System.out.println("Params : " + JsonIO.toJSONString( request.getParameterMap()) );
		
		String action = request.getParameter("action") ;
		if (action == null){
			out.println("missing action");
			return ; 
		}
		else if (action.equals("add")){
			addTag(request, out);
		}
		else if (action.equals("delete")){
			deleteTag(request, out);
		}
		else if (action.equals("query")){
			queryTag(request, out);
		}
		else if (action.equals("summary")){
			summaryTag(request, out);
		}
		out.close();
	}

	private void addTag(HttpServletRequest request, PrintWriter out) {
		int docId = 0;
		int indexId = 0;
		int fieldId = 0;
		
		if (request.getParameter("indexId") == null){
			out.println("missing index Id");
			return ; 
		}
		if (request.getParameter("docId") == null){
			out.println("missing doc Id");
			return ; 
		}
		if (request.getParameter("fieldId") == null){
			out.println("missing fieldId");
			return ; 
		}
		if (request.getParameter("value") == null){
			out.println("missing tag value");
			return ; 
		}
		
		try {
			String tagValue = request.getParameter("value");
			tagValue = new String(tagValue.getBytes("8859_1"), "UTF8");
			docId = Integer.parseInt( request.getParameter("docId") );
			indexId = Integer.parseInt( request.getParameter("indexId") );
			fieldId = Integer.parseInt( request.getParameter("fieldId") );
			
			DocTagManager tagMan = new DocTagManager();
			DocTag tag = new DocTag(indexId, docId, fieldId, tagValue);
			tagMan.insert(tag);
			out.print("ok " + tag.getTagId());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (SQLException e) {
			e.printStackTrace();
			out.println("failed");
		}  catch (IOException ex) {
			ex.printStackTrace();
			out.println("failed");
		}
	}
	
	private void deleteTag(HttpServletRequest request, PrintWriter out) {
		int tagId = 0;
		
		if (request.getParameter("tagId") == null){
			out.println("missing tag Id");
			return ; 
		}
		tagId = Integer.parseInt( request.getParameter("tagId") );
		
		try {
			DocTagManager tagMan = new DocTagManager();
			tagMan.update("delete from DocTag where TagId = " + tagId);
			out.print("ok");
		} catch (NumberFormatException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (SQLException e) {
			e.printStackTrace();
			out.println("failed");
		}
	}
	
	private void queryTag(HttpServletRequest request, PrintWriter out) {
		ArrayList<Integer> docIds = new ArrayList<Integer>();
		int indexId = 0;
		boolean singleDoc = false;
		if (request.getParameter("indexId") == null){
			out.println("missing index Id");
			return ; 
		}
		
		if (request.getParameter("domainId") == null){
			out.println("missing domainId");
			return ; 
		}
				
		try {
			if (request.getParameter("docId") != null){
				docIds.add(Integer.parseInt( request.getParameter("docId") ) ) ;
				singleDoc = true;
			}
			else if (request.getParameter("docIds") != null){
				String [] strs = request.getParameter("docIds").split(",");
				for (String str : strs){
					docIds.add(Integer.parseInt( str ) );
				}
			}
			
			if (docIds.size() <= 0 ) {
				out.println("missing doc Id");
				return ; 
			}
			
			indexId = Integer.parseInt( request.getParameter("indexId") );
			int domainId = Integer.parseInt( request.getParameter("domainId") );
			if (singleDoc){
				JSONArray jsonResult = getTagsJSON(docIds.get(0), indexId, domainId);
				out.print(jsonResult.toString());
			}
			else {
				JSONObject jsonResult = new JSONObject();  
				for (Integer docId : docIds){
					JSONArray jsonArray = getTagsJSON(docId, indexId, domainId);
					jsonResult.put(docId.toString(), jsonArray);				
				}
				out.print(jsonResult.toString());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (SQLException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (JSONException e) {
			e.printStackTrace();
			out.println("failed");
		} 
	}

	/**
	 * @param docId
	 * @param indexId
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray getTagsJSON(int docId, int indexId, int domainId) throws SQLException,
		JSONException {
		JSONArray jsonArray = new JSONArray();
		DocTagManager tagMan = new DocTagManager();
		List<DocTag> tags = tagMan.query("select * from DocTag, FieldInfo where DocId = "
				+ docId + " and IndexId = " + indexId + 
				" AND DomainId = " + domainId + 
				" AND DocTag.FieldId = FieldInfo.FieldId");
		for (DocTag docTag : tags) {
			JSONObject json = new JSONObject();
			json.put("TagId", docTag.getTagId());
			json.put("DocId", docTag.getDocId());
			json.put("IndexId", docTag.getIndexId());
			json.put("FieldId", docTag.getFieldId());
			json.put("Value", docTag.getValue());
			jsonArray.put(json);
		}
		return jsonArray;
	}
	
	private void summaryTag(HttpServletRequest request, PrintWriter out) {
		int domainId = 0;
		int indexId = 0;
		
		if (request.getParameter("indexId") == null){
			out.println("missing index Id");
			return ; 
		}
		if (request.getParameter("domainId") == null){
			out.println("missing doc Id");
			return ; 
		}
				
		try {
			domainId = Integer.parseInt( request.getParameter("domainId") );
			indexId = Integer.parseInt( request.getParameter("indexId") );	
			JSONArray jsonArray = new JSONArray();
			Connection conn = DatabaseManager.getDatabaseManager().getConnection();
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// Query the database, storing the result
			// in an object of type ResultSet
			ResultSet rs = stmt.executeQuery("" +
				"select Name,N.FieldId, Count from " +  
					"(select FieldId, count( DocId) as Count from DocTag " +
					"where IndexId = " + indexId + " " +
					"group by FieldId" +
					") as C, " +
					"(select Name, FieldId " +
					"from FieldInfo " +
					"where DomainId = " + domainId +
					") as N " +
				"where C.FieldID = N.FieldID");
			
			
			int totalTag = 0; 
			while (rs.next()) {
				JSONObject record = new JSONObject();
				record.put("FieldName", rs.getString("Name")); 
				record.put("FieldId", rs.getInt("FieldId"));
				int count = rs.getInt("Count");
				record.put("Count", count);
				totalTag += count;
				jsonArray.put(record);
			}
//			conn.close();
			JSONObject result = new JSONObject();
			result.put("fields", jsonArray);
			result.put("totalTags", totalTag);
			out.print(result.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (SQLException e) {
			e.printStackTrace();
			out.println("failed");
		} catch (JSONException e) {
			e.printStackTrace();
			out.println("failed");
		} 
	}
}