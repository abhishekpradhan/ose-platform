/**
 * 
 */
package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Nam Khanh Tran
 *
 */
public class QueryTransManager extends DBObjectManager {
	protected QueryTransManager(DatabaseManager parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}
	
	public QueryTransManager() {
		this(DatabaseManager.getDatabaseManager());
	}

	public void insert(QueryTrans object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();
		
		String query = "INSERT INTO QueryTrans (queryTransId, queryString, queryInfoId, domainId)" +
			"VALUES (" + object.getQueryTransId() + ", '" + object.getQueryString() + "', " +
			object.getQueryInfoId() + ", " + object.getDomainId() + " );";
		
		System.out.println(query); // for debug
		stmt.executeUpdate(query);
		stmt.close();
	}
	
	public QueryTrans queryByKey(int queryTransId) throws SQLException {
		List<QueryTrans> res = query("select * from QueryTrans where queryTransId = " + queryTransId);
		if (res.size() >= 1) return res.get(0);
		else return null;
	}

	public List<QueryTrans> query(String queryString) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ResultSet rs = stmt.executeQuery(queryString);

		List<QueryTrans> objectList = new ArrayList<QueryTrans>();

		while (rs.next()) {			
			objectList.add(new QueryTrans(
					rs.getInt("QueryTransId"), 
					rs.getString("QueryString"), 
					rs.getInt("QueryInfoId"),
					rs.getInt("DomainId")));
		}
//		conn.close();
		return objectList;
	}
	
	public int getQueryTransIdForQueryInfor(int queryId) throws SQLException{
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		int id = -1;
		ResultSet rs = stmt.executeQuery("Select queryTransId From QueryTrans Where QueryInfoId= " +queryId);
		
		if (rs.next()) {						
			id = rs.getInt("QueryTransId");
		}
//		conn.close();	
		return id;
	}

}
