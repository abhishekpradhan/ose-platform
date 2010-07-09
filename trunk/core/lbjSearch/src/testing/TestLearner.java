package testing;

import lbj.laptop.other_ranker;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairParser;
import LBJ2.classify.Classifier;

public class TestLearner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		testFieldRanker(new brand_ranker(), new DocQueryPairParser("testing_brand.xml"));
//		testFieldRanker(new model_ranker(), new DocQueryPairParser("testing_model.xml"));
//		testFieldRanker(new mpix_ranker(), new DocQueryPairParser("testing_mpix.xml"));
//		testFieldRanker(new zoom_ranker(), new DocQueryPairParser("testing_zoom.xml"));
//		testFieldRanker(new price_ranker(), new DocQueryPairParser("testing_price.xml"));
//		testFieldRanker(new other_ranker(), new DocQueryPairParser("training_other.xml"));
		testFieldRanker(new other_ranker(), new DocQueryPairParser("camera.xml"));
		
	}

	/**
	 * @param ranker
	 * @param parser
	 */
	private static void testFieldRanker(Classifier ranker,
			DocQueryPairParser parser) {
		int count = 0;
		int missed = 0;
		while (true){
			DocQueryPair pair = (DocQueryPair) parser.next();
			if (pair == null) break;
			System.out.println("\t" + ranker.discreteValue(pair) + "\t" + pair.oracle());
			if (!ranker.discreteValue(pair).equals( "" + pair.oracle() ) ){
				missed += 1;
			}
			count += 1;
		}
		System.out.println("Result for " + parser.getSourceName());
		System.out.println("Total " + count + " examples. ");
		System.out.println("Missed " + missed + " examples. ");
		System.out.println("Accuracy " + (1 - missed * 1.0 / count) * 100+ " % ");
	}

}
