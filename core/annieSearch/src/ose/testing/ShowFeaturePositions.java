/**
 * 
 */
package ose.testing;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.FeatureQuery;

/**
 * @author Pham Kim Cuong
 *
 */
public class ShowFeaturePositions {
	public static void main(String[] args) throws Exception{
//		String INDEX_PATH = "C:\\working\\annieIndex\\test_index";
		String INDEX_PATH = "C:\\working\\annieIndex\\laptops_testing_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
//		%BooleanFeature(Phrase(Top(Token('$'),3),Number_body(PRICE)))
		FeatureQuery query = parser.parseFeatureQuery("Phrase(Top(Token('$'),3),Number_body(_range(1700,2000)))");
//		FeatureQuery query = parser.parseFeatureQuery("Token(price)");
//		FeatureQuery query = parser.parseFeatureQuery("Token('$')");
//		FeatureQuery query = parser.parseFeatureQuery("Number_body(_range(1679.05,2945.8))");
		DocPositionIterator iterator = query.getDocPositionIterator(reader);
		int count = 0 ;
		int MAX_COUNT = 3;
		while (iterator.skipTo(3750) && count <= MAX_COUNT){
			System.out.println(" - doc " + iterator.getDocID() + " : " + iterator.getFrequency());
			do {
				System.out.print(iterator.getPosition() + " - " + iterator.getClue() + "\t");
				
			} while (iterator.nextPosition());
			System.out.println();
			count += 1;
		} 
		
		reader.close();
	}
}
