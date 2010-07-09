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

public class LbjseFeatureTemplateManager extends DBObjectManager {
	
	public LbjseFeatureTemplateManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LbjseFeatureTemplateManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LbjseFeatureTemplate object) throws SQLException {
		Connection conn = dbMan.getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO lbjseFeatureTemplate(SessionId, Template, Weight) VALUES (?,?,?)");
		stmt.setInt(1, object.getSessionId() );
		stmt.setString(2, object.getTemplate() );
		stmt.setDouble(3, object.getWeight() );

		// Insert some values into the table
		stmt.executeUpdate();
		
		ResultSet result = stmt.getGeneratedKeys();
		if (result.next()){
			System.out.println("Got id = " + result.getInt(1));
			object.setId(result.getInt(1));
		}
		else {
			System.err.println("something is wrong with insert, doesn't get any id back");
		}
//		conn.close();
	}
	
	public List<LbjseFeatureTemplate> query(String queryString) throws SQLException {
		if (queryString.indexOf("IsDeleted") == -1)
			throw new SQLException("attention !!! IsDeleted criterion is missing ");
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LbjseFeatureTemplate> objectList = new ArrayList<LbjseFeatureTemplate>();
		
		while (rs.next()) {
			objectList.add(new LbjseFeatureTemplate(rs.getInt("TemplateId"), 
					rs.getInt("SessionId"),
					rs.getString("Template"),
					rs.getDouble("Weight")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public List<LbjseFeatureTemplate> getFeatureTemplateForSession(int sessionId) throws SQLException {
		return query("select * from lbjseFeatureTemplate where IsDeleted = 0 and SessionId = " + sessionId + " order by TemplateId");
	}
	
	public LbjseFeatureTemplate getFeatureTemplateForId(int featureTemplateId) throws SQLException {
		List<LbjseFeatureTemplate> indices = query("SELECT * FROM lbjseFeatureTemplate WHERE TemplateId = " + featureTemplateId );
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
}
