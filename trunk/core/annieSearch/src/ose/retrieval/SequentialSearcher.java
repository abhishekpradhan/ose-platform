package ose.retrieval;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;

public class SequentialSearcher {

	
	private String indexPath ;
	
	public SequentialSearcher(String indexPath){
		this.indexPath = indexPath;
	}
	
	public void search(String query) throws IOException{
		IndexReader reader = IndexReader.open(indexPath);
		int pos = query.indexOf(":");
		String field = query.substring(0,pos);
		String regex = query.substring(pos+1);
//		Pattern pat = Pattern.compile(regex);
		int count = 0;
		for (int i = 0; i < reader.numDocs(); i++) {
			Document doc = reader.document(i);
			String value = doc.get(field);
			if (value == null)
				value = "[null]";
			if (value.matches(regex)){
				System.out.println(" --- Doc ID " + i);
				count += 1;
			}
		}
		System.out.println("Total result : " + count);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws SQLException, IOException{
		String query = "DOCUMENT_ID:http://www.ukdigitalcameras.co.uk/";
		query = "DOCUMENT_TITLE:\\[null\\]";
		query = "DOCUMENT_ID:.*pdf";
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(8);
		SequentialSearcher searcher = new SequentialSearcher(iinfo.getIndexPath());
		searcher.search(query);
	}

}
