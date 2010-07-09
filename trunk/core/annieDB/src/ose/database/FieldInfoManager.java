package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FieldInfoManager extends DBObjectManager {
	
	public FieldInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public FieldInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(FieldInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO FieldInfo(FieldId, DomainId, Name, Type, Description) "
				+ "VALUES (" + object.getFieldId() 
				+ ", '" + object.getDomainId() + "'"   
				+ ", '" + object.getName() + "'"
				+ ", '" + object.getType() + "'"
				+ ", '" + object.getDescription() + "'" + ")");
//		conn.close();
	}
	
	public List<FieldInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<FieldInfo> objectList = new ArrayList<FieldInfo>();
		
		while (rs.next()) {
			objectList.add(new FieldInfo(rs.getInt("FieldId"), 
					rs.getInt("DomainId"),
					rs.getString("Name"),
					rs.getString("Type"),
					rs.getString("Description"),
					rs.getInt("TrainingSessionId")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public FieldInfo getFieldInfoForId(int fieldId) throws SQLException {
		List<FieldInfo> indices = query("SELECT * FROM FieldInfo WHERE fieldId = " + fieldId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<String> getFieldNamesForDomain(int domainId) throws SQLException {
		List<String> fieldNames = new ArrayList<String>();
		List<FieldInfo> fields = getFieldInfoForDomain(domainId);
		for (FieldInfo fieldInfo : fields) {
			fieldNames.add( fieldInfo.getName() ); 
		}
		return fieldNames;
	}

	/**
	 * @param domainId
	 * @return
	 * @throws SQLException
	 */
	public List<FieldInfo> getFieldInfoForDomain(int domainId)
			throws SQLException {
		List<FieldInfo> fields = new FieldInfoManager().query("select * from FieldInfo " +
				"where domainId = " + domainId + 
				" order by fieldId");
		return fields;
	}
	
	public FieldInfo getFieldInfoByNameAndDomain(int domainId, String name) throws SQLException {
		List<FieldInfo> fields = new FieldInfoManager().query("select * from FieldInfo " +
				"where DomainId = " + domainId + 
				" and Name = '" + name + "'");
		if (fields.size() > 0)
			return fields.get(0);
		else
			return null;
	}
}
