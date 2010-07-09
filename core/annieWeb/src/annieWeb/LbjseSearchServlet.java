package annieWeb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.SQLException;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lbjse.search.LbjseFeatureQuery;
import lbjse.search.LbjseObjectQuery;
import lbjse.tools.RankedResultToHtml;
import lbjse.tools.UpdateRankedResultOracle;
import lbjse.tools.ViewCache;

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

import common.profiling.Profile;

import annieWeb.utils.FakeHttpServletRequest;
import annieWeb.utils.FakeHttpServletResponse;
import annieWeb.utils.QueryStringOption;


import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryInfo;
import ose.database.QueryTrans;
import ose.database.QueryTransManager;
import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;

import ose.processor.cascader.OSHits;
import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: BM25SearchServlet
 *
 */

public class LbjseSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	static private final long serialVersionUID = 12312;
	 
	IndexSearcher searcher;		
	Configuration conf;
	private String hostAddress = null;
	
	public LbjseSearchServlet() {
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
		Profile profile = Profile.getProfile("runtime");
		profile.start();
		try {
			QueryStringOption options = new QueryStringOption(request, out);
			
			options.require(new String[]{"query","indexId","domainId","start","nresult"});
	
			String query = options.getString("query");
			
			int indexId = options.getInt("indexId");
			
			int domainId = options.getInt("domainId");
			
			int startIndex = options.getInt("start");
			
			int nRecords = options.getInt("nresult");
			
			Date date = new Date();
			
			String fileName = (date.getYear() + 1900) + "_" + (date.getMonth() + 1) + "_" + (date.getDate() + 1) + "." + date.getHours() + "_" + date.getMinutes() + "_ " + date.getSeconds();
			File temp = File.createTempFile(fileName, ".lbjse_search_result.html");
			String outputFile = temp.getAbsolutePath();
			QueryInfo qinfo = new QueryInfo(-1, query, "from LbjseSearchServlet", domainId);

			LbjseFeatureQuery.PAIRWISE = false;
			LbjseObjectQuery lbjseQuery = new LbjseObjectQuery(qinfo);
			
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			IndexReader indexReader = IndexReader.open(iinfo.getIndexPath());
			lbjseQuery.aggregateResult(indexReader);
			OSHits rawResult = lbjseQuery.getResult();
			rawResult.sortByScore();
			
			OSHits result = new OSHits(indexReader);
			int i = 0;
			while (rawResult.hasNext()){
				rawResult.next();
				if (i >= startIndex && i < startIndex + nRecords){
					result.addNewDocument(rawResult.score(), rawResult.getDocID(), rawResult.docFeatures());
				}
				i +=1 ;
			}
			
			LbjseFeatureQuery.writeResultToFile(indexReader, result, outputFile+ ".txt");
			
			indexReader.close();
			
			// this will convert the ranked result in text file to a html file
			
			RankedResultToHtml converter = new RankedResultToHtml(domainId, indexId, nRecords, hostAddress);
			
			if (options.hasArg("data")){
				LbjseData data = new LbjseDataManager().getDataForId(options.getInt("data"));
				UpdateRankedResultOracle.updateOracles(domainId, query, data.getPath() , outputFile+ ".txt", outputFile+ ".txt.update");
				converter.convertToPageViewerOutput(outputFile + ".txt.update" , outputFile );
			} else {
				converter.convertToPageViewerOutput(outputFile + ".txt" , outputFile );
			}
			
			
			
			BufferedReader reader = new BufferedReader(new FileReader(outputFile));
			while (true){
				String line = reader.readLine();
				if (line == null) break;
				out.println(line);
				if (line.indexOf("<body") != -1){ //hack here: insert pagination code to the result
					String servletUrl = hostAddress + "/annieWeb/LbjseSearchServlet" 
						+ "?indexId=" + indexId 
						+ "&domainId=" + domainId
						+ "&query=" + query  
						+ "&nresult=" + nRecords;
					String prevUrl = servletUrl + "&start=" + (startIndex - nRecords);
					String nextUrl = servletUrl + "&start=" + (startIndex + nRecords);
					out.println("<a href='" + prevUrl + "'>Previous</a>");
					out.println("= " + (startIndex) + " to " + (startIndex + nRecords -1) + " out of " + rawResult.getSize() + " =");
					out.println("<a href='" + nextUrl + "'>Next</a>");
					System.out.println("Servlet url : " + servletUrl);
				}
			}
			
			if (options.hasArg("data")){
				double avgPres = ViewCache.showMAP(outputFile + ".txt.update" );
				out.println("<h3>Average Precision : " + avgPres + "</h3>");
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(out);
		}
		
		out.close();
		profile.end();
		System.out.println("Done (" + profile.getTotalTime() + "ms)");
	}
	
	public static void main(String[] args) throws Exception{
		FakeHttpServletRequest request = new FakeHttpServletRequest();
		request.setParameter("domainId", "2");
		request.setParameter("indexId", "30002");
		request.setParameter("start", "0");
		request.setParameter("nresult", "100");
		request.setParameter("query", "{'dept':'medicine'}");
		
		HttpServletResponse response = new FakeHttpServletResponse("c:\\working\\lbjSearch\\output.html");
		
		LbjseSearchServlet servlet = new LbjseSearchServlet();
		servlet.doGet(request, response);
	}
 
 }