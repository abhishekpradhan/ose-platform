package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.util.StringUtils;

public class LBJSECacheManager extends DBObjectManager {
	
	public LBJSECacheManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LBJSECacheManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LBJSECache object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO lbjseCache(QueryId, IndexId, CacheFile, DateCreated) "
				+ "VALUES (" + object.getQueryId() 
				+ ", " + object.getIndexId()   
				+ ", '" + StringEscapeUtils.escapeSql(object.getCacheFile()) + "'"
				+ ", '" + object.getDateCreated() + "'"				
				+ ")");
//		conn.close();
	}
	
	public void update(LBJSECache object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "UPDATE lbjseCache" +
				" SET QueryId = ? ," +
				" IndexId = ? ," +
				" CacheFile = ?," +
				" DateCreated = ?" +
				" WHERE Id = ? ";
				
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setInt(1, object.getQueryId());
        pstmt.setInt(2, object.getIndexId());        
        pstmt.setString(3, StringEscapeUtils.escapeSql(object.getCacheFile()));
        pstmt.setDate(4, object.getDateCreated());
        pstmt.setInt(5, object.getCacheId());
		// Insert some values into the table
		pstmt.executeUpdate();
		
//		conn.close();
	}
	
	public List<LBJSECache> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LBJSECache> objectList = new ArrayList<LBJSECache>();
		
		while (rs.next()) {
			objectList.add(new LBJSECache(rs.getInt("Id"),
					rs.getInt("QueryId"),
					rs.getInt("IndexId"),
					rs.getString("CacheFile"),
					rs.getDate("DateCreated")					
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public LBJSECache getById(int cacheId) throws SQLException {
		List<LBJSECache> indices = query("SELECT * FROM lbjseCache WHERE Id = " + cacheId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
	
	public LBJSECache getCacheForQuery(int queryId, int indexId) throws SQLException {
		List<LBJSECache> indices = query("SELECT * FROM lbjseCache WHERE QueryId = " + queryId
				+ " and IndexId = " + indexId );
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
}
