package ose.processor.splock.utests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ose.processor.Span;

public class TestResultReader {
	
	private BufferedReader reader = null;
	private String testFileName = null;
	private String indexPath = null;
	private String featureString = null;
	private int totalSpans, totalDoc;
	TestResultReader(String testFileName){
		this.testFileName = testFileName;
	}
	
	public String getIndex(){
		if (indexPath == null){
			readHeader();
		}
		return indexPath;
	}

	/**
	 * 
	 */
	private void readHeader() {
		if (reader == null){
			try {
				reader = new BufferedReader(new FileReader(testFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return ;
			}
		}
		try {
			makeSure( reader.readLine().equals("<UTEST>") );
			makeSure( reader.readLine().equals("<INDEX>") );
			indexPath = reader.readLine();
			makeSure( reader.readLine().equals("</INDEX>") );
			makeSure( reader.readLine().equals("<FEATURE>") );
			featureString = reader.readLine();
			makeSure( reader.readLine().equals("</FEATURE>") );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makeSure(boolean mustBeTrue) throws AssertionError {
		if (!mustBeTrue)
			throw new AssertionError();
	}
	
	public String getFeatureString(){
		if (featureString == null){
			readHeader();
		}
		return featureString;
	}
	
	public Span nextSpan(){
		if (reader == null)
			readHeader();
		try {
			while (true){
				String line = reader.readLine();
				if (line == null)
					break;
				else if (line.equals("<SPAN>")){
					int docId = readTagValue("DOCID");
					int pos = readTagValue("POS");
					makeSure( reader.readLine().equals("</SPAN>") );
					return new Span(docId, pos, -1); 
				}
				else{
					totalDoc = readTagValue("TOTALDOC", line);
					totalSpans = readTagValue("TOTALSPAN");
					makeSure( reader.readLine().equals("</UTEST>") );
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int readTagValue(String tag) throws IOException{
		return readTagValue(tag, null);
	}
	
	public int readTagValue(String tag, String previousLine) throws IOException{
		while (true){
			String line = null;
			if (previousLine != null){
				line = previousLine;
				previousLine = null;
			}
			else
				line = reader.readLine();
			if (line == null)
				return -1;
			line = line.trim();
			if (line.length() > 0){
//				System.out.println("===" + line + " === " + tag);
				makeSure( line.startsWith("<" + tag + ">") );
				makeSure( line.endsWith("</" + tag + ">") );
//				System.out.println("---" + line);
				return Integer.parseInt(line.substring(tag.length() + 2, line.length() - tag.length() - 3));
			}
		}
	}
	
	public int getTotalDoc() {
		return totalDoc;
	}
	
	public int getTotalSpan() {
		return totalSpans;
	}
	
	public void finish(){
		try {
			if (reader != null)
				reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TestResultReader reader = new TestResultReader("utests/Test1.xml");
		System.out.println("Index " + reader.getIndex());
		System.out.println("Feature " + reader.getFeatureString());
		while (true){
			Span span = reader.nextSpan();
			if (span == null) 
				break;
			System.out.println("Got span : " + span);
		}
	
		System.out.println("Total Doc : " + reader.getTotalDoc());
		System.out.println("Total Span : " + reader.getTotalSpan());
		
		reader.finish();
	}
}
