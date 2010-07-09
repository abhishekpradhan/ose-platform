package annieWeb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DebugJSONString {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, JSONException{
		String fileName = args[0];
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		char [] cbuf = new char[100000];
		reader.read(cbuf);
		String str = new String(cbuf);
		System.out.println(str.substring(803));
		JSONObject array = new JSONObject(str);
		
	}

}
