package ose.database;

import org.apache.commons.lang.StringEscapeUtils;


public class TestMySQL {
	public static void main(String[] args) {
//		test1();
		System.out.println(StringEscapeUtils.escapeSql("C:\\working\\lbjSearch\\professor.ranked_result"));
	}

	/**
	 * 
	 */
	private static void test1() {
		try {
			DatabaseManager dbMan = DatabaseManager.getDatabaseManager();
			
			DocInfoManager docInfoMan = new DocInfoManager(dbMan);

			// Display URL and connection information
			System.out.println("Connection: " + dbMan.getConnection());

//			docInfoMan.insert(new DocInfo(1,"http://www.google.com/search?q=java+jdbc+tutorial&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a") );
//			docInfoMan.insert(new DocInfo(2,"http://localhost:8080/cache/training_1/nextag/price/price/800-more/Canon/by_price_800-more_canon_5.html"));
//			docInfoMan.insert(new DocInfo(3,"http://localhost:8080/cache/training_1/nextag/zoom/8x-more/Canon/by_zoom_8x-more_Canon_10.html"));
//			
			int i = 0 ;
			for (DocInfo obj : docInfoMan.query("SELECT * from DocInfo ORDER BY Url")){
				i += 1;
				System.out.println("Record #" + i + " : " + obj);
			}

			dbMan.finish();
			
		} catch (Exception e) {
			e.printStackTrace();
		}//end catch
	}
}
