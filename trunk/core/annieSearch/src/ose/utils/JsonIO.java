package ose.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonIO {

	static public String toJSONString(List<?> objList ){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		if (objList != null){
			boolean first = true;
			for (Object docId : objList) {
				if (!first) buffer.append(',');
				else first = false;
				buffer.append(toJSONString( docId ));
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	static public String toJSONString(Map<String, String[]> objList ){		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		boolean first = true;
		for (String docId : objList.keySet()) {
			if (!first) buffer.append(',');
			else first = false;
			buffer.append("\"" + docId + "\":" + toJSONString(objList.get(docId)));
		}
		buffer.append("}");
		return buffer.toString();
	}

	static public String mapToJSONString(Map<Object, Object> objList ){		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		boolean first = true;
		for (Object key : objList.keySet()) {
			if (!first) buffer.append(',');
			else first = false;
			buffer.append("\"" + key + "\":" + toJSONString(objList.get(key)));
		}
		buffer.append("}");
		return buffer.toString();
	}
	
	static public String toJSONString(String [] objList){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		boolean first = true;
		for (String docId : objList) {
			if (!first) buffer.append(',');
			else first = false;
			buffer.append(toJSONString( docId ));
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	static public String toJSONString(String t){
		return "\"" + t + "\"";
	}
	
	static public String toJSONString(Object t){
		return t.toString();
	}
	
	
	static public JSONObject toJSONObject(Map<Object, Collection> objList) throws JSONException{
		JSONObject result = new JSONObject();
		for (Object key : objList.keySet()) {
			result.put(key.toString(), objList.get(key));
		}
		return result;
	}
}