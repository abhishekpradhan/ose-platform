package annieWeb;

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

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.index.IndexReader;
import org.json.JSONException;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
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
 public class AnnotationSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 public static final String OSFIELD_OTHER = "other";

	static private final long serialVersionUID = 12312;
	 
	public AnnotationSearchServlet() {
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
		String objectQuery = request.getParameter("oquery");
		if (objectQuery == null){
			System.out.println("No query given");
			return;
		}
		objectQuery = preProcessAnnotationQuery(objectQuery);
		
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
		
		int queryId = -1;
		if (request.getParameter("queryId") != null){
			queryId = Integer.parseInt(request.getParameter("queryId"));
		}
		
		try {
			ResultPresenter resultPrinter = getResultForQuery(indexId, domainId, objectQuery);
			
			PrintWriter out = response.getWriter();	
			if (startIndex != -1)
				resultPrinter.setStartIndex(startIndex);
			if (nRecords != -1)
				resultPrinter.setHowManyToReturn(nRecords);
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			if (queryId !=  -1)
				resultPrinter.addEvaluation(new AnnotationFeedbackEvaluation(queryId, indexId));
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

	/**
	 * @param objectQuery
	 * @return
	 */
	static private String preProcessAnnotationQuery(String objectQuery) {
		if (objectQuery.indexOf("*") != -1){
			System.out.println("found wildcard string " + objectQuery);
			objectQuery = objectQuery.replace("*", "%");
		}
		return objectQuery;
	}
	
	public ResultPresenter getResultForQuery(int indexId, int domainId, String objectQuery) throws Exception{
		long startTime = System.currentTimeMillis();			
		String sqlQuery = "select * from DocTag" +
				" where IndexId = " + indexId  ;
		OQuery tagQuery = new OQuery(domainId, objectQuery);
		boolean first = true;
		for (String fieldName : tagQuery.getFieldValueMap().keySet()){
			String fieldValue = tagQuery.getFieldValue(fieldName);
			if (fieldValue.equals("")) continue;
			String pred ;
			if (fieldValue.startsWith("_range")){
				RangeConstraint constraint = Utils.parseRangeConstraint(fieldValue);
				pred = " Convert(Value, SIGNED) between " + constraint.getLowerBound() + " and " + constraint.getUpperBound() ;
			}
			else {
				pred = " Value like '" + fieldValue + "'";
			}
			if (first)
				sqlQuery += " and ( (FieldId = " + tagQuery.getFieldIdFromName(fieldName) + " and " + pred  + ") ";
			else
				sqlQuery += " or ( FieldId = " + tagQuery.getFieldIdFromName(fieldName) + " and " + pred  + ") ";
			first = false;
		}
		if (!first)
			sqlQuery += ")";
		System.out.println("Debuggin sqlQuery : " + sqlQuery);
		DocTagManager dtMan = new DocTagManager();
		List<DocTag> docTags = dtMan.query(sqlQuery);
		
		Map<Integer, Map<String, Set<String>>> docWithTags = new HashMap<Integer, Map<String, Set<String>>>();
		for (DocTag dt : docTags){
			if (!docWithTags.containsKey(dt.getDocId())){
				docWithTags.put(dt.getDocId(), new HashMap<String, Set<String>>());
			}
			String fieldName = tagQuery.getFieldNameFromId(dt.getFieldId());
			Map<String, Set<String>> tagMap = docWithTags.get(dt.getDocId()); 
			if (!tagMap.containsKey(fieldName)){
				tagMap.put(fieldName, new HashSet<String>());
			}
			tagMap.get(fieldName).add(dt.getValue());
		}
		
		Set<Integer> docIds = new TreeSet<Integer>();
		for (Integer docId : docWithTags.keySet()){
			if (satisfyTagQuery(docWithTags.get(docId), tagQuery))
				docIds.add(docId);
		}
		long endTime = System.currentTimeMillis();
		
		ResultPresenter scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		for (Integer doc : docIds) {			
			scoredResult.addDocScoreFeatures(doc, 0, null);
		}
		System.out.println("Total documents : " + docIds.size() + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
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
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		AnnotationSearchServlet servlet = new AnnotationSearchServlet();
//		String objectQuery = "{'brand':'canon','model':'','mpix':'_range(0,10)','zoom':'','price':''}";
		String objectQuery = "{'area':'*learning*','dept':'','name':'','univ':''}";
		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  preProcessAnnotationQuery(objectQuery));
		System.out.println(" --- " + result.showJSONResult(iinfo));
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		result.addEvaluation(new AnnotationFeedbackEvaluation(530, indexId));
		System.out.println(result.showResult(reader));
		reader.close();
	}
}