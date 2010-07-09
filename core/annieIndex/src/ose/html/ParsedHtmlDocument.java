package ose.html;

public class ParsedHtmlDocument {
	private String title = null;
	private String plainBody = null;
	
	public ParsedHtmlDocument() {
	}
	
	public String getPlainBody() {
		return plainBody;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setPlainBody(String plainBody) {
		this.plainBody = plainBody;
	}
	
}
