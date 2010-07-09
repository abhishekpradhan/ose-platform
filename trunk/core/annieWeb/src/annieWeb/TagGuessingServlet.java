package annieWeb;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import lbjse.datacleaning.TagGuesser;
import lbjse.objectsearch.ObjectInfo;

import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: TagGuessingServlet
 *
 */
 public class TagGuessingServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public TagGuessingServlet() {
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
		
		System.out.println("TagGuessing : " + JsonIO.toJSONString( request.getParameterMap()) );
		
		int domainId = Integer.parseInt( request.getParameter("domainId") );
		int indexId = Integer.parseInt( request.getParameter("indexId") );
		int docId = Integer.parseInt( request.getParameter("docId") );
		try {
			TagGuesser guesser = new TagGuesser(indexId);
			Map<Integer, List<String>> guessedTags = guesser.guessTag(domainId, docId);
			JSONObject result = new JSONObject();
			result.put("gtags", guessedTags);
			ObjectInfo oinfo = new ObjectInfo(domainId);
			result.put("domInfo", getOrderedMapJSON(oinfo.getIdNameMap()));
			
			System.out.println(result.get("domInfo"));
			out.println(result.toString(1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
	}
	
	JSONObject getOrderedMapJSON(Map<? extends Object, ? extends Object> m) throws JSONException{
		JSONObject obj = new JSONObject();
		TreeMap<Object, Object> tm = new TreeMap<Object, Object>(m);
		for (Object key : tm.keySet()) {
			obj.put(key.toString(), m.get(key));
		}
		return obj;
	}
}