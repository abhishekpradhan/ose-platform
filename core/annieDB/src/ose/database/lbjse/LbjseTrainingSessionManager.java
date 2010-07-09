package ose.database.lbjse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import ose.database.DBObjectManager;
import ose.database.DatabaseManager;

public class LbjseTrainingSessionManager extends DBObjectManager {
	
	public LbjseTrainingSessionManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LbjseTrainingSessionManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LbjseTrainingSession object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO DomainInfo(DomainId, FieldId, Description,FeatureGeneratorClass, ClassifierClass, CurrentPerformance) "
				+ "VALUES (" + object.getDomainId() + "'"   
				+ ", '" + object.getFieldId() + "'"
				+ ", '" + object.getDescription() + "'"
				+ ", '" + object.getFeatureGeneratorClass() + "'"
				+ ", '" + object.getClassifierClass() + "'"
				+ ", '" + StringEscapeUtils.escapeSql(object.getCurrentPerformance()) + "')");
//		conn.close();
	}
	
	public List<LbjseTrainingSession> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LbjseTrainingSession> objectList = new ArrayList<LbjseTrainingSession>();
		
		while (rs.next()) {
			objectList.add(new LbjseTrainingSession( 
					rs.getInt("Id"),
					rs.getInt("DomainId"),
					rs.getInt("FieldId"),
					rs.getString("Description"),
					rs.getString("FeatureGeneratorClass"),
					rs.getString("ClassifierClass"),
					rs.getString("CurrentPerformance")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public LbjseTrainingSession getSessionForId(int id) throws SQLException {
		List<LbjseTrainingSession> indices = query("SELECT * FROM lbjseTrainingSession WHERE Id = " + id);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public int update(LbjseTrainingSession session) throws SQLException {
		Connection conn = dbMan.getConnection();
		PreparedStatement stmt = conn.prepareStatement("UPDATE lbjseTrainingSession " +
				" Set DomainId = ?, " +
				"   FieldId = ?, " +
				"   Description = ?, " +
				"   FeatureGeneratorClass = ?, " +
				"   ClassifierClass = ?, " +
				"   CurrentPerformance = ? " +
				" Where Id = ?");	
		stmt.setInt(1, session.getDomainId() );  
		stmt.setInt(2, session.getFieldId() );
		stmt.setString(3, session.getDescription() );
		stmt.setString(4, session.getFeatureGeneratorClass() );
		stmt.setString(5, session.getClassifierClass() );
		stmt.setString(6, session.getCurrentPerformance() );
		stmt.setInt(7, session.getId() );
		// Insert some values into the table
		return stmt.executeUpdate();
	}
}
