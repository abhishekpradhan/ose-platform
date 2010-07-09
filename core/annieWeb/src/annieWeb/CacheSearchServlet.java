package annieWeb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lbjse.rank.ResultItem;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.index.IndexReader;
import org.json.JSONException;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.LBJSECache;
import ose.database.LBJSECacheManager;
import ose.learning.InRangeAnyTagRule;
import ose.parser.Utils;
import ose.processor.cascader.RangeConstraint;
import ose.query.OQuery;
import ose.retrieval.AnnotationFeedbackEvaluation;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class CacheSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 public static final String OSFIELD_OTHER = "other";
	 private static int lastIndexId = -1;
	 private static int lastQueryId = -1;
	 private static ResultPresenter scoredResult = null;
	
	static private final long serialVersionUID = 12312;
	 
	public CacheSearchServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		System.out.println("hello");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		
		if (request.getParameter("indexId") == null){
			System.out.println("No index given");
			return;
		}
		int indexId = Integer.parseInt(request.getParameter("indexId"));
		
		if (request.getParameter("queryId") == null){
			System.out.println("No query given");
			return;
		}
		int queryId = Integer.parseInt(request.getParameter("queryId"));
		
		int startIndex = -1;
		if (request.getParameter("start") != null){
			startIndex = Integer.parseInt(request.getParameter("start"));
		}
		int nRecords = -1;
		if (request.getParameter("nRecords") != null){
			nRecords = Integer.parseInt(request.getParameter("nRecords"));
		}
		
		try {
			ResultPresenter resultPrinter = getResultForQuery(queryId, indexId);
			
			PrintWriter out = response.getWriter();	
			if (startIndex != -1)
				resultPrinter.setStartIndex(startIndex);
			if (nRecords != -1)
				resultPrinter.setHowManyToReturn(nRecords);
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			if (queryId !=  -1)
				resultPrinter.addEvaluation(new AnnotationFeedbackEvaluation(queryId, indexId));
			resultPrinter.setReOrder(false);
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

	public ResultPresenter getResultForQuery(int queryId, int indexId) throws Exception{
		if (queryId == lastQueryId && indexId == lastIndexId){
			return scoredResult ;
		}
		lastQueryId = queryId;
		lastIndexId = indexId;
		scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		LBJSECacheManager man = new LBJSECacheManager();
		LBJSECache cache = man.getCacheForQuery(queryId, indexId);
		System.out.println("Got cache info: " + cache);
		if (cache == null)
			return scoredResult;
		BufferedReader reader = new BufferedReader(new FileReader(cache.getCacheFile()));
		int count = 0;
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			try {
				item = new ResultItem(line);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			scoredResult.addDocScoreFeatures(item.docId, item.score, null);
			count += 1;
		}
		
		System.out.println("Total documents : " + count + "");
		return scoredResult;
	}

	private boolean satisfyTagQuery(Map<String,Set<String>> tagMap, OQuery tagQuery){
		for (String fieldName : tagQuery.getFieldValueMap().keySet()){
			String fieldValue = tagQuery.getFieldValue(fieldName);
			Set<String> tags = tagMap.get(fieldName);
			
			if (fieldValue.equals("") || fieldValue.equals("%")) 
				continue;
			if (tags == null)
				return false;
			if (fieldValue.startsWith("_range")){
				InRangeAnyTagRule rule = new InRangeAnyTagRule();
				StringBuffer mergedTags = new StringBuffer();
				for (String tag : tags) 
					mergedTags.append(" " + tag);
				if (rule.getClassification(mergedTags.toString(), fieldValue) == 0)
					return false;
			}
			else {
				if (fieldValue.indexOf("%") != -1){
					fieldValue = fieldValue.replace("%", ".*");
				}
				for (String tag : tags){
					if (Pattern.matches(fieldValue, tag))
						return true;
				}
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception{
		int indexId = 201;
		int domainId = 2;
//		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
//		CacheSearchServlet servlet = new CacheSearchServlet();
////		String objectQuery = "{'brand':'canon','model':'','mpix':'_range(0,10)','zoom':'','price':''}";
//		String objectQuery = "{'area':'*learning*','dept':'','name':'','univ':''}";
//		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  preProcessAnnotationQuery(objectQuery));
//		System.out.println(" --- " + result.showJSONResult(iinfo));
//		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
//		result.addEvaluation(new AnnotationFeedbackEvaluation(530, indexId));
//		System.out.println(result.showResult(reader));
//		reader.close();
	}
}