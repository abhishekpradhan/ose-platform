package ose.testing;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

public class MergeIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		
		String INDEX_PATH_TO_MERGE_TO = "C:\\working\\annieIndex\\training_index";
		String INDEX_PATH_TO_READ = "C:\\working\\annieIndex\\cameras_part1";

//		mergeTwoIndices(INDEX_PATH_TO_MERGE_TO, INDEX_PATH_TO_READ);
		mergeTwoIndices(INDEX_PATH_TO_MERGE_TO + "_cache", INDEX_PATH_TO_READ + "_cache");
//		INDEX_PATH_TO_READ = "C:/tmp/annieSearch/index/profs0";
//		reOptimizeIndex(INDEX_PATH_TO_READ);		
	}


	private static void reOptimizeIndex(String INDEX_PATH_TO_READ)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		long startTime = System.currentTimeMillis();
		
		IndexWriter writer = new IndexWriter(INDEX_PATH_TO_READ, new StandardAnalyzer(), false);
		writer.setUseCompoundFile(false);
		
		System.out.println("ReOptimizing : " + (System.currentTimeMillis() - startTime));
		
		writer.optimize();
		
		System.out.println("Done optimizing : " + (System.currentTimeMillis() - startTime));
		
		writer.close();
	}

	
	private static void mergeTwoIndices(
			String INDEX_PATH_TO_MERGE_TO, String INDEX_PATH_TO_READ)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		long startTime = System.currentTimeMillis();
		IndexWriter writer = new IndexWriter(INDEX_PATH_TO_MERGE_TO, new StandardAnalyzer(), false);
		IndexReader reader = IndexReader.open(INDEX_PATH_TO_READ);
		System.out.println("Merging : " + (System.currentTimeMillis() - startTime));
		
		writer.addIndexes(new IndexReader[]{reader});
		
		writer.optimize();
		
		writer.close();		
		reader.close();
		System.out.println("Done merging : " + (System.currentTimeMillis() - startTime));
	}

}
