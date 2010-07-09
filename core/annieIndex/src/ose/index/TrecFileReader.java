package ose.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class TrecFileReader {
	public static final String OS_TAGS_TAG = "OS_TAGS";
	public static final String OS_CONTENT_TAG = "OS_DOC_CONTENT";
	public static final String OS_DOCID_TAG = "OS_DOCID";
	public static final String OS_URL_TAG = "OS_DOC_URL";
	public static final String OS_TITLE_TAG = "OS_TITLE";
	public static final String OS_PLAIN_TAG = "OS_PLAIN";
	public static final String OS_DOC_TAG = "OS_DOC";
	private static final int STATE_CONTENT = 1;
	private static final int STATE_OUTSIDE = 0;
	String buffer = null;
	BufferedReader reader = null;
	TrecDocument nextDoc = null;
	
	public TrecFileReader(String trecFilePath) throws FileNotFoundException{
		try {
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(trecFilePath),"utf-8"));
		} catch (FileNotFoundException e) {
			trecFilePath = TrecFileReader.class.getResource(trecFilePath).getFile();
			reader = new BufferedReader(new FileReader(trecFilePath));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

	private final int LINES_LIMIT = 10000;
	public TrecDocument next(){
		if (reader == null)
			return null;
		try {
			int state = STATE_OUTSIDE;
			String line;
			StringBuffer contentBuffer = new StringBuffer();
			int numberOfLines = 0;
			while ((line=reader.readLine()) != null){
				numberOfLines += 1;
				if (line.startsWith(TrecFileReader.openingTag(OS_DOC_TAG))){
					nextDoc = new TrecDocument();
//					nextDoc.setContent("");
				} else if (line.startsWith(TrecFileReader.closingTag(OS_DOC_TAG))){
					return nextDoc;
				} else if (line.startsWith(TrecFileReader.openingTag(OS_URL_TAG))){
					while ((line=reader.readLine().trim()).length() == 0) {}
					nextDoc.setUrl(line.trim());
					reader.readLine(); //skip "</OS_URL_TAG>"
				} else if (line.startsWith(TrecFileReader.openingTag(OS_DOCID_TAG))){
					while ((line=reader.readLine().trim()).length() == 0) {}
					nextDoc.setDocId(Integer.parseInt(line.trim()));
					reader.readLine(); //skip "</OS_DOCID>"
				} else if (line.startsWith(TrecFileReader.openingTag(OS_CONTENT_TAG))){
					state = STATE_CONTENT;
					contentBuffer = new StringBuffer();
					numberOfLines = 0;
				}  else if (line.startsWith(TrecFileReader.closingTag(OS_CONTENT_TAG))){
					state = STATE_OUTSIDE;
					nextDoc.setContent(contentBuffer.toString());
				} else if (line.startsWith(TrecFileReader.openingTag(OS_PLAIN_TAG))){
					state = STATE_CONTENT;
					contentBuffer = new StringBuffer();
					numberOfLines = 0;
				}  else if (line.startsWith(TrecFileReader.closingTag(OS_PLAIN_TAG))){
					state = STATE_OUTSIDE;
					nextDoc.setPlainBody(contentBuffer.toString());
				} else if (line.startsWith(TrecFileReader.openingTag(OS_TITLE_TAG))){
					state = STATE_CONTENT;
					contentBuffer = new StringBuffer();
					numberOfLines = 0;
				}  else if (line.startsWith(TrecFileReader.closingTag(OS_TITLE_TAG))){
					state = STATE_OUTSIDE;
					nextDoc.setTitle(contentBuffer.toString().trim());
				} else if (line.startsWith(TrecFileReader.openingTag(OS_TAGS_TAG))){
					state = STATE_CONTENT;
					contentBuffer = new StringBuffer();
					numberOfLines = 0;
				}  else if (line.startsWith(TrecFileReader.closingTag(OS_TAGS_TAG))){
					state = STATE_OUTSIDE;
					nextDoc.setTags(contentBuffer.toString().trim());
				} else if (state == STATE_CONTENT && numberOfLines < LINES_LIMIT){
					contentBuffer.append(line);
					contentBuffer.append('\n');
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<TrecDocument> getAllDocs(){		
		List<TrecDocument> allDocs = new ArrayList<TrecDocument>();
		while (true){
			TrecDocument doc = next();
			if (doc == null)
				break;
			else
				allDocs.add(doc);
		}
		return allDocs;
	}
	
	public void close() throws IOException {
		reader.close();
	} 
	
	static public String closingTag(String tag){
		return "</" + tag + ">";
	}

	static public String openingTag(String tag){
		return "<" + tag + ">";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TrecFileReader reader = new TrecFileReader(args[0]);
		int count = 0;
		while (reader.next() != null)
			count += 1;
		System.out.println("Done reading " + count + " trec documents.");
	}

}
