package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FeedbackManager extends DBObjectManager {

	public FeedbackManager(){
		super(DatabaseManager.getDatabaseManager());
	}
	
	public FeedbackManager(DatabaseManager parent) {
		super(parent);
	}

	public void insert(Feedback object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "INSERT INTO Feedback(QueryId, DocId, IndexId, Relevant) VALUES(?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, object.getQueryId());
        pstmt.setInt(2, object.getDocId());
        pstmt.setInt(3, object.getIndexId());
        pstmt.setBoolean(4, object.getRelevant());
		// Insert some values into the table
		pstmt.executeUpdate();
		
//		conn.close();
	}
	public void insertBatch(List<Feedback> objects) throws SQLException {
		
		Connection conn = dbMan.getConnection();
		conn.setAutoCommit(false);
		String sql = "INSERT INTO Feedback(QueryId, DocId, IndexId, Relevant) VALUES(?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Feedback object : objects) {
        	pstmt.setInt(1, object.getQueryId());
            pstmt.setInt(2, object.getDocId());
            pstmt.setInt(3, object.getIndexId());
            pstmt.setBoolean(4, object.getRelevant());
            pstmt.addBatch();
		}
        
        int [] updateCounts = pstmt.executeBatch();
        
        conn.commit();
//        conn.close();
        System.out.println("Inserted batch " + updateCounts.length);
	}
	
	public void insertUpdateBatch(List<Feedback> objects) throws SQLException {
		
		Connection conn = dbMan.getConnection();
		conn.setAutoCommit(false);
		String sql = "INSERT INTO Feedback(QueryId, DocId, IndexId, Relevant) VALUES(?,?,?,?)"
			+ " ON DUPLICATE KEY UPDATE Relevant = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Feedback object : objects) {
        	pstmt.setInt(1, object.getQueryId());
            pstmt.setInt(2, object.getDocId());
            pstmt.setInt(3, object.getIndexId());
            pstmt.setBoolean(4, object.getRelevant());
            pstmt.setBoolean(5, object.getRelevant());
            pstmt.addBatch();
		}
        
        int [] updateCounts = pstmt.executeBatch();
        
        conn.commit();
//        conn.close();
        System.out.println("Inserted batch " + updateCounts.length);
	}
	
	public void insertUpdate(Feedback object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO Feedback(QueryId, DocId, IndexId, DomainId,  Relevant) "
				+ "VALUES (" + object.getQueryId() 
				+ ", " + object.getDocId()  
				+ ", " + object.getIndexId()
				+ ", " + object.getDomainId()
				+ "," + object.getRelevant() + ")" 
				+ " ON DUPLICATE KEY UPDATE Relevant = " + object.getRelevant());
//		conn.close();
	}
	
	public List<Feedback> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);

		List<Feedback> objectList = new ArrayList<Feedback>();

		while (rs.next()) {
			int queryId = rs.getInt("QueryId");
			int docId = rs.getInt("DocId");
			int indexId = rs.getInt("IndexId");
			int domainId = rs.getInt("DomainId");
			boolean relevant = rs.getBoolean("Relevant");
			objectList.add(new Feedback(queryId, docId, indexId, domainId, relevant));
		}
		
//		conn.close();
		return objectList;
	}
	
	public Feedback getFeedbackForQuery(int queryId, int docId, int indexId) throws SQLException{
		List<Feedback> res = query("select * from Feedback where IndexId = " + indexId + 
				" and DocId = " + docId + " and QueryId = " + queryId);
		if (res.size() == 1)
			return res.get(0);
		else 
			return null;
	}
}
