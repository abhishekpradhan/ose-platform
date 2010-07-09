package lbjse.objectsearch;

import java.util.List;
import java.util.Map.Entry;

import lbjse.data.Document;
import lbjse.data.DocumentFromTrec;

public class DocQueryPairFromFile implements DocQueryPair{

	private DocumentFromTrec doc;
	private Query query;
	private OracleTagRule oracle;
	private Boolean bOracle = null;
	
	public DocQueryPairFromFile(DocumentFromTrec doc, Query query) {
		this.doc = doc;
		this.query = query;
		oracle = new OracleTagRuleFromFile();
	}
	
	public boolean oracle(){
		if (bOracle == null)
			bOracle = getOracle();
		return bOracle;
	}
	
	private boolean getOracle(){
		for (Entry<String, String> fieldNameValuePair : query.getFieldValueMap().entrySet()) {
			if (!matchDocQueryField(doc, fieldNameValuePair.getKey(), fieldNameValuePair.getValue())){
				return false;
			}
		}
		return true;
	}
	
	private boolean matchDocQueryField(DocumentFromTrec doc, String fieldName, String fieldValue){
		List<String> tags = doc.getTagForField(fieldName);
		return oracle.satisfyTagRule(fieldName, fieldValue, tags);
		
	}
	
	public Document getDoc() {
		return doc;
	}
	
	public Query getQuery() {
		return query;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "<" + doc + "," + query + ">";
	}
}
