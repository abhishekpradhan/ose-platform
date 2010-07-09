package lbjse.objectsearch;

import java.util.List;

import lbjse.data.Document;

import ose.database.DocInfo;

public class DocumentFromDatabase implements Document{
	private int docId;
	private DocAnnotations annotationManager;
	
	private boolean bHasAnnotation;
	
	private DocInfo fullText;
	
	public DocumentFromDatabase() {
		bHasAnnotation = false;
		fullText = null;
	}
	
	public DocumentFromDatabase(int docId, DocAnnotations annotationManager) {
		this();
		this.docId = docId;
		this.annotationManager = annotationManager;
		bHasAnnotation = true;
	}

	public int getDocId() {
		return docId;
	}
	
	public boolean hasAnnotation(){
		return bHasAnnotation;
	}
	
	public DocAnnotations getAnnotationManager() {
		return annotationManager;
	}
	
	@Override
	public String toString() {
		if (hasAnnotation())
			return "[A]Doc " + docId;
		else
			return "Doc " + docId;
	}
	
	public String getTitle() {
		if (fullText == null){
			fullText = annotationManager.getDocInfoForId(docId);
			if (fullText == null){
				System.out.println("Warning : doc " + docId + " has not been inserted into the database yet, please run IndexToDatabase.");
				return null;
			}
		}
		return fullText.getTitle();
	}
	
	public String getBody() {
		if (fullText == null){
			fullText = annotationManager.getDocInfoForId(docId);
			if (fullText == null){
				System.out.println("Warning : doc " + docId + " has not been inserted into the database yet, please run IndexToDatabase.");
				return null;
			}
		}
		return fullText.getBodyText();
	}
	
	public String getUrl() {
		return null;
	}
	
	public List<String >getTokenizedTitle() {
		return Utils.getTokenizedString(getTitle());
	}
	
	public List<String> getTokenizedBody() {
		return Utils.getTokenizedString(getBody());
	}
}
