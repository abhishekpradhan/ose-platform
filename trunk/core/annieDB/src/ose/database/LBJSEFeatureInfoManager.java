package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LBJSEFeatureInfoManager extends DBObjectManager {
	
	public LBJSEFeatureInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LBJSEFeatureInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LBJSEFeatureInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO lbjseFeatureInfo(FieldId, Template, Weight) VALUES (?,?,?)");
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
	
	public List<LBJSEFeatureInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LBJSEFeatureInfo> objectList = new ArrayList<LBJSEFeatureInfo>();
		
		while (rs.next()) {
			objectList.add(new LBJSEFeatureInfo(rs.getInt("FeatureId"), 
					rs.getInt("FieldId"),
					rs.getString("Template"),
					rs.getDouble("Weight")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public List<LBJSEFeatureInfo> getFeaturesForField(int fieldId) throws SQLException {
		return query("select * from lbjseFeatureInfo where IsDeleted = 0 and FieldId = " + fieldId + " order by FeatureId");
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
	
	public LBJSEFeatureInfo getIndexForId(int featureId) throws SQLException {
		List<LBJSEFeatureInfo> indices = query("SELECT * FROM lbjseFeatureInfo WHERE featureId = " + featureId );
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<LBJSEFeatureInfo> getFeatureForFieldId(int fieldId) throws SQLException {
		return query(	"select * from lbjseFeatureInfo" +
						" where IsDeleted = 0 " +						
						"   and FieldId = " + fieldId + 
						" order by FeatureId" ) ;
	}
}
