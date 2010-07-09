package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModelInfoManager extends DBObjectManager {
	
	public ModelInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public ModelInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(ModelInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		if (object.getModelId() == -1) {
			stmt.executeUpdate("INSERT INTO ModelInfo(FieldId, Path, Weight) "
					+ "VALUES (" + object.getFieldId()    
					+ ", '" + object.getPath() + "'"
					+ ", '" + object.getWeight() + "'" + ")");
		}
		else {
			// Insert some values into the table
			stmt.executeUpdate("INSERT INTO ModelInfo(ModelId, FieldId, Path, Weight) "
					+ "VALUES (" + object.getModelId() 
					+ ", '" + object.getFieldId() + "'"   
					+ ", '" + object.getPath() + "'"
					+ ", '" + object.getWeight() + "'" + ")");
		}
//		conn.close();
	}
	
	public void update(ModelInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("UPDATE ModelInfo Set FieldId = " + object.getFieldId() + ", " +
				"Path = '" + object.getPath() + "', "
				+ " Weight = " + object.getWeight() + " " +
						"Where ModelId = " + object.getModelId());
//		conn.close();
	}
	
	public List<ModelInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<ModelInfo> objectList = new ArrayList<ModelInfo>();
		
		while (rs.next()) {
			objectList.add(new ModelInfo(rs.getInt("ModelId"), 
					rs.getInt("FieldId"),
					rs.getString("Path"),
					rs.getDouble("Weight")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public ModelInfo getModelInfoForFieldId(int fieldId) throws SQLException {
		List<ModelInfo> indices = query("SELECT * FROM ModelInfo WHERE fieldId = " + fieldId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<ModelInfo> getModelsForDomainId(int domainId) throws SQLException {
		return query("select ModelInfo.* from ModelInfo, FieldInfo " + 
					 "where FieldInfo.DomainId = " + domainId + 
					 "  and ModelInfo.FieldId = FieldInfo.FieldId");
	}
}
