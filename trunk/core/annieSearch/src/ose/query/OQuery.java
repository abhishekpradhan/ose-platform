package ose.query;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;

public class OQuery {
	
	private int domainId;
	private Map<Integer, String> idNameMap;
	
	private Map<String, String> fieldValueMap;
	
	public OQuery() {
		domainId = -1;
		fieldValueMap = new HashMap<String, String>();
	}
	
	public OQuery(int domainId) {
		this();
		this.domainId = domainId;
		initializeFromDomainID();
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public OQuery(int domainId, String json) {
		this(domainId);
		try {
			JSONObject query = new JSONObject(json);
			for (Iterator<String> it = query.keys(); it.hasNext(); ){
				String key = it.next();
				setFieldValue(key, query.getString(key).trim());
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeFromDomainID() {
		try {
			idNameMap = new HashMap<Integer, String>();
			FieldInfoManager man = new FieldInfoManager();
			List<FieldInfo> fields = man.query("select * from FieldInfo where DomainId = " + domainId);
			for (FieldInfo fieldInfo : fields) {
				idNameMap.put(fieldInfo.getFieldId(), fieldInfo.getName());
				fieldValueMap.put(fieldInfo.getName(), "");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setFieldValue(int fieldId, String value) {
		if (idNameMap.containsKey(fieldId)){
			fieldValueMap.put(idNameMap.get(fieldId), value);
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
	
	public Map<Integer, String> getIdNameMap() {
		return idNameMap;
	}
	
	public String getFieldValue(String fieldName) {
		fieldName = fieldName.toLowerCase();
		return fieldValueMap.get(fieldName);
	}

	public String getFieldNameFromId(int fieldId) {
		return idNameMap.get(fieldId);
	}
	
	public Integer getFieldIdFromName(String fieldName) {
		for (Integer id : idNameMap.keySet())
			if (idNameMap.get(id).equals(fieldName))
				return id;
		return -1;
	}
	
	@Override
	public String toString() {
		return fieldValueMap.toString();
	}
	
	public String getJsonString() throws JSONException{
		JSONObject json = new JSONObject();
		for (String key : fieldValueMap.keySet()){
			json.put(key, fieldValueMap.get(key));
		}
		return json.toString();
	}
}
