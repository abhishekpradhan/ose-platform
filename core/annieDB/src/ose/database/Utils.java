package ose.database;

public class Utils {
	static public String escapeSQLString(String sqlStr) {
		return sqlStr.replaceAll("[\\\\]", "\\\\");		
	}

}
