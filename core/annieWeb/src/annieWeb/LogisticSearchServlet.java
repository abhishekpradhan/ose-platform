package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.json.JSONException;

import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.ModelInfo;
import ose.database.ModelInfoManager;
import ose.parser.OSQueryParser;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.LogisticRankingQuery;
import ose.processor.cascader.OSHits;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class LogisticSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static private final long serialVersionUID = 12312;
	 
	 Configuration conf;
	public LogisticSearchServlet() {
		super();
		conf = new Configuration();
		conf.addResource("os-site.xml");
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
		String featureQuery = request.getParameter("fquery");
		if (featureQuery == null){ //debugging
			featureQuery = "%TFFeature(Token('canon')) %TFFeature(HTMLTitle('canon'))";
		}
		String indexIdStr = request.getParameter("indexId");
		int indexId = conf.getInt("FeatureSearchServlet.indexId", 8);
		if (indexIdStr != null){
			indexId = Integer.parseInt(indexIdStr);
		}
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
		try {
			ResultPresenter scoredResult = getResultForQuery(indexId, domainId, featureQuery);
			
			PrintWriter out = response.getWriter();	
			if (startIndex != -1)
				scoredResult.setStartIndex(startIndex);
			
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			scoredResult.showJSONResult(iinfo, out);
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
		}finally {
			System.out.println("Done !");
		}
		
	}
	
	private ResultPresenter getResultForQuery(int indexId, int domainId, String featureQuery) throws SQLException, IllegalAccessException, InstantiationException, IOException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery osQuery = parser.parseFeatureQuery(featureQuery);
		List<ModelInfo> models = new ModelInfoManager().getModelsForDomainId(domainId);
		List<String> paths = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for (ModelInfo modelInfo : models){
			paths.add(modelInfo.getPath());
			weights.add(modelInfo.getWeight());
		}
		List<String> fieldNames = new FieldInfoManager().getFieldNamesForDomain(domainId);
		LogisticRankingQuery query = new LogisticRankingQuery(osQuery.getPredicates(), 
				paths.toArray(new String[]{}), 
				fieldNames.toArray(new String[]{}),
				weights.toArray(new Double[]{}));

		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		
		long startTime = System.currentTimeMillis();			
		OSHits result = query.search(reader);			
		long endTime = System.currentTimeMillis();
		
		ResultPresenter scoredResult = new ResultPresenter();
		scoredResult.setHowManyToReturn(10);
		int i = 0;
		for (Document doc : result) {			
			scoredResult.addDocScoreFeatures(result.getDocID(), result.score(), result.docFeatures());
		}
		System.out.println("Total documents : " + i + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
		return scoredResult;
	}
	
	public static void main(String[] args) throws Exception{
		int indexId = 602;
		int domainId = 6;
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		LogisticSearchServlet servlet = new LogisticSearchServlet();
		String featureQuery = "%Null() %Null() %Null() %Null() %Null() %Null() %Null() %Null() %Null() %BooleanFeature(Token(instructor)) %BooleanFeature(Token(semester)) %BooleanFeature(Token(grading)) %BooleanFeature(Token(homework)) %BooleanFeature(Token(textbook))";
		ResultPresenter result = servlet.getResultForQuery(indexId, domainId,  featureQuery);
		System.out.println(" --- " + result.showJSONResult(iinfo));
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		System.out.println(result.showResult(reader));
		reader.close();
	}
}