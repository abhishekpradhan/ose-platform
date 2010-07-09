package ose.index.tool;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.index.IndexFieldConstant;

public class IndexFilter {

	private String path;
	
	public IndexFilter(String indexPath) {
		path = indexPath;
	}

	public void filterUrl(Pattern urlPat) throws SQLException, IOException{
		IndexReader reader = IndexReader.open(path);
		for (int i = 0 ; i < reader.numDocs(); i++){
			if (!reader.isDeleted(i)){
				Document doc = reader.document(i);
				String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
				if (urlPat.matcher(url).matches()){
					System.out.println("Deleting " + i + "\t" + url);
					reader.deleteDocument(i);
				}
			}
				
		}

		System.out.println("Done processing " + reader.numDocs() + " documents.");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		String path = "C:\\working\\annieIndex\\training_index_cache";
		String path = "C:\\working\\annieIndex\\camera_training_index";
		IndexFilter runner = new IndexFilter(path);
//		Pattern binFiles = Pattern.compile(".*\\.(pdf|ps)$");
		Pattern binFiles = Pattern.compile("^$");
		runner.filterUrl(binFiles);
	}

}
