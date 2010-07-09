package ose.database.lbjse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ose.database.DBObjectManager;
import ose.database.DatabaseManager;

public class LbjseSearchFeatureManager extends DBObjectManager {
	
	public LbjseSearchFeatureManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LbjseSearchFeatureManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LbjseSearchFeature object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "INSERT INTO lbjseSearchFeature(SessionId, Value) VALUES(?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, object.getSessionId());
        pstmt.setString(2, object.getValue());
		// Insert some values into the table
		pstmt.executeUpdate();
		
		ResultSet result = pstmt.getGeneratedKeys();
		if (result.next()){
			System.out.println("Got id = " + result.getInt(1));
			object.setFeatureId(result.getInt(1));
		}
		else {
			System.err.println("something is wrong with insert, doesn't get any id back");
		}

//		conn.close();
	}
	
	public List<LbjseSearchFeature> query(String queryString) throws SQLException {
		if (queryString.indexOf("IsDeleted") == -1){
			throw new SQLException("IsDeleted criterion is missing");
		}
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LbjseSearchFeature> objectList = new ArrayList<LbjseSearchFeature>();
		
		while (rs.next()) {
			int id = rs.getInt("FeatureId");
			String value = rs.getString("Value");
			int sessionId = rs.getInt("SessionId");
			objectList.add(new LbjseSearchFeature(id, sessionId, value));
		}
		
//		conn.close();
		return objectList;
	}
	
	public LbjseSearchFeature getFeatureForId(int id) throws SQLException {
		List<LbjseSearchFeature> indices = query("SELECT * FROM lbjseSearchFeature WHERE FeatureId = " + id);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<LbjseSearchFeature> getFeaturesForSession(int sessionId) throws SQLException {
		return query("SELECT * FROM lbjseSearchFeature WHERE SessionId = " + sessionId + 
				" AND IsDeleted = 0");
	}
}
