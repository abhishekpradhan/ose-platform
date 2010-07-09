package lbjse.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import lbjse.objectsearch.LBJTrecFileParser;
import common.CommandLineOption;

public class LBJTrecMerger {
	
	public LBJTrecMerger() {
		// TODO Auto-generated constructor stub
	}
	
	public void merge(String firstTrec, String secondTrec) throws IOException {
		LBJTrecFileParser parser = new LBJTrecFileParser(firstTrec);
		Set<String> urlSet = new HashSet<String>();
		int maxId = 0;
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			urlSet.add( doc.getUrl() );
			if (doc.getDocId() > maxId )
				maxId = doc.getDocId();
		}
		PrintWriter appender = new PrintWriter(new FileOutputStream(firstTrec,true));
		parser = new LBJTrecFileParser(secondTrec);
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			doc.setDocId(doc.getDocId() + maxId);
			if ( !urlSet.contains( doc.getUrl() ) )
				doc.serialize(appender);
			else{
				System.out.println("Duplicated " + doc.getUrl());
			}
		}
		appender.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"first","second"});
		String firstTrec = options.getString("first");
		String secondTrec = options.getString("second");
		LBJTrecMerger merger = new LBJTrecMerger();
		merger.merge(firstTrec, secondTrec);
		System.out.println("Done !");
	}

}
