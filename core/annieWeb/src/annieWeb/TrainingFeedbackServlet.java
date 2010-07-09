package annieWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ose.database.DatabaseManager;
import ose.database.TrainingFeedback;
import ose.database.TrainingFeedbackManager;
import ose.utils.CommonUtils;

/**
 * Servlet implementation class for Servlet: FeedBackServlet
 *
 */
 public class TrainingFeedbackServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public TrainingFeedbackServlet() {
		super();
	}   	

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null){ //givefeedback
			List<Integer> relevantDocIds = CommonUtils.convertStringToList(request.getParameter("positive"));
			List<Integer> nonRelevantDocIds = CommonUtils.convertStringToList(request.getParameter("negative"));
			int queryId = 0;
			int indexId = 0;
			int domainId = 0;
			if (request.getParameter("queryId") == null){
				System.err.println("missing query Id");
				return ; 
			}
			if (request.getParameter("indexId") == null){
				System.err.println("missing index Id");
				return ; 
			}
			if (request.getParameter("domainId") == null){
				System.err.println("missing domain Id");
				return ; 
			}
			try {
				TrainingFeedbackManager fbMan = new TrainingFeedbackManager(DatabaseManager.getDatabaseManager());
				
				queryId = Integer.parseInt( request.getParameter("queryId") );
				indexId = Integer.parseInt( request.getParameter("indexId") );
				domainId = Integer.parseInt( request.getParameter("domainId") );
				System.out.println("Query ID : " + queryId);
				System.out.println("Index ID : " + indexId);
				System.out.println("Domain ID : " + indexId);
				
				for (Integer relId : relevantDocIds) {
					System.out.println("Adding feedback for doc id " + relId + " -> relevant . " );
					fbMan.insertUpdate(new TrainingFeedback(queryId, relId, indexId, domainId, true));
				}
				for (Integer nonRelId : nonRelevantDocIds) {
					System.out.println("Adding feedback for doc id " + nonRelId + " -> non relevant . " );
					fbMan.insertUpdate(new TrainingFeedback(queryId, nonRelId, indexId, domainId, false));
				}
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (action.equals("get")){
			int queryId = 0;
			int indexId = 0;
			int docId = 0;
			if (request.getParameter("queryId") == null){
				System.err.println("missing query Id");
				return ; 
			}
			if (request.getParameter("indexId") == null){
				System.err.println("missing index Id");
				return ; 
			}
			if (request.getParameter("docId") == null){
				System.err.println("missing docId");
				return ; 
			}
			try {
				TrainingFeedbackManager fbMan = new TrainingFeedbackManager(DatabaseManager.getDatabaseManager());
				
				queryId = Integer.parseInt( request.getParameter("queryId") );
				indexId = Integer.parseInt( request.getParameter("indexId") );
				docId = Integer.parseInt( request.getParameter("docId") );
				System.out.println("Query ID : " + queryId);
				System.out.println("Index ID : " + indexId);
				System.out.println("Doc ID : " + docId);
				TrainingFeedback fb = fbMan.getFeedbackForQuery(queryId, docId, indexId);
				if (fb != null) {
					PrintWriter writer = response.getWriter();
					writer.print(fb.getRelevant());
					writer.close();
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	

}