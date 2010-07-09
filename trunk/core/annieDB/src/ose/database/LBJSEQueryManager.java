package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LBJSEQueryManager extends DBObjectManager {
	
	public LBJSEQueryManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LBJSEQueryManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LBJSEQuery object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO lbjseQuery(QueryId, FieldId, Value) "
				+ "VALUES (" + object.getQueryId() 
				+ ", '" + object.getFieldId()   
				+ ", '" + object.getValue() + "'"
				+ ")");
//		conn.close();
	}
	
	public List<LBJSEQuery> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LBJSEQuery> objectList = new ArrayList<LBJSEQuery>();
		
		while (rs.next()) {
			objectList.add(new LBJSEQuery(rs.getInt("QueryId"), 
					rs.getInt("FieldId"),
					rs.getString("Value")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public LBJSEQuery getById(int fieldId) throws SQLException {
		List<LBJSEQuery> indices = query("SELECT * FROM lbjseQuery WHERE queryId = " + fieldId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<LBJSEQuery> getTrainingQueryForDomainId(int domainId) throws SQLException{
		return query("Select * from LBJSEQuery, FieldInfo where LBJSEQuery.FieldId = FieldInfo.FieldId and FieldInfo.DomainId = " + domainId);
	}
}
