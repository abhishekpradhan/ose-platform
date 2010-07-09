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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import annieWeb.utils.ServeletUtils;

import ose.database.DatabaseManager;
import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: TaggingServlet
 * 
 */
public class DocInfoServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static private final long serialVersionUID = 62234345L;

	public DocInfoServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();

		System.out.println("Params : "
				+ JsonIO.toJSONString(request.getParameterMap()));

		int docId = Integer.parseInt(request.getParameter("docId"));
		int indexId = Integer.parseInt(request.getParameter("indexId"));
		int domainId = Integer.parseInt(request.getParameter("domainId"));
		try {
			JSONObject info = makeJsonObjectFromDocId(docId, indexId);
			info.put("TagList", TaggingServlet.getTagsJSON(docId, indexId, domainId));
			out.println(info.toString(2));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.close();
	}

	private JSONObject makeJsonObjectFromDocId(int docId, int indexId) throws JSONException,
			CorruptIndexException, IOException, SQLException {
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader reader = IndexReader.open(iinfo.getCachePath());
		JSONObject json = new JSONObject();
		json.put("No", 1);
		json.put("DocId", docId);
		json.put("Score", 0.0);
		json.put("Features", "");
		TrecDocument trecDoc = new TrecDocument(reader, docId);
		trecDoc.parseHtml(false);
		String title = trecDoc.getTitle();
		if (title == null || title.length() == 0)
			title = "untitled";
		json.put("Title", title);
		json.put("PlainBody", trecDoc.getPlainBody());
		json.put("CacheUrl", ServeletUtils.getCacheURL(indexId, docId));
		json.put("Url", trecDoc.getUrl());
		reader.close();
		return json;
	}

	

}