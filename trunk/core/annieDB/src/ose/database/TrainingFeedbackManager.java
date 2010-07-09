package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TrainingFeedbackManager extends DBObjectManager {

	public TrainingFeedbackManager(){
		super(DatabaseManager.getDatabaseManager());
	}
	
	public TrainingFeedbackManager(DatabaseManager parent) {
		super(parent);
	}

	public void insert(TrainingFeedback  object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "INSERT INTO TrainingFeedback(QueryId, DocId, IndexId, Relevant) VALUES(?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, object.getQueryId());
        pstmt.setInt(2, object.getDocId());
        pstmt.setInt(3, object.getIndexId());
        pstmt.setBoolean(4, object.getRelevant());
        pstmt.setInt(5, object.getDomainId());
		// Insert some values into the table
		pstmt.executeUpdate();
		
//		conn.close();
	}
	public void insertBatch(List<TrainingFeedback> objects) throws SQLException {
		
		Connection conn = dbMan.getConnection();
		conn.setAutoCommit(false);
		String sql = "INSERT INTO TrainingFeedback(QueryId, DocId, IndexId, Relevant) VALUES(?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (TrainingFeedback object : objects) {
        	pstmt.setInt(1, object.getQueryId());
            pstmt.setInt(2, object.getDocId());
            pstmt.setInt(3, object.getIndexId());
            pstmt.setBoolean(4, object.getRelevant());
            pstmt.setInt(5, object.getDomainId());
            pstmt.addBatch();
		}
        
        int [] updateCounts = pstmt.executeBatch();
        
        conn.commit();
//        conn.close();
        System.out.println("Inserted batch " + updateCounts.length);
	}
	
	public void insertUpdate(TrainingFeedback object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO TrainingFeedback(QueryId, DocId, IndexId, DomainId,  Relevant) "
				+ "VALUES (" + object.getQueryId() 
				+ ", " + object.getDocId()  
				+ ", " + object.getIndexId()
				+ ", " + object.getDomainId()
				+ "," + object.getRelevant() + ")" 
				+ " ON DUPLICATE KEY UPDATE Relevant = " + object.getRelevant());
//		conn.close();
	}
	
	public List<TrainingFeedback> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);

		List<TrainingFeedback> objectList = new ArrayList<TrainingFeedback>();

		while (rs.next()) {
			int queryId = rs.getInt("QueryId");
			int docId = rs.getInt("DocId");
			int indexId = rs.getInt("IndexId");
			int domainId = rs.getInt("DomainId");
			boolean relevant = rs.getBoolean("Relevant");
			objectList.add(new TrainingFeedback(queryId, docId, indexId, domainId, relevant));
		}
		
//		conn.close();
		return objectList;
	}
	
	public TrainingFeedback getFeedbackForQuery(int queryId, int docId, int indexId) throws SQLException{
		List<TrainingFeedback> res = query("select * from TrainingFeedback where IndexId = " + indexId + 
				" and DocId = " + docId + " and QueryId = " + queryId);
		if (res.size() == 1)
			return res.get(0);
		else 
			return null;
	}
	
	public List<TrainingFeedback> getAllFeedbackForQuery(int queryId, int indexId) throws SQLException{
		return query("select * from TrainingFeedback where IndexId = " + indexId + 
				" and QueryId = " + queryId);
	}
	
	
}
