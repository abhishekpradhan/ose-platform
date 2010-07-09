package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBObjectManager {
	protected DatabaseManager dbMan;
	
	protected DBObjectManager(DatabaseManager parent){
		dbMan = parent;
	}

	static public String sqlString(Object obj){
		if (obj != null)
			return "'" + obj.toString() + "'";
		else
			return "null";
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
	
}
