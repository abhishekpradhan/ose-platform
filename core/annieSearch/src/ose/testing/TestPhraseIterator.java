/**
 * 
 */
package ose.testing;

import org.apache.lucene.index.IndexReader;

import common.profiling.Profile;

import ose.parser.OSQueryParser;
import ose.processor.cascader.BooleanQuery;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.DisjunctiveJoinIterator;
import ose.processor.cascader.DocPositionIterator;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestPhraseIterator {

	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "C:\\tmp\\annieSearch\\index\\profs";
		INDEX_PATH = "C:\\working\\index\\redu_light_nc";
		String query = "Phrase(Token(price) Number(_range(0,300) ) ) ";
		
		query = "Number(_range(110,200))";
		query = "Token('$',usd,dollar,dollars)";
		
		query = "Proximity(Number(_range(4,6)),Token(megapixel,megapixels,mp),-3,1)"; 
		query = "Proximity(Number(_range(4,100)),Phrase(Token(optical),Token(zoom)),-4,1)";
		
			
		query = "Phrase(Token(of) Token(the)) ";
//		String query = "%CountNumber(Phrase(Token(price) Number(_range(0,300) ) ) )";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		BooleanQuery osQuery = parser.parseBooleanQuery(query);
		DocPositionIterator iterator = (DocPositionIterator ) osQuery.getPredicates().get(0).getInvertedListIterator(reader);
		int count = 0;
		int countPos = 0;
		long startTime = System.currentTimeMillis();
		
		while (iterator.next()){
//			System.out.println("Doc : " + iterator.getDocID());
			
			do {
//				System.out.print("\t" + iterator.getPosition() + " [ " + iterator.getClue() + " ] ");				
//				System.out.print("\t" + iterator.getPosition());
				countPos ++;
			} while (iterator.nextPosition());
//			System.out.println();
			count ++;
//			System.out.println("\t" + countPos);
//			break;
		}
		System.out.println("Done " + count + "  " + countPos);
		System.out.println("Time " + (System.currentTimeMillis() - startTime) );
		System.out.println("DisjunctiveJoinIterator.skipTo() : " + DisjunctiveJoinIterator.PROF_countSkipTo);
		Profile.printAll();
	}

}
