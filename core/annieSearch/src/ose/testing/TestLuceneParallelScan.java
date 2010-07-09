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
public class TestLuceneParallelScan {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
//		testSkipTo(INDEX_PATH);
		testParallelScan();
	}

	private static void testParallelScan() throws CorruptIndexException, IOException {
		String INDEX_PATH = "C:/tmp/annieSearch/index/profs";
//		String INDEX_PATH = "C:/working/index/random";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term term = new Term("Annotation","number");
		Term term2 = new Term("Token","the");
		Term term3 = new Term("Token","and");
		Term term4 = new Term("Token","to");
		long startTime = System.currentTimeMillis();
		int count = 0;
		int test = 1;
		for (int iter = 0; iter < 1; iter++) {
			if (test == 0){
				count += parallelScan(new TermPositions[] {
						reader.termPositions(term), 
						reader.termPositions(term2),
						reader.termPositions(term3),
						reader.termPositions(term4)
						});
			}
			else {
				count += sequentialScan(new TermPositions[] {
						reader.termPositions(term), 					
						reader.termPositions(term2),
						reader.termPositions(term3),
						reader.termPositions(term4)
						});
			}
		}
		System.out.println("Count : " + count);
		System.out.println("Time : " + ( System.currentTimeMillis() - startTime) );
	}

	/**
	 * @param count
	 * @param termPos
	 * @param termPos2
	 * @return
	 * @throws IOException
	 */
	private static int parallelScan(TermPositions [] termPos) throws IOException {
		int n = termPos.length;
		int count = 0;
		int tt = 0;
		int missed = 0;
		while (missed < n){
			int k = tt % n ;
			if (termPos[k].next()){
				count += 1;
				int d = termPos[k].doc();
				for (int i = 0; i < termPos[k].freq(); i++) {
					int p = termPos[k].nextPosition();	
				}
				missed = 0 ;
			}
			else{
				missed += 1;
			}
			tt ++;
		}
		return count;
	}

	/**
	 * @param count
	 * @param termPos
	 * @param termPos2
	 * @return
	 * @throws IOException
	 */
	private static int sequentialScan(TermPositions [] termPos) throws IOException {
		int n = termPos.length;
		int count = 0;
		for (int i = 0; i < termPos.length; i++) {
			while (termPos[i].next()){
				count += 1;
				int d = termPos[i].doc();
				for (int j = 0; j < termPos[i].freq(); j++) {
					break;
//					int p = termPos[i].nextPosition();					
				}
			}	
		}
		
		return count;
	}


}
