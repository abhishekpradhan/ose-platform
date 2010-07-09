package ose.index.tool;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ose.database.DatabaseManager;
import ose.database.DocInfo;
import ose.database.DocInfoManager;
import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.DOMContentUtils;
import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;

import common.CommandLineOption;
import common.profiling.Profile;

public class IndexToDatabase {

	private boolean useTagSoup = true;
	private String title;
	private String plainBody;
	private int indexId;
	Set<Integer> docIdsToConvert;
	
	public IndexToDatabase(int indexId) {
		this.indexId = indexId;
	}

	public void populateDocInfo(int indexId, int domainId) throws SQLException, IOException{
		List<DocTag> docTags = new DocTagManager().getAllTagForIndexDomain(indexId, domainId);
		docIdsToConvert = new TreeSet<Integer>();
		for (DocTag tag : docTags) {
			docIdsToConvert.add(tag.getDocId());
		}
		populateDocInfo();
	}
	
	private void populateDocInfo() throws SQLException, IOException{
		IndexInfo indexIndo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader reader = IndexReader.open(indexIndo.getCachePath());
		DocInfoManager man = new DocInfoManager();
		
		int count = 0 ;
		for (Integer docId : docIdsToConvert) {
			if (!reader.isDeleted(docId)){
				Document doc = reader.document(docId);
				String cachedContent = doc.get(IndexFieldConstant.FIELD_DOCUMENT_CONTENT);
				String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
				TrecDocument trec = new TrecDocument();
				trec.setContent(cachedContent);
				trec.setUrl(url);
				annotate(trec);
				System.out.println("--- " + title);
				System.out.println("+++ " + plainBody.length());				
				System.out.println("... " + cachedContent.length());
				DocInfo dinfo = new DocInfo(docId, url, indexId, title, plainBody, cachedContent);
				man.insert(dinfo);
				count ++;
			}
			if ( count % 100 == 0){
				System.out.print(".");
			}
			if ( count % 10000 == 0){
				System.out.println();
			}
		}
		System.out.println("Done processing " + count + " documents.");
	}
	
	public void annotate(TrecDocument trec){
		title = null;
		plainBody = null;
		DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
        boolean badParse = true;
        try {
        	Profile.getProfile("parsing").start();
			parser.parse(getInputSource(trec), fragment);
			Profile.getProfile("parsing").end();
			DOMContentUtils utils = new DOMContentUtils();
			StringBuffer sb = new StringBuffer();
			utils.getTextInBody(sb, fragment);
			plainBody = sb.toString();
			sb = new StringBuffer();
			utils.getTitle(sb, fragment);
			title = sb.toString();
			badParse = false;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DOMException e){
			e.printStackTrace();
		} finally {
			if (badParse) {
				System.err.println("Error parsing " + trec.getUrl());
			}
		}
	}
	
	private InputSource getInputSource(TrecDocument trec) {
		if (useTagSoup){
			Parser tagSoupParser = new Parser();
	    	StringWriter stringWriter = new StringWriter();
	    	XMLWriter writer = new XMLWriter(stringWriter);
	    	InputSource input = new InputSource(new StringReader(trec.getContent()));
	    	tagSoupParser.setContentHandler(writer);        	
	    	try {
				tagSoupParser.parse(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return new InputSource(new StringReader(stringWriter.toString()));
		}
		else {
			return new InputSource(new StringReader(trec.getContent()));
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"index","domain"});
		if (options.getString("database") != null){
			System.out.println("Using database : " + options.getString("database"));
			DatabaseManager.getDatabaseManager(options.getString("database"));
		}
		int indexId = options.getInt("index");
		int domainId = options.getInt("domain");
		IndexToDatabase runner = new IndexToDatabase(indexId);
		runner.populateDocInfo(indexId, domainId);
	}

}
