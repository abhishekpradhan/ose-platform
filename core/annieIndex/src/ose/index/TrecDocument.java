package ose.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ose.html.HtmlParser;
import ose.html.ParsedHtmlDocument;

import common.profiling.Profile;

public class TrecDocument {
	private Integer docId = null; //id from a particular index, null=not exists
	private String url = null;
	private String content = null;
	private String tags= null;
	private String title = null;
	private String plainBody = null;
	
	public TrecDocument(){
		
	}
	
	public TrecDocument(IndexReader reader, int docId) throws IOException{
		this.docId = docId;
		Document doc = reader.document(docId);
		url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
		content = doc.get(IndexFieldConstant.FIELD_DOCUMENT_CONTENT);
		title = doc.get(IndexFieldConstant.FIELD_DOCUMENT_TITLE);
		plainBody = doc.get(IndexFieldConstant.FIELD_PLAIN_BODY);
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setTags(Collection<String> tags) {
		StringBuffer buffer = new StringBuffer();
		for (String tag : tags){
			buffer.append(tag);
			buffer.append("\n");
		}
		this.tags = buffer.toString().trim();
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getTags() {
		return tags;
	}
	
	public boolean isParsed() {
		return (plainBody != null);
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setPlainBody(String plainBody) {
		this.plainBody = plainBody;
	}
	
	public String getPlainBody() {
		return plainBody;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Integer getDocId() {
		return docId;
	}
	
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	
	@Override
	public String toString() {
		return "Url : " + url + "\nContent : " + content;
	}
	
	public void parseHtml(boolean useTagSoup){
		HtmlParser parser = new HtmlParser();
		parser.setUseTagSoup(useTagSoup);
		ParsedHtmlDocument parsedDoc = parser.parse(content);
		if (parsedDoc != null) {
			title = parsedDoc.getTitle();
			plainBody = parsedDoc.getPlainBody();
		}
        	
	}
	
	public void serialize(PrintWriter writer){
		if (writer == null)
			return ;
		writer.println(TrecFileReader.openingTag(TrecFileReader.OS_DOC_TAG));
		writer.println(TrecFileReader.openingTag(TrecFileReader.OS_URL_TAG));
		writer.println(url);
		writer.println(TrecFileReader.closingTag(TrecFileReader.OS_URL_TAG));
		if (docId != null){
			writer.println(TrecFileReader.openingTag(TrecFileReader.OS_DOCID_TAG));
			writer.println(docId);
			writer.println(TrecFileReader.closingTag(TrecFileReader.OS_DOCID_TAG));
		}
		if (title != null){
			writer.println(TrecFileReader.openingTag(TrecFileReader.OS_TITLE_TAG));
			writer.println(title);
			writer.println(TrecFileReader.closingTag(TrecFileReader.OS_TITLE_TAG));
		}
		if (plainBody != null){
			writer.println(TrecFileReader.openingTag(TrecFileReader.OS_PLAIN_TAG));
			writer.println(plainBody);
			writer.println(TrecFileReader.closingTag(TrecFileReader.OS_PLAIN_TAG));
		}
		if (tags != null){
			writer.println(TrecFileReader.openingTag(TrecFileReader.OS_TAGS_TAG));
			writer.println(tags );
			writer.println(TrecFileReader.closingTag(TrecFileReader.OS_TAGS_TAG));
		}
		if (content != null){
			writer.println(TrecFileReader.openingTag(TrecFileReader.OS_CONTENT_TAG));
			writer.println(content);
			writer.println(TrecFileReader.closingTag(TrecFileReader.OS_CONTENT_TAG));
		}
		writer.println(TrecFileReader.closingTag(TrecFileReader.OS_DOC_TAG));
	}
}
