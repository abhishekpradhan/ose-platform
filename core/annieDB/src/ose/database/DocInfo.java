package ose.database;

public class DocInfo {
	private int docId;
	private String url;
	private int indexId;
	private String title;
	private String bodyText;
	private String html;
	
	public DocInfo(int docId, String url, int indexId, String title, String bodyText, String html){
		this.docId = docId;
		this.url = url;
		this.indexId = indexId;
		this.title = title;
		this.bodyText = bodyText;
		this.html = html;
	}

	public DocInfo(int docId, String url, int indexId){
		this(docId, url, indexId, null,null,null);
	}
	
	public DocInfo(int docId, String url){
		this(docId, url, 0,null,null,null);
	}
	
	public int getDocId() {
		return docId;
	}
	
	public String getUrl() {
		return url;
	}

	public int getIndexId() {
		return indexId;
	}
	
	public String getBodyText() {
		return bodyText;
	}
	
	public String getHtml() {
		return html;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return "DocInfo : \t" + docId + "\t" + url + "\t" + indexId ;
	}
}
