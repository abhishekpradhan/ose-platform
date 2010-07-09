/**
 * 
 */
package ose.testing;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.BooleanQuery;
import ose.processor.cascader.DocPositionIterator;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestProximityPredicate {

	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "D:\\My Documents\\PhD\\Research\\ObjectSearch\\svn\\GATE\\workspace\\data\\output\\index\\mp0_4";
//		String query = "Proximity(Token(image),Token(processor),-1,1)";
//		String query = "Proximity(Number(_range(0,10000)),Token(megapixel,megapixels),-4,0)";
//		String query = "Proximity(Number(_range(neg_infinity,infinity)),Token(megapixel,megapixels),-7,0)";
//		String query = "Proximity(Number(_range(neg_infinity,infinity)),Token(gb,mb,kb,megabyte,megabytes,gigabyte,gigabytes,kilobyte,kilobytes),-4,0)";
		String query = "Proximity(Number(),Token(megapixel,megapixels,mp),-3,3)";
		
//		String query = "Number(_range(8,8))";
//		String query = "Token(megapixel)";
//		String query = "%CountNumber(Phrase(Token(price) Number(_range(0,300) ) ) )";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		BooleanQuery osQuery = parser.parseBooleanQuery(query);
		DocPositionIterator iterator = (DocPositionIterator ) osQuery.getPredicates().get(0).getInvertedListIterator(reader);
		int count = 0;
		int maxCountPos = 0;
		int maxDoc = 0;		
		while (iterator.skipTo(1)){
			int countPos = 0;
			System.out.println("Doc : " + iterator.getDocID());
			do {
				System.out.print("\t" + iterator.getPosition() + " [ " + iterator.getClue() + " ] ");
				countPos ++;
			} while (iterator.nextPosition());
			System.out.println();
			System.out.println("\t" + countPos);
			if (maxCountPos < countPos){
				maxCountPos = countPos;
				maxDoc = iterator.getDocID();
				
			}
			count ++;
		}
		System.out.println("Number of docs : " + count);
		System.out.println("Doc with most positions " + maxDoc + " has " + maxCountPos);
		System.out.println("Done " );
	}

}
