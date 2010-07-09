/**
 * 
 */
package ose.testing;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestLuceneSkipTo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
//		testSkipTo(INDEX_PATH);
		testSkipToPerformance();
	}

	/**
	 * @param INDEX_PATH
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private static void testSkipTo(String INDEX_PATH) throws CorruptIndexException, IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		TermPositions termPos = reader.termPositions(new Term("Token","mp"));

		while (termPos.skipTo(1024596)){
			System.out.println("Doc : " + termPos.doc() + " , freq : " + termPos.freq());
			for (int i = 0 ; i < termPos.freq() ;i++)
				System.out.print(" " + termPos.nextPosition());
			System.out.println();
			break;
		}
	}
	
	private static void test(String INDEX_PATH) throws CorruptIndexException, IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		TermPositions termPos = reader.termPositions(new Term("Token","professor"));
		TermPositions termPos2 = reader.termPositions(new Term("Token","of"));
		TermPositions termPos3 = reader.termPositions(new Term("Token","computer"));
		int docId = 0;
		while (termPos.skipTo(docId) && termPos2.skipTo(docId) && termPos3.skipTo(docId) ){
			System.out.println("Doc : " + termPos.doc() + " , freq : " + termPos.freq());
			for (int i = 0 ; i < termPos.freq() ;i++)
				System.out.print(" " + termPos.nextPosition());
			System.out.println();
			
			System.out.println("Doc : " + termPos2.doc() + " , freq : " + termPos2.freq());
			for (int i = 0 ; i < termPos2.freq() ;i++)
				System.out.print(" " + termPos2.nextPosition());
			System.out.println();
			
			System.out.println("Doc : " + termPos3.doc() + " , freq : " + termPos3.freq());
			for (int i = 0 ; i < termPos3.freq() ;i++)
				System.out.print(" " + termPos3.nextPosition());
			System.out.println();
			
			break;
		}
	}
	
	private static void testSkipToPerformance() throws CorruptIndexException, IOException {
		int MAX_DOC = 1000000;
		String INDEX_PATH = "C:/working/index/random";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term term = new Term("Annotation","number");
//		TermPositions termPos = reader.termPositions(new Term("Token","$"));
		int N_SAMPLE = 30;
		System.out.print("All\t \t");
		for (int i = 0; i < N_SAMPLE ; i++){
			System.out.print("\t" + iterAll(reader.termPositions(term)));
		}
		System.out.println();
		
		for (int gap = 2; gap < MAX_DOC; gap *= 2){
			System.out.print("Jump\t" + gap + "\t");
			for (int i = 0; i < N_SAMPLE ; i++){
				System.out.print("\t" + iterJump(reader.termPositions(term), gap ));
			}
			System.out.println();
		}
		
		reader.close();
	}

	private static long iterJump(TermPositions termPos, int skipFor) throws IOException {
		long startTime;
		int lastDoc;
		startTime = System.currentTimeMillis();
		lastDoc = 0;
		while (termPos.skipTo(lastDoc)){
			if (termPos.doc() > lastDoc)
				lastDoc = termPos.doc();
			lastDoc += skipFor;
		}
//		System.out.println("Count : " + total);
//		System.out.println("Time : " + (System.currentTimeMillis() - startTime) );
		return (System.currentTimeMillis() - startTime);
	}

	private static long iterAll(TermPositions termPos) throws IOException {
		long startTime = System.currentTimeMillis();
//		int total = 0;
		int lastDoc = 0;
		while (termPos.next()){
//			total += termPos.freq();
			if (termPos.doc() > lastDoc)
				lastDoc = termPos.doc();
		}
		
//		System.out.println("Last doc : " + lastDoc);
//		System.out.println("Total : " + total);
//		System.out.println("Time : " + (System.currentTimeMillis() - startTime) );
		return System.currentTimeMillis() - startTime;
	}

}
