/**
 * 
 */
package ose.runner;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

import ose.processor.cascader.OSHits;
import ose.query.FeatureValue;
import ose.retrieval.OSSearcher;

/**
 * @author Pham Kim Cuong
 *
 */
public class AnnieSearcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException{
		System.setProperty("gate.home", "c:/GATE-4.0");
		String INDEX_PATH = "D:\\My Documents\\PhD\\Research\\ObjectSearch\\svn\\GATE\\workspace\\data\\output\\index\\vldb2";
		OSSearcher searcher = new OSSearcher(INDEX_PATH);
//		String query = "Proximity(Number(_range(6,8)),Token(megapixel,megapixels,mp),-2,2) " ;
//		String query = "Or(Token(google),Token(samsung)) " ;
		String query = "Token('$')";
//		String query = "Phrase(Token(price) Number(_range(0,300) ) ) ";
//		String query = "%CountNumber(Phrase(Token(price) Number(_range(0,300) ) ) )";
		OSHits result = searcher.booleanSearch(query);
		int i = 0;
		for (Document doc : result) {
			i += 1;
			System.out.println("<p><a href='" + doc.get("DOCUMENT_ID") + "'> Doc " + i + " [" + result.score() + "] : " + doc.get("DOCUMENT_TITLE") + " </a>");		
			List<FeatureValue> features = result.docFeatures();
			for (FeatureValue feature : features) {
				System.out.println("<li>" + feature + "</li>");
			}
		}
		System.out.println("Total documents : " + i + "<br>");
		
	}

}
