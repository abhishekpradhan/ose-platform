/**
 * 
 */
package ose.testing;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import ose.index.IndexFieldConstant;

/**
 * @author kimpham2
 *
 */
public class ShowFilenameFromIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String INDEX_PATH_TO_READ = "C:/working/index/redu";
		IndexReader reader = IndexReader.open(INDEX_PATH_TO_READ);
		int START_INDEX = 278298;
		for (int i = START_INDEX; i < reader.numDocs(); i++){
			System.out.println(i + "\t" + reader.document(i).get(IndexFieldConstant.FIELD_DOCUMENT_ID));
		}
		reader.close();
	}

}
