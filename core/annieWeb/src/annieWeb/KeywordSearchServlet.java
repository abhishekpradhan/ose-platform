package annieWeb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbjse.search.LbjseFeatureQuery;
import lbjse.tools.RankedResultToHtml;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocCollector;
import org.json.JSONException;
import org.ninit.models.bm25.BM25BooleanQuery;
import org.ninit.models.bm25.BM25Parameters;
import org.ninit.models.bool.NumberRange;

import annieWeb.utils.QueryStringOption;


import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryTrans;
import ose.database.QueryTransManager;

import ose.processor.cascader.OSHits;
import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: BM25SearchServlet
 *
 */

public class KeywordSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	static private final long serialVersionUID = 12312;
	 
	IndexSearcher searcher;		
	Configuration conf;
	private String hostAddress = null;
	
	public KeywordSearchServlet() {
		super();
		conf = new Configuration();
		conf.addResource("dbconfig.xml");
		hostAddress = conf.get("servlet.hostAddress");
	}
	
	/**
	 * Constructor of the object.
	 */
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
			
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		PrintWriter out = response.getWriter();
		
		try {
			QueryStringOption options = new QueryStringOption(request, out);
			
			options.require(new String[]{"query","indexId","domainId","start","nresult"});
	
			String query = options.getString("query");
			
			int indexId = options.getInt("indexId");
			
			int domainId = options.getInt("domainId");
			
			int startIndex = options.getInt("start");
			
			int nRecords = options.getInt("nresult");
	
		
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			// search the index using standard Lucene API
			Query q = new QueryParser("Token", new StandardAnalyzer()).parse(query);
			IndexSearcher searcher = new IndexSearcher(iinfo.getIndexPath());
			TopDocCollector results = new TopDocCollector(startIndex + nRecords);
			searcher.search(q, results);
			OSHits hits = new OSHits(searcher.getIndexReader());
			int i = 0;
			
			for (ScoreDoc doc : results.topDocs().scoreDocs){
				if (i >= startIndex && i < startIndex + nRecords)
					hits.addNewDocument(doc.score, doc.doc, null);
				i += 1;
			}
			Date date = new Date();
			
			String fileName = (date.getYear() + 1900) + "_" + (date.getMonth() + 1) + "_" + (date.getDate() + 1) + "." + date.getHours() + "_" + date.getMinutes() + "_ " + date.getSeconds();
			File temp = File.createTempFile(fileName, ".keyword_search_result.html");
			String outputFile = temp.getAbsolutePath();
			// this will write to a text file. 
			LbjseFeatureQuery.writeResultToFile(searcher.getIndexReader(), hits, outputFile + ".txt");
			searcher.close();
			// this will convert the ranked result in text file to a html file
			
			RankedResultToHtml converter = new RankedResultToHtml(domainId, indexId, nRecords, hostAddress);
			converter.convertToPageViewerOutput(outputFile + ".txt" , outputFile );
			
			BufferedReader reader = new BufferedReader(new FileReader(outputFile));
			while (true){
				String line = reader.readLine();
				if (line == null) break;
				out.println(line);
				if (line.indexOf("<body") != -1){ //hack here: insert pagination code to the result
					String servletUrl = hostAddress + "/annieWeb/KeywordSearchServlet" 
						+ "?indexId=" + indexId 
						+ "&domainId=" + domainId
						+ "&query=" + query  
						+ "&nresult=" + nRecords;
					String prevUrl = servletUrl + "&start=" + (startIndex - nRecords);
					String nextUrl = servletUrl + "&start=" + (startIndex + nRecords);
					out.println("<a href='" + prevUrl + "'>Previous</a>");
					out.println("= " + (startIndex) + " to " + (startIndex + nRecords -1) + " out of " + results.getTotalHits() + " =");
					out.println("<a href='" + nextUrl + "'>Next</a>");
					System.out.println("Servlet url : " + servletUrl);
				}
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(out);
		}
		
		out.close();
		
	}
	
	public static void main(String[] args) throws Exception{
	}
 
 }