/**
 * 
 */
package ose.testing;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;

import ose.parser.OSQueryParser;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.FeatureQuery;

/**
 * @author Pham Kim Cuong
 *
 */
public class ShowIndex {
	public static void main(String[] args) throws Exception{
		System.setProperty("gate.home", "c:/GATE-4.0");
//		String INDEX_PATH = "C:\\working\\annieIndex\\test_index_1";
		String INDEX_PATH = "C:\\working\\annieIndex\\test_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		TermEnum terms = reader.terms();
		while (terms.next()){
			Term term = terms.term();
			TermPositions termPos = reader.termPositions(term);
			System.out.println("Term : " + term);
			while (termPos.next()){
				System.out.println("---" + termPos.doc() + " - " + termPos.freq());
				for (int i = 0; i < termPos.freq(); i++) {
					System.out.println("\t" + termPos.nextPosition());
				}
			}
		}
		reader.close();
	}
}
