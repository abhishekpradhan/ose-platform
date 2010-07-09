package lbjse.objectsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;

public class ObjectInfo {

	private int domainId;
	private Map<Integer, String> idNameMap;
	 
	public ObjectInfo(String domainFile) throws FileNotFoundException, IOException{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(domainFile));
		} catch (FileNotFoundException e) {
			domainFile =
			      ObjectInfo.class.getResource(domainFile).getFile();
			reader = new BufferedReader(new FileReader(domainFile));
		}
		
		domainId = Integer.parseInt(reader.readLine());
		String line;
		idNameMap = new HashMap<Integer, String>();
		while ( (line = reader.readLine()) != null){
			String [] toks = line.split("\\s+");
			if (toks.length != 2)
				throw new IOException("bad line in domain File : " + line );
			int fieldId = Integer.parseInt(toks[0]);
			idNameMap.put(fieldId, toks[1]);
		}
	}

	public ObjectInfo(int domainId) {
		this.domainId = domainId;
		initializeFromDomainID();
	}
	
	public String getFieldName(int fieldId) {
		return idNameMap.get(fieldId);
	}
	
	public List<String> getFieldNames(){
		return new ArrayList<String>(idNameMap.values());
	}
	
	public Map<Integer, String> getIdNameMap() {
		return idNameMap;
	}
	
	private void initializeFromDomainID() {
		try {
			idNameMap = new HashMap<Integer, String>();
			FieldInfoManager man = new FieldInfoManager();
			List<FieldInfo> fields = man.query("select * from FieldInfo where DomainId = " + domainId);
			for (FieldInfo fieldInfo : fields) {
				idNameMap.put(fieldInfo.getFieldId(), fieldInfo.getName());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
