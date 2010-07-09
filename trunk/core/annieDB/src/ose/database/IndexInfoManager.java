package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class IndexInfoManager extends DBObjectManager {
	
	public IndexInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public IndexInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(IndexInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO IndexInfo(IndexId, Description, IndexPath, Name, CachePath) "
				+ "VALUES (" + object.getIndexId() 
				+ ", '" + object.getDescription() + "'"   
				+ ", '" + object.getIndexPath() + "'"
				+ ", '" + object.getName() + "'"  
				+ ", '" + object.getCachePath() + "'" +
				")");
//		conn.close();
	}
	
	public List<IndexInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<IndexInfo> objectList = new ArrayList<IndexInfo>();
		
		while (rs.next()) {
			int iid = rs.getInt("IndexId");
			String description = rs.getString("Description");
			String path = rs.getString("IndexPath");
			String cachePath = rs.getString("CachePath");
			String name = rs.getString("Name");
			objectList.add(new IndexInfo(iid, description, path, name, cachePath));
		}
//		conn.close();
		return objectList;
		
	}
	
	public IndexInfo getIndexForId(int indexId) throws SQLException {
		List<IndexInfo> indices = query("SELECT * FROM IndexInfo WHERE IndexId = " + indexId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public void update(IndexInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "UPDATE IndexInfo" +
				" SET Name = ? ," +
				" Description = ?," +
				" IndexPath = ?," +
				" CachePath = ?" +
				" WHERE IndexId = ? ";
				
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, object.getName());
        pstmt.setString(2, object.getDescription());        
        pstmt.setString(3, StringEscapeUtils.escapeSql(object.getIndexPath()));
        pstmt.setString(4, StringEscapeUtils.escapeSql(object.getCachePath()));
        pstmt.setInt(5, object.getIndexId());
		// Insert some values into the table
		pstmt.executeUpdate();
		
//		conn.close();
	}
}
