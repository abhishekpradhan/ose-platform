package ose.testing;

import java.util.Arrays;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.DisjunctiveJoinIterator;
import ose.processor.cascader.DisjunctivePositionIterator;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.TFFeatureIterator;

public class TestTFFeatureIterator {
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "C:\\working\\annieIndex\\test_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termA = new Term("HTMLTitle","megapixel");
		Term termB = new Term("HTMLTitle","mp");
		Term termC = new Term("HTMLTitle","mpix");
		DisjunctivePositionIterator iterator1 = 
			new DisjunctivePositionIterator( Arrays.asList( new DocPositionIterator[]{
					new ConstraintTermPositionsWrapper(termA, null, reader.termPositions(termA)),
					new ConstraintTermPositionsWrapper(termB, null, reader.termPositions(termB)),
					new ConstraintTermPositionsWrapper(termC, null, reader.termPositions(termC))
			}));
		TFFeatureIterator iterator = new TFFeatureIterator(iterator1);
		int count = 0 ;
		while (iterator.skipTo(322)){
			System.out.println("- Doc : " + iterator.getDocID() + " " + iterator.getFeatures());
			count ++;
		}
		System.out.println("Done : " + count );
	}
}
