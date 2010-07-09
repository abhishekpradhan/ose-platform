package ose.index.tool;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

import ose.index.IndexFieldConstant;

import common.CommandLineOption;

public class IndexUniqueMerger {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("Usage : IndexUniqueMerger [options]\n" +
				"Options : \n" +
				"	destIndex : merged index\n" +
				"	sourceIndex : index to be merged\n" +
				"	[withCache] : merge cache as well");
		options.require(new String[]{"destIndex","sourceIndex"});
				
		String INDEX_PATH_TO_MERGE_TO = options.getString("destIndex");
		String INDEX_PATH_TO_READ = options.getString("sourceIndex");
		System.out.println("Merging " + INDEX_PATH_TO_READ + " to " + INDEX_PATH_TO_MERGE_TO);
		mergeTwoIndices(INDEX_PATH_TO_MERGE_TO, INDEX_PATH_TO_READ);
		System.out.println("Done " + INDEX_PATH_TO_READ);
		if (options.getString("withCache") != null){
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
		boolean bCreate = ! (new File(INDEX_PATH_TO_MERGE_TO).exists() );
		Set<String> seenUrls = new HashSet<String>();
		if (!bCreate) //if the index is already there.
			seenUrls = getUrlSetFromIndex(INDEX_PATH_TO_MERGE_TO);
		
		System.out.println("Total unique url before merging : " + seenUrls.size());
		
		long startTime = System.currentTimeMillis();
		
		IndexWriter writer = new IndexWriter(INDEX_PATH_TO_MERGE_TO, new StandardAnalyzer(), bCreate);
		IndexReader reader = IndexReader.open(INDEX_PATH_TO_READ);
		System.out.println("Merging : " + (System.currentTimeMillis() - startTime));
		
		for (int i = 0 ; i< reader.numDocs(); i++){
			String url = reader.document(i).get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			if (seenUrls.contains(url)){
				reader.deleteDocument(i);
				System.out.println("Delete doc " + i + " with url " + url);
			}
			seenUrls.add(url);
		}
		writer.addIndexes(new IndexReader[]{reader});
		
		System.out.println("Done merging : " + (System.currentTimeMillis() - startTime));
		System.out.println("Total unique url after merging : " + seenUrls.size());
		System.out.println("optimizting..." );
		writer.optimize();
		
		writer.close();
		reader.undeleteAll();
		reader.close();
		System.out.println("Done optimizating : " + (System.currentTimeMillis() - startTime));
	}

	private static Set<String> getUrlSetFromIndex(String indexPath){
		Set<String> seenUrls = new HashSet<String>();
		try {
			IndexReader reader = IndexReader.open(indexPath);
			for (int i = 0 ; i < reader.numDocs(); i ++){
				String url = reader.document(i).get(IndexFieldConstant.FIELD_DOCUMENT_ID);
				seenUrls.add(url);
			}
			reader.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return seenUrls;
	}
}
