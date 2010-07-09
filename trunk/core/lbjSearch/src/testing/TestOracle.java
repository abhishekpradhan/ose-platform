package testing;

import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairParser;
import lbjse.objectsearch.Utils;


public class TestOracle {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		DocQueryPairParser parser = new DocQueryPairParser("training_model.xml");
		int count = 0;
		while (true) {
			DocQueryPair p = (DocQueryPair) parser.next();
			if (p == null ) break;
			count += 1;
			System.out.println("Ex " + p + " : " + p.oracle());
			System.out.println("\t" + Utils.contains(p.getDoc().getTitle(), p.getQuery().getFieldValue("model")));
		}
		System.out.println("Total " + count + " examples. ");
		
	}

}
