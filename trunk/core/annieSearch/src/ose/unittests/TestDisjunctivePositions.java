/**
 * 
 */
package ose.unittests;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.FeatureQuery;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestDisjunctivePositions {
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "C:\\working\\annieIndex\\laptops_testing_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		
		FeatureQuery query = parser.parseFeatureQuery("Token(widescreen,wxga)");
		DocPositionIterator iterator = query.getDocPositionIterator(reader);
		int count = 0 ;
		int MAX_COUNT = 2;
		iterator.skipTo(2252);
		
		while (iterator.next() && count < MAX_COUNT){
			System.out.println(" - doc " + iterator.getDocID() + " : " + iterator.getFrequency());
			do {
				System.out.print(iterator.getPosition() + " - " + iterator.getClue() + "\t");
				
			} while (iterator.nextPosition());
			System.out.println();
			count += 1;
		} 
		reader.close();
		System.out.println("Did you see 7 positions?");
	}
}
