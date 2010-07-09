/**
 * 
 */
package ose.testing;

import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.DocFeatureIterator;
import ose.processor.cascader.DocPositionIterator;
import ose.processor.cascader.FeatureQuery;
import ose.query.FeatureValue;

/**
 * @author Pham Kim Cuong
 *
 */
public class ShowDocFeatures {
	public static void main(String[] args) throws Exception{
		System.setProperty("gate.home", "c:/GATE-4.0");
//		String INDEX_PATH = "C:\\working\\annieIndex\\test_index_1";
		String INDEX_PATH = "C:\\working\\annieIndex\\training_index";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		
//		FeatureQuery query = parser.parseFeatureQuery("Proximity(Number(_range(4,12)),Token(megapixel,megapixels,mp),-3,1)");
//		FeatureQuery query = parser.parseFeatureQuery("Proximity(Number(),Token('$',usd,dollar,dollars),-2,1)");
		FeatureQuery query = parser.parseFeatureQuery("%DivideBy(%CountNumber(Proximity(Number_body(_range(4,10)),Token(megapixel,megapixels,mp),-3,1)),%CountNumber  (Proximity(Number_body(),Token(megapixel,megapixels,mp),-3,1))) ");
		int docId = 3281;
		List<FeatureValue> iterator = query.getFeaturesForDoc(reader, docId);
		System.out.println("Feature for " + docId);
		for (FeatureValue featureValue : iterator) {
			System.out.println("\t" + featureValue);
		}
		
		reader.close();
	}
}
