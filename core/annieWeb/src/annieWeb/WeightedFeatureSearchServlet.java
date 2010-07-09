package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Document;
import org.json.JSONException;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.learning.OracleFeedback;
import ose.processor.cascader.OSHits;
import ose.query.FeatureValue;
import ose.retrieval.OSSearcher;
import ose.retrieval.ResultPresenter;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class WeightedFeatureSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static private final long serialVersionUID = 12312;
	 
	 Configuration conf;
	 
	public WeightedFeatureSearchServlet() {
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
			featureQuery = "0.1 %TFFeature(Token('canon')) 1.0 %TFFeature(HTMLTitle('canon'))";
		}
		String indexIdStr = request.getParameter("indexId");
		int indexId = conf.getInt("FeatureSearchServlet.indexId", 7);
		if (indexIdStr != null){
			indexId = Integer.parseInt(indexIdStr);
		}
		int startIndex = -1;
		if (request.getParameter("start") != null){
			startIndex = Integer.parseInt(request.getParameter("start"));
		}
		
		Set<Integer> docSet = null;
		
		try {
			/* process the "annotOnly" option */
			if (request.getParameter("annotOnly") != null){
				int domainId = -1;
				if (request.getParameter("domainId") != null){
					domainId = Integer.parseInt(request.getParameter("domainId"));
				}
				else{
					System.out.println("domainId must be given with annotOnly options");
					return;
				}
				OracleFeedback oracle = new OracleFeedback(domainId, indexId);
				oracle.fetchTagsFromDatabase();
				docSet = oracle.getAnnotatedDocs().keySet();
			}
			
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			OSSearcher searcher = new OSSearcher(iinfo.getIndexPath());
			
			long startTime = System.currentTimeMillis();			
			OSHits result = searcher.weightedFeatureSearch(featureQuery);
			long endTime = System.currentTimeMillis();
			
			ResultPresenter scoredResult = new ResultPresenter();
			int i = 0;
			for (Document doc : result) {			
				i += 1;
				if (docSet == null || docSet.contains(result.getDocID())){ //filter the result
					List<FeatureValue> features = result.docFeatures();
					scoredResult.addDocScoreFeatures(result.getDocID(), result.score(), features );
				}
			}
			System.out.println("Total documents : " + i + "<br>");
			System.out.println("Total time : " + (endTime - startTime) );
			PrintWriter out = response.getWriter();	
			if (startIndex != -1)
				scoredResult.setStartIndex(startIndex);
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
		} 
		System.out.println("Done !");
	}
	
	public static void main(String[] args) throws SQLException, IOException, InstantiationException, IllegalAccessException, JSONException {
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(8);
//		WeightedFeatureSearchServlet servlet = new WeightedFeatureSearchServlet();
		OSSearcher searcher = new OSSearcher(iinfo.getIndexPath());
		String featureQuery = "100 %TFFeature(HTMLTitle(nikon)) 1.0 %TFFeature(HTMLTitle(reviews))";
		featureQuery = " 1000.0 %DivideBy(%CountNumber(Proximity(Number(_range(4,6)),Token(megapixel,megapixels,mp),-3,1)),%CountNumber(Proximity(Number(),Token(megapixel,megapixels,mp),-3,1)))   100 %TFFeature(HTMLTitle(megapixel mp mpix)) ";
		featureQuery = "100 %TFFeature(HTMLTitle(nikon)) 1.0 %TFFeature(HTMLTitle(reviews))";
		featureQuery = "1 %BooleanFeature(Token(bán))";		
		OSHits result = searcher.weightedFeatureSearch(featureQuery);
		System.out.println(" --- " + result);
		ResultPresenter scoredResult = new ResultPresenter();
		int i = 0;
		for (Document doc : result) {			
			i += 1;
			List<FeatureValue> features = result.docFeatures();
			scoredResult.addDocScoreFeatures(result.getDocID(), result.score(), features );
		}
		System.out.println(scoredResult.showJSONResult(iinfo));
	}
}