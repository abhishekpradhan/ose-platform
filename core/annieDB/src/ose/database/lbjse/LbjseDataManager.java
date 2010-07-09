package ose.database.lbjse;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ose.database.DBObjectManager;
import ose.database.DatabaseManager;

public class LbjseDataManager extends DBObjectManager {
	
	public LbjseDataManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public LbjseDataManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(LbjseData  object) throws SQLException {
		Connection conn = dbMan.getConnection();
		String sql = "INSERT INTO lbjseData(Path, Description, IndexId) VALUES(?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, object.getPath());
        pstmt.setString(2, object.getDescription());
        pstmt.setInt(3, object.getIndexId());
		// Insert some values into the table
		pstmt.executeUpdate();
		
//		conn.close();
	}
	
	public List<LbjseData> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<LbjseData> objectList = new ArrayList<LbjseData>();
		
		while (rs.next()) {
			int id = rs.getInt("Id");
			String description = rs.getString("Description");
			String path = rs.getString("Path");
			int indexid = rs.getInt("IndexId");
			Date date = rs.getDate("DateCreated");
			objectList.add(new LbjseData(id, path, description, indexid, date));
		}
		
//		conn.close();
		return objectList;
		
	}
	
	public LbjseData getDataForId(int id) throws SQLException {
		List<LbjseData> indices = query("SELECT * FROM lbjseData WHERE Id = " + id);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
}
