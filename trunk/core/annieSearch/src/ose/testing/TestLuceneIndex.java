package ose.testing;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.LockObtainFailedException;

public class TestLuceneIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		testWriter();
//		testReader();
//		testDelete();
	}

	/**
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	private static void testWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		String INDEX_PATH = "C:/tmp/annieSearch/index/test";
		IndexWriter writer = new IndexWriter(INDEX_PATH, new StandardAnalyzer(), true);
		Document doc = new Document();
		Field field = new Field("Token","test",Field.Store.YES, Field.Index.TOKENIZED);
		field.setBoost(200.0f);
		doc.add(field);
		
		Document doc2 = new Document();
		Field field2 = new Field("Token","test",Field.Store.YES, Field.Index.TOKENIZED);
		field2.setBoost(30000.0f);
		doc2.add(field2);
		
		
		System.out.println("Boost : " + doc.getField("Token").getBoost());
		System.out.println("Boost : " + doc2.getField("Token").getBoost());
		
		writer.addDocument(doc);		
		writer.addDocument(doc2);
		writer.close();
		
		System.out.println("Done writing index.");
	}

	private static void testReader() throws CorruptIndexException, LockObtainFailedException, IOException {
		String INDEX_PATH = "C:/tmp/annieSearch/index/test";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		System.out.println("Num docs : " + reader.numDocs());
//		Document doc = reader.document(0);
//		System.out.println("Boost : " + doc.getField("Token").getBoost());
//				
//		Document doc2 = reader.document(1);
//		System.out.println("Boost : " + doc2.getField("Token").getBoost());
		
		float[] normDecoder = Similarity.getNormDecoder();
		
		byte [] norms = reader.norms("Token");
		System.out.println("Length norms : " + norms.length);
		for (int i = 0; i < norms.length; i++) {
			System.out.println("\t" + (1 / normDecoder[norms[i] & 0xFF])*(1 / normDecoder[norms[i] & 0xFF]) );
		}
		System.out.println("Done reading index.");
	}

	private static void testDelete() throws CorruptIndexException, LockObtainFailedException, IOException {
		String INDEX_PATH = "C:/tmp/annieSearch/index/amagoonew";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		System.out.println("Num doc : " + reader.numDocs());
		int n = reader.numDocs();
		for (int i = 3770; i < n ; i++){
			System.out.println("Deleting " + i);
			reader.deleteDocument(i);
		}
		reader.close();
//		
//		IndexWriter writer = new IndexWriter(INDEX_PATH, new StandardAnalyzer(), false);
//		Document doc = new Document();
//		writer.addDocument(doc);		
//		writer.close();
//		writer.optimize();
		System.out.println("Done writing index.");
	}

}
