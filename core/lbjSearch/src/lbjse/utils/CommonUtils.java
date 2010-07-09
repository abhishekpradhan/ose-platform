package lbjse.utils;

import java.util.HashMap;
import java.util.Map;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonUtils {

	static public Map<String, String> convertObjectQueryJsonToMap(String objectQuery) {
		Map<String, String> res = new HashMap<String, String>();
		try {
			JSONObject oquery = new JSONObject(objectQuery);
			JSONArray names = oquery.names();
			if (names != null){
				for (int i = 0 ; i < names.length() ; i++){
					String fieldName = names.getString(i);
					String fieldValue = oquery.getString(fieldName);
					res.put(fieldName, fieldValue);
				}
			}
			return res;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* 
	 * we demands that the doc must have enough tags to "determine" the label for this doc,query pair.
	 */
	static public boolean hasEnoughTags(DocumentFromTrec doc, Query query){
		//if doc has "other" tag, then it's enough to give "false" label
		if (doc.getTagForField("other").size() > 0)
			return true;
		for (String field : query.getFieldValueMap().keySet()){
			if ("other".equals(field)) continue;
			if (query.getFieldValue(field).length() == 0) continue;
			if (doc.getTagForField(field).size() == 0)
				return false;
		}
		return true;
	}

}
