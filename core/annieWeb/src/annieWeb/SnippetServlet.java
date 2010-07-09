package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import os.snippet.generator.SnippetGenerator;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: SnippetServlet
 *
 */
 public class SnippetServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public SnippetServlet() {
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
		
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		String objectQuery = request.getParameter("oquery");
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
		String docIdsStr = request.getParameter("docIds");
		if (docIdsStr == null){
			System.out.println("No Doc IDs");
			return;
		}
		PrintWriter writer = response.getWriter();
		try {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			for (String s : docIdsStr.split(",")){
				docIds.add(Integer.parseInt(s));
			}
			System.out.println("Got " + docIds.size() + " elements , " + docIds);
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			if (iinfo == null){
				System.out.println("No such index id");
				return;
			}
			SnippetGenerator snippetGenerator = new SnippetGenerator(domainId, iinfo.getIndexPath(), iinfo.getCachePath(), objectQuery);
			String [] snippets = snippetGenerator.generateSnippet(docIds.toArray(new Integer[]{})); 
			JSONObject snippetJson = new JSONObject();
			for (int i = 0; i < snippets.length; i++) {
				snippetJson.put("" + docIds.get(i), snippets[i]);
			}
			
			JSONObject resJson = new JSONObject();
			resJson.put("snippets", snippetJson);
			resJson.put("numSnippetReturned", snippets.length);
			resJson.write(writer);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(writer);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(writer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(writer);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
	}
	
}