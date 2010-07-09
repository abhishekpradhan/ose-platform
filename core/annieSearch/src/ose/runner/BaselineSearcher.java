package ose.runner;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;

import ose.processor.cascader.OSHits;
import ose.query.FeatureValue;
import ose.retrieval.OSSearcher;

public class BaselineSearcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException{
		System.setProperty("gate.home", "c:/GATE-4.0");
		String INDEX_PATH = "D:\\My Documents\\PhD\\Research\\ObjectSearch\\svn\\GATE\\workspace\\data\\output\\index\\vldb";
		OSSearcher searcher = new OSSearcher(INDEX_PATH);
//		String query = "Token(canon) Token(camera) ( Number(_range(100,200)) Token(megapixel) )";
//		String query = "Token(canon,nikon) Number(_range(100,200)) Phrase(Number(_range(3,6)) Token(megapixel,mp,megapixels))";
		String query = "Phrase(Number(_range(6,8)) Token(megapixel,megapixels,mp))";
//		String query = "Number(_range(6,8)) Token(megapixel)";
		OSHits result = searcher.booleanSearch(query);
		//TODO : next : add ranking predicate 
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
/*
 * sampple query
 
%BooleanFeature(And(HTMLTitle(canon), Proximity(Number(_range(4,6)),HTMLTitle(megapixel,megapixels,mp),-3,1)))

*/