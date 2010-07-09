package ose.query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;

public class FeatureQuery {
	
	protected int domainId;
	protected List<String> featureStrings;
	
	public FeatureQuery() {
	}
	
	public void initializeFromDomain(int domainId) {
	}
	
	public void instantiateWithOQuery(OQuery oQuery) throws SQLException{
		if (oQuery.getDomainId() == -1){
			throw new RuntimeException("oQuery has unknown domain" );
		}
		this.domainId = oQuery.getDomainId();
		featureStrings = new ArrayList<String>();
		
		Map<Integer, String> fieldIdToNameMap = oQuery.getIdNameMap();
		Set<Integer> orderedKeys = new TreeSet<Integer>(fieldIdToNameMap.keySet());
		Map<Integer, Boolean> fieldSkipped = new HashMap<Integer, Boolean>();
		
		for (Integer fieldId : orderedKeys){
			List<FeatureInfo> features = new FeatureInfoManager().getFeatureForFieldId(fieldId);
			
			if (features != null && features.size() > 0){
				for (FeatureInfo featureInfo : features) {				
					if (fieldSkipped.containsKey(fieldId)) continue;
					if (!fieldIdToNameMap.containsKey(fieldId)){
						throw new RuntimeException("fieldId " + fieldId + " does not belong to domain " + domainId);
					}
					String featureValue = featureInfo.getTemplate();
					String fieldName = fieldIdToNameMap.get(fieldId);
					if (fieldName.equals(Constant.OSFIELD_OTHER)){
						featureStrings.add(featureValue );
					}
					else {
						if (oQuery.getFieldValue(fieldName) == null){
							throw new RuntimeException("fieldName " + fieldName + " is not found in the query " + oQuery);
						}
						String fieldValue = oQuery.getFieldValue(fieldName);
						if (fieldValue.trim().length() == 0){
							fieldSkipped.put(fieldId, true);
						}
						else{
							featureValue = featureValue.replaceAll("(?!\\W)" + fieldName.toUpperCase() + "(?=\\W)", fieldValue);
							featureStrings.add(featureValue );
						}
					}
				}
			}
		}
	}
	
	public String getFeatureString() {
		StringBuffer output = new StringBuffer();
		for (String fstr : featureStrings) {
			output.append(fstr);
			output.append(" ");
		}
		return output.toString();
	}
	
}
