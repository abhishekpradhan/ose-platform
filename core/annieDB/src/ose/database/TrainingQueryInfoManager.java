package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TrainingQueryInfoManager extends DBObjectManager {
	
	public TrainingQueryInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public TrainingQueryInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(TrainingQueryInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO TagRuleInfo(AttributeId, FieldId, Value) "
				+ "VALUES (" + object.getTrainingQueryId() 
				+ ", " + object.getFieldId()   
				+ ", '" + object.getValue() + "'"
				+ ")");
//		conn.close();
	}
	
	public List<TrainingQueryInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<TrainingQueryInfo> objectList = new ArrayList<TrainingQueryInfo>();
		
		while (rs.next()) {
			objectList.add(new TrainingQueryInfo(rs.getInt("AttributeId"), 
												 rs.getInt("FieldId"),
												 rs.getString("Value")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public TrainingQueryInfo getById(int fieldId) throws SQLException {
		List<TrainingQueryInfo> indices = query("SELECT * FROM TrainingQueryInfo WHERE attributeId = " + fieldId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<TrainingQueryInfo> getTrainingQueryForDomainId(int domainId) throws SQLException{
		return query("Select * from TrainingQueryInfo, FieldInfo where TrainingQueryInfo.FieldId = FieldInfo.FieldId and FieldInfo.DomainId = " + domainId);
	}
	
	public List<TrainingQueryInfo> getTrainingQueryForFieldId(int fieldId) throws SQLException{
		return query("Select * from TrainingQueryInfo where FieldId = " + fieldId);
	}
}
