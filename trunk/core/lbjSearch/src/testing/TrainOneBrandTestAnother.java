package testing;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbj.laptop.brand_ranker;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import LBJ2.classify.TestDiscrete;

public class TrainOneBrandTestAnother {

	public static void main(String[] args) throws IOException {
		DocQueryFileParser trainingData = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query_training.txt");
//		Set<String> trainingBrands = new HashSet<String>( Arrays.asList(new String [] {"canon"}));
//		Set<String> testingBrands = new HashSet<String>( Arrays.asList(new String [] {"sony"}));
		String [] brands = new String [] {"canon","nikon","sony","olympus", "kodak"};
		for (int i = 0 ; i < brands.length; i ++){
			for (int j = 0 ; j < brands.length ; j++)
				if (i != j){
					Set<String> trainingBrands = new HashSet<String>( );
					trainingBrands.add(brands[i]);
					Set<String> testingBrands = new HashSet<String>( );
					testingBrands.add(brands[j]);
					System.out.println("Training on : " + brands[i]);
					System.out.println("Testing on : " + brands[j]);
					trainAndTestBrands(trainingData, trainingBrands, testingBrands);
				}
		}
	}

	/**
	 * @param trainingData
	 * @param trainingBrands
	 * @param testingBrands
	 */
	private static void trainAndTestBrands(DocQueryFileParser trainingData,
			Set<String> trainingBrands, Set<String> testingBrands) {
		Set<Integer> trainingDocIds = getDocIdsWithBrand(trainingData.getDocs(), trainingBrands);
		Set<Integer> testingDocIds = getDocIdsWithBrand(trainingData.getDocs(), testingBrands);
		Set<Integer> evalDocIds = new HashSet<Integer>();
		for (Integer doc : testingDocIds){
			if (Math.random() > 1){  //adjust the ratio of new brand to add to training here.
				trainingDocIds.add(doc);
			}
			else{
				evalDocIds.add(doc);
			}
		}
		System.out.println("Total docs : " + trainingData.getDocs().size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + evalDocIds.size());		
		learnAndTest(1500, trainingData, trainingDocIds, evalDocIds );
	}
	
	static private Set<Integer> getDocIdsWithBrand(List<DocumentFromTrec> docs, Set<String> brands){
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				continue;
			if (doc.getTagForField("brand").size() > 0 && !brands.contains( doc.getTagForField("brand").get(0) ) )
				continue;
			docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	static private void learnAndTest(int nRounds, DocQueryFileParser data, Set<Integer> trainingDocIds, Set<Integer> testingDocIds){
		lbj.laptop.brand_ranker ranker = new brand_ranker();
		ranker.forget();
		brand_ranker.isTraining = true;
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			data.reset();
			count = 0;
			DocQueryPairFromFile pair = null;
			while ( (pair = (DocQueryPairFromFile) data.next()) != null){
				DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
				if (!trainingDocIds.contains(trec.getDocId()))
					continue;
				count += 1;
				
				ranker.learn(pair);
			}
		}
		System.out.println("Total " + count + " examples trained.");
		brand_ranker.isTraining = false;
		evaluateTestingBrands(data, trainingDocIds, ranker);
		evaluateTestingBrands(data, testingDocIds, ranker);
		
//		ranker.write(System.out);
	}

	/**
	 * @param data
	 * @param testingBrands
	 * @param ranker
	 * @param count
	 */
	private static void evaluateTestingBrands(DocQueryFileParser data,
			Set<Integer> testingDocIds, brand_ranker ranker) {
		data.reset();
		TestDiscrete results = new TestDiscrete();
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
			if (!testingDocIds.contains(trec.getDocId()))
				continue;
			results.reportPrediction(ranker.discreteValue(pair) , pair.oracle() + "");
		}
//		results.printPerformance(System.out);
		System.out.println("Accuracy : " + results.getOverallStats()[0] );
	}
	
	
}
