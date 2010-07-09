package lbjse.tools;

import common.CommandLineOption;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbj.common.WordsInBody;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

public class ShowTokenizedTerms {

	public void showFeatures(DocQueryPair example) {
		WordsInBody featureGenerator = new WordsInBody();
		FeatureVector fv = featureGenerator.classify(example);
		for (Object obj : fv.features){
			DiscreteFeature feature = (DiscreteFeature) obj; 
//			System.out.println("\t" + feature.getStringValue() + "\t" + feature);
			System.out.println(feature.getStringValue() );
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"opt","trec"});
		String trecFile = options.getString("trec");
		DocQueryFileParser parser = new DocQueryFileParser(trecFile,"query.txt");
		if ("doc".equals(options.getString("opt"))){
			int docId = options.getInt("docId");
			showFeatureOnDocId(parser, docId);
		}
		else if ("slice".equals(options.getString("opt"))){
			String fieldName = options.getString("field");
			String [] values = options.getStrings("values");
			for (String val : values){
				showFeatureOnFieldValue(parser, fieldName, val);
			}
		}
//		test(parser);
		
	}

	/**
	 * @param parser
	 */
	private static void test(DocQueryFileParser parser) {
		ShowTokenizedTerms featureGenerator = new ShowTokenizedTerms();
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			featureGenerator.showFeatures(example);
			break;
		}
	}
	
	private static void showFeatureOnFieldValue(DocQueryFileParser parser, String fieldName, String fieldValue) {
		ShowTokenizedTerms featureGenerator = new ShowTokenizedTerms();
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			if (example == null) break;
			DocumentFromTrec doc = (DocumentFromTrec) example.getDoc(); 
			example.getQuery().setFieldValue(fieldName, fieldValue);
			if (example.oracle()){
				featureGenerator.showFeatures(example);
//				break;
			}
		}
	}
	
	private static void showFeatureOnDocId(DocQueryFileParser parser, int docId) {
		ShowTokenizedTerms featureGenerator = new ShowTokenizedTerms();
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			if (example == null) break;
			if (docId == example.getDoc().getDocId())
				featureGenerator.showFeatures(example);
		}
	}

}
