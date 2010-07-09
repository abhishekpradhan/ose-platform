package ose.index.tool;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

import common.CommandLineOption;

public class IndexMerger {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"destIndex","sourceIndex"});
				
		String INDEX_PATH_TO_MERGE_TO = options.getString("destIndex");
		String INDEX_PATH_TO_READ = options.getString("sourceIndex");
		System.out.println("Merging " + INDEX_PATH_TO_READ + " to " + INDEX_PATH_TO_MERGE_TO);
		mergeTwoIndices(INDEX_PATH_TO_MERGE_TO, INDEX_PATH_TO_READ);
		System.out.println("Done " + INDEX_PATH_TO_READ);
		if (options.getString("noCache") == null){
			String CACHE_TO_READ = INDEX_PATH_TO_READ + "_cache";
			System.out.println("Merging " + CACHE_TO_READ);
			mergeTwoIndices(INDEX_PATH_TO_MERGE_TO + "_cache", CACHE_TO_READ);
			System.out.println("Done " + CACHE_TO_READ);
		}
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
		
		System.out.println("Done merging : " + (System.currentTimeMillis() - startTime));
		System.out.println("optimizting..." );
		writer.optimize();
		
		writer.close();		
		reader.close();
		System.out.println("Done merging : " + (System.currentTimeMillis() - startTime));
	}

}
