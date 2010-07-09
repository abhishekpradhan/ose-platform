package lbjse.objectsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ose.utils.JsonIO;

public class Query {
	
	
	private ObjectInfo domainInfo;
	private Map<String, String> fieldValueMap;
	
	public Query() {
		domainInfo = null;
		fieldValueMap = new HashMap<String, String>();
	}
	
	private Query(ObjectInfo objectInfo, Map<String, String> map) {
		domainInfo = objectInfo;
		fieldValueMap = new HashMap<String, String>(map);
	}
	
	public Query(int domainId) {
		this();
		domainInfo = new ObjectInfo(domainId);
	}
	
	public Query(ObjectInfo objInfo, String json) {
		this();
		domainInfo = objInfo;
		try {
			JSONObject query = new JSONObject(json);
			for (Iterator<String> it = query.keys(); it.hasNext(); ){
				String key = it.next();
				setFieldValue(key, query.getString(key));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Query(int domainId, String json) {
		this(new ObjectInfo(domainId), json);
	}
	
	
	
	public void setFieldValue(int fieldId, String value) {
		if (domainInfo.getFieldName(fieldId) != null){
			fieldValueMap.put(domainInfo.getFieldName(fieldId), value);
		}
		else
			System.err.println("Unknown FieldId " + fieldId);
	}
	
	public void setFieldValue(String fieldName, String value) {
		fieldValueMap.put(fieldName, value);
	}
	
	public Map<String, String> getFieldValueMap() {
		return fieldValueMap;
	}
	
	public String getFieldValue(String fieldName) {
		fieldName = fieldName.toLowerCase();
		String res = fieldValueMap.get(fieldName);
		if (res == null)
			return "";
		else
			return res;
	}

	public String getFieldNameFromId(int fieldId) {
		return domainInfo.getFieldName(fieldId);
	}
	
	@Override
	public String toString() {
		return fieldValueMap.toString();
	}
	
	public String toJSONString() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("object", fieldValueMap);
		return json.getJSONObject("object").toString();
	}
	
	public String getFieldValue() {
		return null;
	}
	
	public Query clone(){
		return new Query(domainInfo, fieldValueMap);
	}
}
