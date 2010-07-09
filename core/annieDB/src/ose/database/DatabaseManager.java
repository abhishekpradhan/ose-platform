package ose.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;

public class DatabaseManager {
	private Connection con;
	private DocInfoManager docInfoManager;
	private QueryInfoManager queryInfoManager;
	private IndexInfoManager indexInfoManager;
	
	static private DatabaseManager singletonDBMan;
	private String dbConnectionString;
	private String dbUsername;
	private String dbPassword;
	private String dbConfigFile;
	
	static public DatabaseManager getDatabaseManager(){
		return getDatabaseManager("dbconfig.xml");
	}
	
	synchronized static public DatabaseManager getDatabaseManager(String configFile){
		if (singletonDBMan == null || !singletonDBMan.dbConfigFile.equals(configFile)){
			try {
				singletonDBMan = new DatabaseManager(configFile);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				singletonDBMan = null;
			}
		}
		return singletonDBMan;
	}
	
	private DatabaseManager(String configFile) throws ClassNotFoundException {
		// Register the JDBC driver for MySQL.
		Class.forName("com.mysql.jdbc.Driver");
		
		dbConfigFile = configFile;
		Configuration conf = new Configuration();
		conf.addResource(configFile);
		
		dbConnectionString = conf.get("database.connectionString");
		dbUsername = conf.get("database.username");
		dbPassword = conf.get("database.password");
		
		if (dbConnectionString == null || dbUsername == null || dbPassword == null) {
			throw new RuntimeException("bad database configurations : " + configFile);
		}
		
		System.out.println("DBManager Initialized with parameters : ");
		System.out.println("Connection : " + dbConnectionString);
		System.out.println("Username : " + dbUsername);
		System.out.println("Password : " + dbPassword);
		
	}
	
	synchronized public Connection getConnection() throws SQLException{
		try {
			if (con == null || con.isClosed()){
				con = DriverManager.getConnection(dbConnectionString, dbUsername,
				dbPassword);
			}
			con.setAutoCommit(true);
		} catch (RuntimeException e) { //what's going here?
			e.printStackTrace();
			con = DriverManager.getConnection(dbConnectionString, dbUsername,
			dbPassword);
		}
		return con;
	}
	
	public DocInfoManager getDocInfoManager() {
		if (docInfoManager == null){
			docInfoManager = new DocInfoManager(this);
		}
		return docInfoManager;
	}
	
	public QueryInfoManager getQueryInfoManager() {
		if (queryInfoManager == null){
			queryInfoManager = new QueryInfoManager(this);
		}
		return queryInfoManager;
	}
	
	public IndexInfoManager getIndexInfoManager() {
		if (indexInfoManager == null){
			indexInfoManager = new IndexInfoManager(this);
		}
		return indexInfoManager;
	}

	public void finish() throws SQLException {
		if (con != null){
			con.close();
		}
	}
}
