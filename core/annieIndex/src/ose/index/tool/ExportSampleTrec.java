package ose.index.tool;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;

import common.CommandLineOption;

public class ExportSampleTrec {

	public ExportSampleTrec() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void exportRandom(String indexPath, int n, String outputFile) throws Exception{
		IndexReader reader = IndexReader.open(indexPath);
		int total = reader.numDocs();
		Set<Integer> randomIds = new HashSet<Integer>(); 
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < n; i++) {
			while (true){
				int k = rand.nextInt(total);
				if (!randomIds.contains(k)){
					randomIds.add(k);
					break;
				}
			}
		}
		
		PrintWriter writer = new PrintWriter(outputFile);
		for (Integer id : randomIds) {
			TrecDocument trec = new TrecDocument();
			Document doc = reader.document(id);
			trec.setContent(doc.get(IndexFieldConstant.FIELD_DOCUMENT_CONTENT));
			trec.setUrl(doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID));
			trec.serialize(writer);
		}
		writer.close();
		System.out.println("Done exporting to " + outputFile);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("Usage : ExportRandomSample --index [path to index] --n [number of trec files] --output [output file]");
		options.require(new String[] {"index","n","output"} );
		String indexPath = options.getString("index");
		int n = options.getInt("n");
		String outputFile = options.getString("output");
		ExportSampleTrec prog = new ExportSampleTrec();
		prog.exportRandom(indexPath, n, outputFile);
	}
}
