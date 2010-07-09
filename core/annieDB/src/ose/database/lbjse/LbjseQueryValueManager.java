package ose.database.lbjse;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ose.database.DBObjectManager;
import ose.database.DatabaseManager;

public class LbjseQueryValueManager extends DBObjectManager {
	
	public LbjseQueryValueManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LbjseQueryValueManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LbjseQueryValue object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "INSERT INTO lbjseQueryValue(SessionId, Value) VALUES(?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, object.getSessionId());
        pstmt.setString(2, object.getValue());
		// Insert some values into the table
		pstmt.executeUpdate();
		
		ResultSet result = pstmt.getGeneratedKeys();
		if (result.next()){
			System.out.println("Got id = " + result.getInt(1));
			object.setValueId(result.getInt(1));
		}
		else {
			System.err.println("something is wrong with insert, doesn't get any id back");
		}

//		conn.close();
	}
	
	public List<LbjseQueryValue> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LbjseQueryValue> objectList = new ArrayList<LbjseQueryValue>();
		
		while (rs.next()) {
			int id = rs.getInt("Id");
			String value = rs.getString("Value");
			int sessionId = rs.getInt("SessionId");
			objectList.add(new LbjseQueryValue(id, sessionId, value));
		}
		
//		conn.close();
		return objectList;
		
	}
	
	public LbjseQueryValue getValueForId(int id) throws SQLException {
		List<LbjseQueryValue> indices = query("SELECT * FROM lbjseQueryValue WHERE Id = " + id);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<LbjseQueryValue> getQueryValueForSession(int sessionId) throws SQLException {
		return query("select * from lbjseQueryValue where SessionId=" + sessionId);
	}
}
