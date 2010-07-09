package testing;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import LBJ2.parse.LinkedVector;

import lbjse.objectsearch.DocAnnotations;
import lbjse.objectsearch.DocumentFromDatabase;
import lbjse.objectsearch.PositionWordPair;
import lbjse.objectsearch.Utils;

public class TestSurroundingWords {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DocAnnotations annotMan = new DocAnnotations(1,8);
		DocumentFromDatabase doc = new DocumentFromDatabase(1,annotMan);
		String bodyText = doc.getBody();
		System.out.println(bodyText);
		LinkedVector features = Utils.getSurroundingFeatures(bodyText, "_range(0,100000)",-2,2);
		for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next){
			System.out.println("Feature : " + pair);
		}
	}

}
