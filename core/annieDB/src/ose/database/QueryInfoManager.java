package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class QueryInfoManager extends DBObjectManager {

	public QueryInfoManager() {
		this(DatabaseManager.getDatabaseManager());
	}
	
	public QueryInfoManager(DatabaseManager parent) {
		super(parent);
	}

	public void insert(QueryInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO QueryInfo(QueryString, Description, DomainId) "
				+ "VALUES ('" + StringEscapeUtils.escapeSql( object.getQueryString() ) + "'" +
													 ", '" + object.getDescription() + "'" +
													 ",  " + object.getDomainId() + ")");		
//		conn.close();
	}

	public QueryInfo queryByKey(int queryId) throws SQLException {
		List<QueryInfo> res = query("select * from QueryInfo where queryId = " + queryId);
		if (res.size() >= 1) return res.get(0);
		else return null;
	}
	
	public List<QueryInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);

		List<QueryInfo> objectList = new ArrayList<QueryInfo>();

		while (rs.next()) {
			objectList.add(new QueryInfo(rs.getInt("QueryId"), rs.getString("QueryString"), 
					rs.getString("Description"), rs.getInt("domainId")));			
		}
//		conn.close();
		return objectList;

	}

}
