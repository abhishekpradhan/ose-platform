package lbjse.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.trainer.Utils;
import common.CommandLineOption;

public class FilterDocumentByTagValues {

	public void filterByTagValues(String trecFile, String fieldName, String tagValueFile, String output) 
		throws IOException {
		LBJTrecFileParser trecReader = new LBJTrecFileParser(trecFile);
		List<DocumentFromTrec> docs = trecReader.getDocs();
		List<String> fieldValues = Utils.getFieldValuesFromFile(tagValueFile);
		Set<String> partitions = new HashSet<String>( fieldValues);
		Set<Integer> targetDocIds ;
		targetDocIds = Utils.getDocIdsWithFieldValues(docs,fieldName,  partitions);
		List<DocumentFromTrec> docSet = Utils.getSubset(docs, targetDocIds);
		PrintWriter writer = new PrintWriter(output);
		for (DocumentFromTrec doc : docSet) {
			doc.serialize(writer);
		}
		writer.close();
		System.out.println("Done saving " + docSet.size() + " docs to " + output);
	}
	
	public void filterByTagField(String trecFile, String fieldName, String output) 
		throws IOException {
		LBJTrecFileParser trecReader = new LBJTrecFileParser(trecFile);
		PrintWriter writer = new PrintWriter(output);
		int count = 0;
		while (true) {
			DocumentFromTrec doc = (DocumentFromTrec) trecReader.next();
			if (doc == null)
				break;
			if (doc.getTagForField(fieldName).size() > 0){ 
				doc.serialize(writer);
				count += 1;
			}
		}
		writer.close();
		System.out.println("Done saving " + count + " docs to " + output);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("" +
				"Usage : TrecFilter --mode tagvalues|tagfield --trec [trec path] --output [output file]\n" 
				);
		options.require(new String[]{"mode","trec","output"});
		String trecPath = options.getString("trec");
		String outputFile = options.getString("output");
		FilterDocumentByTagValues filter = new FilterDocumentByTagValues();
		if ("tagvalues".equals(options.getString("mode"))){
			options.require(new String[]{"field","tags"});
			filter.filterByTagValues(trecPath, options.getString("field"), options.getString("tags"), outputFile);
		}
		else if ("tagfield".equals(options.getString("mode"))){
			options.require(new String[]{"field"});
			filter.filterByTagField(trecPath, options.getString("field"), outputFile);
		} 		
		
	}

}
