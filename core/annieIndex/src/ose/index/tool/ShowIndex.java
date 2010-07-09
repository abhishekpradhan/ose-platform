/**
 * 
 */
package ose.index.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import common.CommandLineOption;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;

/**
 * @author Pham Kim Cuong
 *
 */
public class ShowIndex {
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("Examples:\n" +
				"Show all urls from an index\n" +
				   "\tShowIndex --mode showUrls --index [index path] --output [output file]\n" +
				"Update stat info for all indices in the database, skip invalid path\n" +
				   "\tShowIndex --mode updateDesc [--append]\n" +
				"Show the number of documents\n" +
				   "\tShowIndex --mode showStats --index [index path]");
		String mode = options.getString("mode");
		if (mode.equals("showUrls")){
			options.require(new String[]{"index","output"});
			String indexPath = options.getString("index");
			String outputFile = options.getString("output");
			try {
				int indexId = Integer.parseInt(indexPath); //try to see if user input indexId instead
				//if yes, then pull indexPath from DB
				indexPath = new IndexInfoManager().getIndexForId(indexId).getIndexPath();
			} catch (Exception e) {
				//do nothing
			}
			showUrls(indexPath, outputFile);
		}
		else if (mode.equals("showStats") ){
			options.require(new String[]{"index"});
			String indexPath = options.getString("index");
			showStats(indexPath);
		}
		else if (mode.equals("updateDesc") ){
			boolean append = options.hasArg("append");
			updateIndices(append);
		}
		else {
			System.err.println("Unknown command " + mode);
			options.printUsage();
		}
	}
	
	static private void updateIndices(boolean append) throws Exception{
		IndexInfoManager indexManager = new IndexInfoManager();
		List<IndexInfo> iinfoList = indexManager.query("select * from IndexInfo");
		for (IndexInfo iinfo : iinfoList){
			String statInfo = getIndexStatInfo(iinfo.getIndexPath());
			if (append)
				iinfo.setDescription(iinfo.getDescription() + statInfo);
			else
				iinfo.setDescription(statInfo);
			System.out.println("Updating " + iinfo + " with " + statInfo);
			indexManager.update(iinfo);
		}
	}
	
	static private String getIndexStatInfo(String indexPath){
		String result = "";
		try {
			IndexReader reader = IndexReader.open(indexPath);
			result = "[#docs:" + reader.numDocs() + "]";
			reader.close();
		} catch (Exception e) {
			System.out.println("warning");
			e.printStackTrace();
			result = "[error]";
		}
		return result;
	}
	
	static private void showStats(String indexPath) {
		try {
			IndexReader reader = IndexReader.open(indexPath);
			System.out.println("Statistics for " + indexPath);
			System.out.println("\tNumber of documents : " + reader.numDocs());
			reader.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void showUrls(String indexPath, String outputFile)
			throws FileNotFoundException, CorruptIndexException, IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		IndexReader reader = IndexReader.open(indexPath);
		int CHECK_POINT = 100;
		for (int i = 0 ; i < reader.numDocs(); i++){
			Document doc = reader.document(i);
			writer.println(i + "\t" + doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID));
			if (i % CHECK_POINT == 0){
				System.out.print(".");
				System.out.flush();
			}
		}
		writer.close();
		reader.close();
	}
}
