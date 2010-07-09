package ose.index.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import ose.index.TrecDocument;
import ose.index.TrecFileReader;
import ose.index.Utils;
import common.CommandLineOption;

public class TrecFilter {

	static void exportRange(String trecPath, String outputPath, int fromN, int toN) throws FileNotFoundException{
		TrecFileReader reader = new TrecFileReader(trecPath);
		try {
			PrintWriter writer = Utils.getPrintWriter(outputPath);
			for (int i = 0 ; i < fromN-1 ; i++){
				if (reader.next() == null){
					System.err.println("trec file too short");
					return;
				}
			}
			
			for (int j = fromN; j <= toN; j++){
				TrecDocument doc = reader.next();
				if (doc == null){
					System.err.println("trec file is cut at doc " + j);
					return;
				}
				doc.serialize(writer);
			}
			writer.close();		
			System.out.println("Done exporting doc from " + fromN + " to " + toN);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void exportUrls(String trecPath, String outputPath) throws IOException{
		TrecFileReader reader = new TrecFileReader(trecPath);
		PrintStream writer = new PrintStream(outputPath);
		int count = 0;
		while (true){
			TrecDocument doc = reader.next();
			if (doc == null)
				break;
			writer.println(doc.getUrl());
			count += 1;
		}
		writer.close();		
		System.out.println("Done exporting " + count + " docs" );
	}
	
	static void exportExcludeUrls(String trecPath, String outputPath, String excludeUrlFile) throws IOException{
		BufferedReader fileReader = new BufferedReader(new FileReader(excludeUrlFile));
		Set<String> urls = new HashSet<String>();
		while (true){
			String line = fileReader.readLine();
			if (line == null) break;
			urls.add(line.trim());
		}
		
		TrecFileReader reader = new TrecFileReader(trecPath);
		PrintWriter writer = new PrintWriter(outputPath);
		int count = 0;
		int filtered = 0;
		while (true){
			TrecDocument doc = reader.next();
			if (doc == null)
				break;
			if (!urls.contains( doc.getUrl() ) ){
				doc.serialize(writer);
			}
			else {
				filtered  += 1;
				System.out.println("Excluding " + doc.getUrl());
			}
			count += 1;
		}
		writer.close();		
		System.out.println("Done filtering " + count + " docs" );
		System.out.println("Filtered " + filtered + " docs" );
	}
	
	/*
	 * Perform deduplication, including normalizing trailing slashes as in "http://..../~blah/"  
	 */
	static void exportDedup(String trecPath, String outputPath) throws IOException{
		Set<String> urls = new HashSet<String>();
		TrecFileReader reader = new TrecFileReader(trecPath);
		PrintWriter writer = Utils.getPrintWriter(outputPath);
		int count = 0;
		int filtered = 0;
		while (true){
			TrecDocument doc = reader.next();
			if (doc == null)
				break;
			if (isUrlNormalized(doc.getUrl())){
				String l = doc.getUrl();
				String nml = urlNormalize(l);
				doc.setUrl(nml);
			}
			if (!urls.contains( doc.getUrl() ) ){
				doc.serialize(writer);
				urls.add(doc.getUrl());
			}
			else {
				filtered  += 1;
				System.out.println("Excluding " + doc.getUrl());
			}
			count += 1;
		}
		writer.close();		
		System.out.println("Done filtering " + count + " docs" );
		System.out.println("Filtered " + filtered + " docs" );
	}

	public static String urlNormalize(String url) {
		if (url.endsWith("/"))
			return url.substring(0,url.length()-1);
		return url;
	}
	
	static public boolean isUrlNormalized(String url){
		return url.endsWith("/");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("" +
				"Usage : TrecFilter --mode urls|exclude|range|dedup|single|random --trec [trec path] --output [output file]\n" 
				);
		options.require(new String[]{"mode","trec","output"});
		String trecPath = options.getString("trec");
		String outputFile = options.getString("output");
		if ("range".equals(options.getString("mode"))){
			options.require(new String[]{"from","to"});
			int fromNum = options.getInt("from");
			int toNum = options.getInt("to");
			exportRange(trecPath, outputFile, fromNum, toNum);
		}
		else if ("urls".equals(options.getString("mode"))){
			exportUrls(trecPath, outputFile);
		}
		else if ("dedup".equals(options.getString("mode"))){
			exportDedup(trecPath, outputFile);
		}
		else if ("exclude".equals(options.getString("mode"))){
			options.require(new String[]{"urls"});
			exportExcludeUrls(trecPath, outputFile, options.getString("urls"));
		}
		else {
			System.err.println("Unknown command");
			options.printUsage();
		}
	}
}
