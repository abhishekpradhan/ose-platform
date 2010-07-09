package ose.testing;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestJSON {
	static private void printJSONAArray(JSONArray arr) throws JSONException{
		for (int i = 0; i < arr.length() ; i++){
			System.out.println("\t" + arr.getJSONObject(i));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws JSONException{
		JSONObject json = new JSONObject("");
		Iterator iter = json.keys();
		while (iter.hasNext()){
			String key = (String) iter.next();
			Object item = json.get(key);
			if (item instanceof JSONArray) {
				JSONArray arrItem = (JSONArray) item;
				printJSONAArray(arrItem);
			}
			else 
				System.out.println("Key : " + key + "\t" + json.get(key).getClass() + "\t" + item );
		}

	}

}
