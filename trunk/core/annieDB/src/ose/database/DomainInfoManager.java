package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DomainInfoManager extends DBObjectManager {
	
	public DomainInfoManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public DomainInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(DomainInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO DomainInfo(DomainId, Name, Description) "
				+ "VALUES (" + object.getDomainId() + "'"   
				+ ", '" + object.getName() + "'"
				+ ", '" + object.getDescription() + "'" + ")");
//		conn.close();
	}
	
	public List<DomainInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<DomainInfo> objectList = new ArrayList<DomainInfo>();
		
		while (rs.next()) {
			objectList.add(new DomainInfo( 
					rs.getInt("DomainId"),
					rs.getString("Name"),
					rs.getString("Description")
					));
		}
//		conn.close();
		return objectList;
		
	}
	
	public DomainInfo getDomainInfoForId(int domainId) throws SQLException {
		List<DomainInfo> indices = query("SELECT * FROM DomainInfo WHERE domainId = " + domainId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
}
