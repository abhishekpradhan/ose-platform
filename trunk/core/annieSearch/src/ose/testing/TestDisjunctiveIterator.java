package ose.testing;

import java.util.Arrays;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.index.IndexFieldConstant;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.DisjunctiveJoinIterator;
import ose.processor.cascader.DisjunctivePositionIterator;
import ose.processor.cascader.DocPositionIterator;

public class TestDisjunctiveIterator {
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "C:\\working\\annieIndex\\test_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term term = new Term(IndexFieldConstant.FIELD_ANNOTATION,IndexFieldConstant.TERM_NUMBER);
		Term termA = new Term("HTMLTitle","megapixel");
		Term termB = new Term("HTMLTitle","mp");
		Term termC = new Term("HTMLTitle","mpix");
		DisjunctivePositionIterator iterator = 
			new DisjunctivePositionIterator( Arrays.asList( new DocPositionIterator[]{
					new ConstraintTermPositionsWrapper(term, null, reader.termPositions(termA)),
					new ConstraintTermPositionsWrapper(term, null, reader.termPositions(termB)),
					new ConstraintTermPositionsWrapper(term, null, reader.termPositions(termC))
			}));
		int count = 0 ;
		while (iterator.skipTo(817)){
			System.out.println("- Doc : " + iterator.getDocID());
			do {
				System.out.print(" " + iterator.getClue() );
			} while (iterator.nextPosition());
			System.out.println();
			count ++;
		}
		System.out.println("Done : " + count );
	}
}
