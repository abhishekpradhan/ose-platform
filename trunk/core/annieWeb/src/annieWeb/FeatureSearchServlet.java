package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Document;
import org.json.JSONException;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.processor.cascader.OSHits;
import ose.query.FeatureValue;
import ose.retrieval.CombineResult;
import ose.retrieval.OSSearcher;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: FeatureSearchServlet
 *
 */
 public class FeatureSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static private final long serialVersionUID = 12312;
	 
	 Configuration conf;
	public FeatureSearchServlet() {
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
		int indexId = conf.getInt("FeatureSearchServlet.indexId", 7);
		if (indexIdStr != null){
			indexId = Integer.parseInt(indexIdStr);
		}
		int startIndex = -1;
		if (request.getParameter("start") != null){
			startIndex = Integer.parseInt(request.getParameter("start"));
		}
		try {
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			OSSearcher searcher = new OSSearcher(iinfo.getIndexPath());
			
			long startTime = System.currentTimeMillis();			
			OSHits result = searcher.featureSearch(featureQuery);
			long endTime = System.currentTimeMillis();
			
			CombineResult scoredResult = new CombineResult(0);
			int i = 0;
			for (Document doc : result) {			
				i += 1;
				List<FeatureValue> features = result.docFeatures();
				scoredResult.addInstanceWithFeatures(result.getDocID(), features );
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
		}finally {
			System.out.println("Done !");
		}
		
	}
	
	public static void main(String[] args) throws SQLException, IOException, InstantiationException, IllegalAccessException{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(7);
		FeatureSearchServlet servlet = new FeatureSearchServlet();
		OSSearcher searcher = new OSSearcher(iinfo.getIndexPath());
		String featureQuery = "100 %TFFeature(HTMLTitle(BRAND))";
		OSHits result = searcher.featureSearch(featureQuery);
		System.out.println(" --- " + result);
	}
}