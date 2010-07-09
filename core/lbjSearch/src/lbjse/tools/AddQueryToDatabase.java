package lbjse.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import ose.database.QueryInfo;
import ose.database.QueryInfoManager;

public class AddQueryToDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		QueryInfoManager qMan = new QueryInfoManager();
		BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
		while (true){
			String line = reader.readLine();
			if (line == null) break;
			QueryInfo qInfo = new QueryInfo(-1,line.trim(), line.trim(), 2);
			System.out.println("Inserting " + line);
			qMan.insert(qInfo);
		}
	}

}
