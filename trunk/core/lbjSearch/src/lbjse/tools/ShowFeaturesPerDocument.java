package lbjse.tools;

import java.util.HashSet;
import java.util.Set;

import common.CommandLineOption;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbj.common.WordsInBody;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

public class ShowFeaturesPerDocument {

	public void showFeatures(DocQueryPair example) {
		WordsInBody featureGenerator = new WordsInBody();
		FeatureVector fv = featureGenerator.classify(example);
		for (Object obj : fv.features){
			DiscreteFeature feature = (DiscreteFeature) obj; 
//			System.out.println("\t" + feature.getStringValue() + "\t" + feature);
			System.out.println(feature.getStringValue() );
		}
	}
	
	public void showFeaturesOnce(DocQueryPair example) {
		WordsInBody featureGenerator = new WordsInBody();
		FeatureVector fv = featureGenerator.classify(example);
		Set<String> seen = new HashSet<String>();
		for (Object obj : fv.features){
			DiscreteFeature feature = (DiscreteFeature) obj; 
//			System.out.println("\t" + feature.getStringValue() + "\t" + feature);
			String w = feature.getStringValue();
			if ( !seen.contains(w) ){
				System.out.println(w );
				seen.add(w);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("");
		String trecFile = options.getString("trecFile");
		String queryFile = options.getString("queryFile");
		DocQueryFileParser parser = null;
		if (trecFile == null || queryFile == null)
			parser = new DocQueryFileParser("annotated_docs_index_30000_domain_2.trec","query.txt");
		else
			parser = new DocQueryFileParser(trecFile, queryFile);
//		test(parser);
		showFeaturesOnAllDocs(parser);
//		showFeatureOnDepartment(parser, "mathematics");
//		showFeatureOnDepartment(parser, "physics");
	}

	/**
	 * @param parser
	 */
	private static void test(DocQueryFileParser parser) {
		ShowFeaturesPerDocument featureGenerator = new ShowFeaturesPerDocument();
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			featureGenerator.showFeatures(example);
			break;
		}
	}
	
	private static void showFeatureOnDepartment(DocQueryFileParser parser, String dept) {
		ShowFeaturesPerDocument featureGenerator = new ShowFeaturesPerDocument();
//		Query deptQuery = new Query(parser.getObjectInfo(), "{'dept':'" + dept + "'"); 
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			if (example == null) break;
			DocumentFromTrec doc = (DocumentFromTrec) example.getDoc(); 
//			System.out.println(Arrays.asList(doc.getTags()));
			example.getQuery().setFieldValue("dept", dept);
			if (example.oracle()){
				featureGenerator.showFeatures(example);
//				break;
			}
		}
	}
	
	private static void showFeaturesOnAllDocs(DocQueryFileParser parser) {
		ShowFeaturesPerDocument featureGenerator = new ShowFeaturesPerDocument();
		while (true){
			DocQueryPair example = (DocQueryPair) parser.next();
			if (example == null) break;
			DocumentFromTrec doc = (DocumentFromTrec) example.getDoc(); 
//			System.out.println(Arrays.asList(doc.getTags()));
			if (example.oracle()){
				featureGenerator.showFeaturesOnce(example);
//				break;
			}
		}
	}

}
