package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FeatureInfoManager extends DBObjectManager {
	
	public FeatureInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public FeatureInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(FeatureInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO FeatureInfo(FieldId, Template, Weight) VALUES (?,?,?)");
		stmt.setInt(1, object.getFieldId() );  
		stmt.setString(2, object.getTemplate() );
		stmt.setDouble(3, object.getWeight() );

		// Insert some values into the table
		stmt.executeUpdate();
		
		ResultSet result = stmt.getGeneratedKeys();
		if (result.next()){
			System.out.println("Got id = " + result.getInt(1));
			object.setFeatureId(result.getInt(1));
		}
		else {
			System.err.println("something is wrong with insert, doesn't get any id back");
		}
//		conn.close();
	}
	
	public List<FeatureInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<FeatureInfo> objectList = new ArrayList<FeatureInfo>();
		
		while (rs.next()) {
			objectList.add(new FeatureInfo(rs.getInt("FeatureId"), 
					rs.getInt("FieldId"),
					rs.getString("Template"),
					rs.getDouble("Weight")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public List<FeatureInfo> getFeaturesForField(int fieldId) throws SQLException {
		return query("select * from FeatureInfo where IsDeleted = 0 and FieldId = " + fieldId + " order by FeatureId");
	}
	
	public int update(String updateString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		int res = stmt.executeUpdate(updateString);
		
//		conn.close();
		return res;
	}
	
	public FeatureInfo getIndexForId(int featureId) throws SQLException {
		List<FeatureInfo> indices = query("SELECT * FROM FeatureInfo WHERE featureId = " + featureId );
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<FeatureInfo> getFeatureForFieldId(int fieldId) throws SQLException {
		return query(	"select * from FeatureInfo" +
						" where IsDeleted = 0 " +						
						"   and FieldId = " + fieldId + 
						" order by FeatureId" ) ;
	}
}
