package ose.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

import common.CommandLineOption;

public class TrecIndexer {
	private static boolean bDebug = false;
	private static final int CHECK_POINT = 100;
	String indexPath ;
	String trecInputFile;
	
	public TrecIndexer(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public static String excludeExtension = "doc,exe,gif,gz,jpg,mpg,mpeg,pdf,ppt,ps,png,rtf,swf,zip,wmv,mov,avi";
	
	public static Pattern excludePatterns = null;
	
	public void index(String trecFilePath, boolean create) {
		long startTime = System.currentTimeMillis();
		try {
			TrecFileReader trecReader = new TrecFileReader(trecFilePath);
			IndexWriter writer = new IndexWriter(indexPath , new OSEAnalyzer(), create );
			IndexWriter cacheWriter = new IndexWriter(indexPath + "_cache" , new OSEAnalyzer(),create );
			OSEDocumentFactory factory = new OSEDocumentFactory();
			int count = 0;
			while (true){
				TrecDocument doc = trecReader.next();
				if (doc == null) break;
				if (bDebug){
					System.out.println("indexing : " + doc.getUrl());
				}
				if (excludePatterns.matcher(doc.getUrl()).find()){
					System.out.println("Skip : " + doc.getUrl());
					continue;
				}
				count += 1;
				Document indexedDocument = factory.createIndexedDocument(doc);
				if (indexedDocument != null) {
					writer.addDocument( indexedDocument );
					cacheWriter.addDocument( factory.createCacheDocument(doc) );
				}
				else {
					System.err.println("skipped " + doc.getUrl());
				}
				System.out.print(".");
				System.out.flush();
				if (count % CHECK_POINT == 0){
					System.out.println("Time : " + (System.currentTimeMillis() - startTime) + "\t" + count);
				}
			}
			writer.optimize();
			writer.close();
			cacheWriter.close();
			System.out.println(count + " documents indexed.");
			System.out.println("Time : " + (System.currentTimeMillis() - startTime) / 1000.0 );
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public String USAGE = "" +
			"Required options: \n" +
			"\t --trec [trec file]\n" +
			"\t --index [index file]\n" +
			"Optional options: \n" +
			"\t --debug: print debugging info \n" +
			"\t --create : create new index, overwrite existing \n" +
			"\t --exclude [exclude file extensions]\n" ;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"index","trec"});
		options.setUsage(USAGE);
		String trecInputFile = options.getString("trec");
		String indexPath = options.getString("index");
		boolean create = options.hasArg("create");
		if (create){
			System.out.println(" --> Creating new index.");
		}
		if (options.hasArg("exclude")){
			excludeExtension += "," + options.getString("exclude");
		}
		System.out.println("File extenstions excluded " + excludeExtension);
		excludePatterns = getUrlExcludePatterns();
		
		if (options.hasArg("debug")){
			System.out.println("Running debuging mode");
			bDebug = true;
		}
		
		System.out.println("Indexing ...");
		TrecIndexer indexer = new TrecIndexer(indexPath);
		indexer.index(trecInputFile,create);
		System.out.println("Done ! ");
	}
	private static Pattern getUrlExcludePatterns() {
		String regexStr = "";
		for (String ext : excludeExtension.split(",")){
			if (!regexStr.equals(""))
				regexStr += "|";
			regexStr = regexStr += "\\." + ext + "$";
		}
		System.out.println("Regex : " + regexStr);
		return Pattern.compile(regexStr);
	}

}
