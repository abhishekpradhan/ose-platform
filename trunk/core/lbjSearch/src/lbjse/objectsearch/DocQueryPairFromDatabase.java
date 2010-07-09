package lbjse.objectsearch;

import java.util.Map.Entry;

public class DocQueryPairFromDatabase implements DocQueryPair {
	private DocumentFromDatabase doc;
	private Query query;
	
	public DocQueryPairFromDatabase(DocumentFromDatabase doc, Query query) {
		this.doc = doc;
		this.query = query;
	}
	
	public boolean oracle(){
		if (doc.hasAnnotation()){
			for (Entry<String, String> fieldNameValuePair : query.getFieldValueMap().entrySet()) {
				if (!matchDocQueryField(doc, fieldNameValuePair.getKey(), fieldNameValuePair.getValue())){
					return false;
				}
			}
			return true;
		}
		else 
			return false;
	}
	
	private boolean matchDocQueryField(DocumentFromDatabase doc, String fieldName, String fieldValue){
		DocAnnotations annotMan = doc.getAnnotationManager();
		String tags = annotMan.getAnnotationForField(doc.getDocId(), fieldName);
		if (tags == null)
			return false;
		return annotMan.satisfyTagRule(fieldName, fieldValue, tags);
		
	}
	
	public DocumentFromDatabase getDoc() {
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
