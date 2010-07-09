package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagRuleInfoManager extends DBObjectManager {
	Map<Integer, TagRuleInfo> cache = new HashMap<Integer, TagRuleInfo>();
	
	public TagRuleInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public TagRuleInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(TagRuleInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO TagRuleInfo(RuleId, FieldId, Value) "
				+ "VALUES (" + object.getRuleId() 
				+ ", " + object.getFieldId()   
				+ ", '" + object.getValue() + "'"
				+ ")");
//		conn.close();
	}
	
	public List<TagRuleInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<TagRuleInfo> objectList = new ArrayList<TagRuleInfo>();
		
		while (rs.next()) {
			objectList.add(new TagRuleInfo( rs.getInt("RuleId"), 
											rs.getInt("FieldId"),
											rs.getString("Value")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public TagRuleInfo getTagRuleInfoForFieldId(int fieldId) throws SQLException {
		if (cache.get(fieldId) != null){
			return cache.get(fieldId);
		}
		
		List<TagRuleInfo> indices = query("SELECT * FROM TagRuleInfo WHERE fieldId = " + fieldId);
		if (indices.size() == 1){
			cache.put(fieldId, indices.get(0));
			return indices.get(0);
		}
		else{
			return null;
		}
	}
}
