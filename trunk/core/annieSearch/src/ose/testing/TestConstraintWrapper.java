package ose.testing;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.index.IndexFieldConstant;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.RangeConstraint;

public class TestConstraintWrapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "D:\\My Documents\\PhD\\Research\\ObjectSearch\\svn\\GATE\\workspace\\data\\output\\index\\vldb";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term term = new Term(IndexFieldConstant.FIELD_ANNOTATION,IndexFieldConstant.TERM_NUMBER);
		ConstraintTermPositionsWrapper iterator = 
			new ConstraintTermPositionsWrapper(term,
					new RangeConstraint(100,110), 
					reader.termPositions(term));
		while (iterator.next()){
			System.out.println("- Doc : " + iterator.getDocID());
			do {
				System.out.print(" " + iterator.getClue() );
			} while (iterator.nextPosition());
			System.out.println();
		}
		System.out.println("Done");
	}

}
