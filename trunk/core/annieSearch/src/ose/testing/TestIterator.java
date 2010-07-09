/**
 * 
 */
package ose.testing;

import java.util.Arrays;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.DisjunctiveJoinIterator;
import ose.processor.cascader.DocPositionIterator;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestIterator {
	public static void main(String[] args) throws Exception{
		System.setProperty("gate.home", "c:/GATE-4.0");
		String INDEX_PATH = "C:\\working\\annieSearch\\index\\amagoonew_lite";
		IndexReader reader = IndexReader.open(INDEX_PATH);
//		Term term = new Term("Token", "megapixel");
//		DisjunctiveJoinIterator iter = new DisjunctiveJoinIterator(Arrays.asList( new DocPositionIterator[]{
//			new ConstraintTermPositionsWrapper(null, reader.termPositions(new Term("Token", "megapixel")))
//			}
//		));
		ConstraintTermPositionsWrapper iter = new ConstraintTermPositionsWrapper(null, null, 
				reader.termPositions(new Term("HTMLTitle", "nikon")));
		iter.skipTo(6788);
		do {
			System.out.println(" - doc " + iter.getDocID() + " : " + iter.getFrequency());			
		} while (iter.next());
	}
}
