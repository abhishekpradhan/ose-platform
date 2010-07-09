package ose.index.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.index.IndexFieldConstant;

import common.CommandLineOption;

public class IndexTranslation {

	private String outputFile;
	private Map<String, Integer> idMap;
	
	public IndexTranslation(String outputFile) {
		this.outputFile = outputFile;
	}

	public void populateDocInfo(String indexFrom, String indexTo) throws SQLException, IOException{
		IndexReader reader = IndexReader.open(indexTo);
		idMap = new HashMap<String, Integer>();
		for (int i = 0; i < reader.numDocs(); i++) {
			Document doc = reader.document(i);
			String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			idMap.put(url, i);
		}
		reader.close();
		System.out.println("Done reading " + indexFrom );
		System.out.println("Number of files " + idMap.size());
		
		PrintWriter writer = new PrintWriter(outputFile);
		reader = IndexReader.open(indexFrom);
		int count = 0;
		for (int i = 0; i < reader.numDocs(); i++) {
			Document doc = reader.document(i);
			String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			if (idMap.containsKey(url)){
				writer.println(i + "\t" + idMap.get(url) + "\t" + url);
				count += 1;
			}
//			else {
//				System.out.println("Url not found " + url);
//			}
		}
		writer.close();
		reader.close();
		System.out.println("Done translation. Docs converted : " + count );
	}
	
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("" +
				"Usage : IndexTranslation [options] \n" +
				"    --indexFrom : source index \n" +
				"    --indexTo : destination index \n" +
				"    --output : outptu file \n" +
				"");
		options.require(new String[]{"indexFrom","indexTo","output"});
		String indexPathFrom = options.getString("indexFrom");
		String indexPathTo = options.getString("indexTo");
		String outputFile = options.getString("output");
		IndexTranslation runner = new IndexTranslation(outputFile);
		runner.populateDocInfo(indexPathFrom, indexPathTo);
	}

}
