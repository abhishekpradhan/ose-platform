package lbjse.data;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ose.index.TrecDocument;
import ose.index.TrecFileReader;
import ose.index.Utils;

public class DocumentFromTrec implements Document {
	/*
	public static final String TAGS_TAG = "TAGS";
	public static final String TEXT_TAG = "TEXT";
	public static final String TITLE_TAG = "TITLE";
	public static final String URL_TAG = "URL";
	public static final String DOC_ID_TAG = "DOCID";
	public static final String DOC_TAG = "DOC";
	*/
	
	public static final String DOC_TAG = "OS_DOC";
	public static final String TAGS_TAG = "OS_TAGS";
	public static final String TEXT_TAG = "OS_PLAIN";
	public static final String TITLE_TAG = "OS_TITLE";
	public static final String URL_TAG = "OS_DOC_URL";
	public static final String DOC_ID_TAG = "OS_DOCID";
	
	
	private int docId;
	private String title;
	private String url;
	private String text;
	private String [] tags = new String[0];
	
	protected List<String> tokenizedCache = null;
	protected List<String> tokenizedBodyCache = null;
	
	public DocumentFromTrec() {
	}	
	
	public DocumentFromTrec(int docId, String url, String title, String text) {
		this.docId = docId;
		this.url = url;
		this.title = title;
		this.text = text;
	}
	
	public DocumentFromTrec(TrecDocument trec) {
		this.docId = trec.getDocId();
		this.url = trec.getUrl();
		this.title = trec.getTitle();
		this.text = trec.getPlainBody();
		setTags(trec.getTags());
	}
	
	public int getDocId() {
		return docId;
	}
	
	public void setDocId(int docId) {
		this.docId = docId;
	}
	
	@Override
	public String toString() {
		return "Doc " + docId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setTags(String tagStr) {
		if (tagStr != null)
			this.tags = tagStr.trim().split("\n");
	}

	public void setTags(Collection<String> tagCol) {
		if (tagCol == null) {
			tags = new String[]{};
		}
		else
			this.tags = tagCol.toArray(new String[]{});
	}
	
	public void setTags(String [] tags) {
		this.tags = tags;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getBody() {
		return text;
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public List<String> getTagForField(String fieldName) {
		List<String> res = new ArrayList<String>();
		if (tags != null ){
			for (String tag : tags)
				if (tag.startsWith(fieldName)){
					res.add( tag.substring(fieldName.length() + 1) );
				}
		}
		return res;
	}
	
	public List<String >getTokenizedTitle() {
		if (tokenizedCache == null){
			tokenizedCache = Utils.getTokenizedString(getTitle());
		}
		return tokenizedCache ; 
	}
	
	public List<String> getTokenizedBody() {
		if (tokenizedBodyCache == null){
			tokenizedBodyCache = Utils.getTokenizedString(getBody()); 
		}
		return tokenizedBodyCache;
	}
	
	public void serialize(PrintWriter output){
		output.println(TrecFileReader.openingTag(DOC_TAG));
		output.println(TrecFileReader.openingTag(DOC_ID_TAG));
		output.println(docId);
		output.println(TrecFileReader.closingTag(DOC_ID_TAG));
		output.println(TrecFileReader.openingTag(URL_TAG));
		output.println(url);
		output.println(TrecFileReader.closingTag(URL_TAG ));
		output.println(TrecFileReader.openingTag(TITLE_TAG));
		output.println(title);
		output.println(TrecFileReader.closingTag(TITLE_TAG ));
		output.println(TrecFileReader.openingTag(TEXT_TAG));
		output.println(text);
		output.println(TrecFileReader.closingTag(TEXT_TAG));
		output.println(TrecFileReader.openingTag(TAGS_TAG));
		for (String tag : tags)
			output.println(tag);
		output.println(TrecFileReader.closingTag(TAGS_TAG));
		output.println(TrecFileReader.closingTag(DOC_TAG ));
	}
}
