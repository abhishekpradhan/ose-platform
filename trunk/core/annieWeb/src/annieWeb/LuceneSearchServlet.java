package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Similarity;
import org.json.JSONException;
import org.ninit.models.bm25.BM25BooleanQuery;
import org.ninit.models.bm25.BM25Parameters;
import org.ninit.models.bool.NumberRange;


import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryTrans;
import ose.database.QueryTransManager;

import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: BM25SearchServlet
 *
 */

public class LuceneSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	static private final long serialVersionUID = 12312;
	 
	 IndexSearcher searcher;		
	 	
	public LuceneSearchServlet() {
		super();
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
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		String queryString = request.getParameter("queryId");
		
		if (queryString == null){ 
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
		int startIndex = -1;
		if (request.getParameter("start") != null){
			startIndex = Integer.parseInt(request.getParameter("start"));
		}
		int nRecords = -1;
		if (request.getParameter("nRecords") != null){
			nRecords = Integer.parseInt(request.getParameter("nRecords"));
		}
		
		int queryId = -1, queryTransId = -1;
		if (request.getParameter("queryId") != null){
			queryId = Integer.parseInt(request.getParameter("queryId"));
			try {
				queryTransId = new QueryTransManager().getQueryTransIdForQueryInfor(queryId);
				QueryTrans qTrans = new QueryTransManager().queryByKey(queryTransId);
				queryString = qTrans.getQueryString();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println(queryTransId);
		if (queryTransId > 0) {
			try {
				ResultPresenter resultPrinter = getResultForQuery(indexId, domainId, queryString);				
				PrintWriter out = response.getWriter();	
				if (startIndex != -1)
					resultPrinter.setStartIndex(startIndex);
				if (nRecords != -1)
					resultPrinter.setHowManyToReturn(nRecords);
				IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
				if (queryId !=  -1)
					resultPrinter.addEvaluation(new ObjectRankingEvaluation(queryId, indexId));
				resultPrinter.showJSONResult(iinfo, out);
				out.close();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("Done !");
			}
		}	
	}
	
	
	public ResultPresenter getResultForQuery(int indexId, int domainId, String queryString) throws Exception{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);	
		searcher = new IndexSearcher(iinfo.getIndexPath());
		IndexReader reader = searcher.getIndexReader();			
		int docs=1;
		float len = 0.f;
		try {											
			byte[] norms = reader.norms(NumberRange.field);
			docs = norms.length;			
			for(int i = 0 ; i < docs; i++){
				float norm = Similarity.decodeNorm(norms[i]);
				len += 1 / (norm * norm);
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float av_length = len/docs;
		System.out.println(av_length);
		
		BM25Parameters.setAverageLength("Token", av_length);		
		
		
		long startTime = System.currentTimeMillis();
		
		BM25BooleanQuery query = new BM25BooleanQuery(queryString, "Token", new StandardAnalyzer());
		System.out.println(query.toString("contents"));			
			
		Hits result = searcher.search(query);				
				
		long endTime = System.currentTimeMillis();
		
		ResultPresenter scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		int i = 0;
		int totalResult = result.length();
		while(i<totalResult){				
			Document doc = result.doc(i);
			scoredResult.addDocScoreFeatures(doc.getDocid(), result.score(i), null);
			i += 1;
		}
		
		System.out.println("Total time : " + (endTime - startTime) );
		return scoredResult;
	}

		

	public static void main(String[] args) throws Exception{
		int indexId = 18;
		int domainId = 5;
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		
		LuceneSearchServlet servlet = new LuceneSearchServlet();
		String objectQuery = "hoàng mai AND nhà phố AND 700000000...2500000000 vnd AND 1..5 phòng ngủ AND 1..4 phòng tắm AND 50..200 m2";
		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  objectQuery);
		System.out.println(" --- " + result.showJSONResult(iinfo));
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		System.out.println(result.showResult(reader));
		reader.close();
	}
 
 }