package ose.processor.splock.utests;

import java.io.File;

import org.apache.lucene.index.IndexWriter;

import ose.index.OSEAnalyzer;
import ose.index.OSEDocumentFactory;
import ose.index.TrecDocument;
import ose.index.TrecFileReader;

public class UTestData {
	public static String [] col1_docs = new String [] {
		"screen size 10 display",
		"screen display",
		"14 display",
		"screen camera digital",
		"size 14",
		"display",
		"display 10",
	}; 
	
	public static String [] col2_docs = new String [] {
		"screen size 10 display",
		"screen display",
		"14 display",
		"screen camera digital",
		"size 14 camera",
		"display",
		"display 10 camera",
	}; 
	
	//focus : phrase with multiple spans in one document.
	public static String [] col3_docs = new String [] {
		"screen size 10 display 20 camera 20",
		"screen display",
		"14 display",
		"screen camera digital",
		"size 14 camera 20 display 30",
		"display",
		"display 10 camera",
	}; 
	
	//focus : phrase with multiple spans in one document.
	public static String [] col4_docs = new String [] {
		"screen size 10 display 20 camera 20",
		"size 10 display 20",
		"display 20 size 10 display 20 size 10",
		"screen camera digital",
		"size 14 camera 20 display 30",
		"display",
		"display 10 camera",
	};
	
	static public String INDEX_PATH = "/build/unittests/indexes";
	static public String indexPath1 = System.getProperty("user.dir") + INDEX_PATH + "/collection1";
	static public String indexPath2 = System.getProperty("user.dir") + INDEX_PATH + "/collection2";
	static public String indexPath3 = System.getProperty("user.dir") + INDEX_PATH + "/collection3";
	static public String indexPath4 = System.getProperty("user.dir") + INDEX_PATH + "/collection4";
	static public String indexPath_camera10 = System.getProperty("user.dir") + INDEX_PATH + "/camera_10";
	static public String indexPath_random100 = System.getProperty("user.dir") + INDEX_PATH + "/random100pages";
	
	static public void makeSureIndexExists() throws Exception{
		
		if (! new File(indexPath1).exists()) {
			createIndex(col1_docs, indexPath1);
		}
		
		if (! new File(indexPath2).exists()) {
			createIndex(col2_docs, indexPath2);
		}
		
		if (! new File(indexPath3).exists()) {
			createIndex(col3_docs, indexPath3);
		}
			
		if (! new File(indexPath4).exists()) {
			createIndex(col4_docs, indexPath4);
		}
		
		if (! new File(indexPath_camera10).exists()) {
			createIndex("utests/camera_10docs.trec", indexPath_camera10);
		}
		
		if (! new File(indexPath_random100).exists()) {
			createIndex("utests/random100pages.trec", indexPath_random100);
		}
		
	}
	
	static public void createIndex(String [] docs, String indexPath) throws Exception{
		if ( ! new File(indexPath).exists())
			new File(indexPath).mkdirs();
		IndexWriter writer = new IndexWriter(indexPath , new OSEAnalyzer(), true );
		OSEDocumentFactory factory = new OSEDocumentFactory();
		for (int i = 0; i < docs.length; i++) {
			TrecDocument doc = new TrecDocument();
			doc.setContent(docs[i]);
			doc.setUrl("local_document_" + i);
			writer.addDocument( factory.createIndexedDocument(doc) );
		}
		writer.optimize();
		writer.close();
		System.out.println("Index created : " + indexPath);
	}
	
	static public void createIndex(String trecFile, String indexPath) throws Exception{
		if ( ! new File(indexPath).exists())
			new File(indexPath).mkdirs();
		System.out.println("Creating index : " + indexPath + " from trec " + trecFile);
		IndexWriter writer = new IndexWriter(indexPath , new OSEAnalyzer(), true );
		TrecFileReader reader = new TrecFileReader(trecFile);
		OSEDocumentFactory factory = new OSEDocumentFactory();
		TrecDocument doc = null;
		while ( (doc=reader.next()) != null){
			writer.addDocument( factory.createIndexedDocument(doc) );
		}
		writer.optimize();
		writer.close();
		System.out.println("Index created : " + indexPath);
	}
	
	public static void main(String[] args) throws Exception {
		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/camera_10";
		UTestData.createIndex("utests/camera_10docs.trec", indexPath);
		indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/random100pages";
		UTestData.createIndex("utests/random100pages.trec", indexPath);		
	}
}
