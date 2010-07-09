package ose.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DocTagManager extends DBObjectManager {
	
	public DocTagManager(){
		this(DatabaseManager.getDatabaseManager());
	}
	
	public DocTagManager(DatabaseManager parent){
		super(parent);
	}
	
	public void insert(DocTag object) throws SQLException {
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement();

		// Insert some values into the table
		stmt.executeUpdate("INSERT INTO DocTag(IndexId, DocId, FieldId, Value) "
				+ "VALUES ("  
				+ "'" + object.getIndexId() + "'"   
				+ ", '" + object.getDocId() + "'"
				+ ", '" + object.getFieldId() + "'" 
				+ ", '" + object.getValue() + "')");
		ResultSet result = stmt.getGeneratedKeys();
		if (result.next()){
			System.out.println("Got id = " + result.getInt(1));
			object.setTagId(result.getInt(1));
		}
		else {
			System.err.println("something is wrong with insert, doesn't get any id back");
		}
//		conn.close();
	}
	
	public List<DocTag> query(String queryString) throws SQLException {
		// Get another statement object initialized
		// as shown.
		Connection conn = dbMan.getConnection();
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// Query the database, storing the result
		// in an object of type ResultSet
		ResultSet rs = stmt.executeQuery(queryString);
		
		List<DocTag> objectList = new ArrayList<DocTag>();
		
		while (rs.next()) {
			objectList.add(new DocTag(rs.getInt("TagId"), 
					rs.getInt("IndexId"),
					rs.getInt("DocId"),
					rs.getInt("FieldId"),
					rs.getString("Value")
					));
		}
//		conn.close();
		return objectList;
		
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
	
	public List<DocTag> getAllTagForIndexDomain(int indexId, int domainId) throws SQLException{
		return query("select DocTag.* " +
				"	from DocTag, FieldInfo " +
				"   where DocTag.IndexId = " + indexId + 
				"   and FieldInfo.DomainId = " + domainId + 
				"	and DocTag.FieldId = FieldInfo.FieldId ");
	}
	
	public List<DocTag> getAllTagForIndexFieldId(int indexId, int fieldId) throws SQLException{
		return query("select * " +
				"	from DocTag " +
				"   where IndexId = " + indexId + 
				"   and FieldId = " + fieldId );
	}
	
	public List<DocTag> getAllTagForIndex(int indexId) throws SQLException{
		return query("select * " +
				"	from DocTag " +
				"   where IndexId = " + indexId );
	}
	
	public DocTag getTagForId(int tagId) throws SQLException {
		List<DocTag> indices = query("SELECT * FROM DocTag WHERE TagId = " + tagId);
		if (indices.size() == 1){
			return indices.get(0);
		}
		else{
			return null;
		}
	}
}
