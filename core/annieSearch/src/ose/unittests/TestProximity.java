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
public class TestProximity {
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "C:\\working\\annieIndex\\laptops_testing_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		
		FeatureQuery query = parser.parseFeatureQuery("Proximity(Number_body(_range(16,22)),Token(widescreen,wxga),-5,0)");
		DocPositionIterator iterator = query.getDocPositionIterator(reader);
		int count = 0 ;
		int MAX_COUNT = 10;
		while (iterator.skipTo(2195) && count <= MAX_COUNT){
			System.out.println(iterator.getDocID());
			if (iterator.getDocID() <= 2255){
				System.out.println(" - doc " + iterator.getDocID() + " : " + iterator.getFrequency());
				do {
					System.out.print(iterator.getPosition() + " - " + iterator.getClue() + "\t");
					
				} while (iterator.nextPosition());
				System.out.println();
				count += 1;
			}
			if (iterator.getDocID() > 2255) break;
		} 
		System.out.println("Did you see 3 features in 2255?");
		reader.close();
	}
}
