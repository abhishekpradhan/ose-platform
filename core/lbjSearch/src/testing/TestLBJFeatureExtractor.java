package testing;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbj.common.WordsInBody;

public class TestLBJFeatureExtractor {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WordsInBody feature = new WordsInBody();
//		WordsInTitle feature = new WordsInTitle();
		DocQueryFileParser data = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query_training.txt");
		DocQueryPairFromFile pair;
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
			if (trec.getTagForField("other").size() > 0)
				continue;
			
			System.out.println(" -- "  + "  ,  " + pair.oracle());
//			System.out.println(" -- " + feature.classify(pair).toString().substring(0,100) + "  ,  " + pair.oracle());
		}
	}

}
