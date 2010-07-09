package ose.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DocInfoManager extends DBObjectManager{
	
	public DocInfoManager(){
		super(DatabaseManager.getDatabaseManager());
	}
	
	public DocInfoManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(DocInfo object) throws SQLException {
		Connection conn = dbMan.getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO DocInfo(DocId, Url, IndexId, Title, BodyText, Html) VALUES (?,?,?,?,?,?)");
		stmt.setInt(1, object.getDocId() );  
		stmt.setString(2, object.getUrl() );
		stmt.setInt(3, object.getIndexId() );
		stmt.setString(4, object.getTitle() );
		stmt.setString(5, object.getBodyText() );
		stmt.setString(6, object.getHtml() );
		
		// Insert some values into the table
		stmt.executeUpdate();
		
//		conn.close();
	}
	
	public List<DocInfo> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<DocInfo> objectList = new ArrayList<DocInfo>();
		
		while (rs.next()) {
			int theInt = rs.getInt("DocId");
			String str = rs.getString("Url");
			int indexId = rs.getInt("IndexId");
			String title = rs.getString("Title");
			String bodyText = rs.getString("BodyText");
			String html = rs.getString("Html");
			objectList.add(new DocInfo(theInt, str, indexId, title, bodyText, html));
		}
		
//		conn.close();
		return objectList;
		
	}
	
	public DocInfo getDocInfoForId(int docId, int indexId) throws SQLException{
		List<DocInfo> result = query("select * from DocInfo where docId = " + docId + " and indexId = " + indexId);
		if (result.size() > 0)
			return result.get(0);
		else
			return null;
	}
	
}
