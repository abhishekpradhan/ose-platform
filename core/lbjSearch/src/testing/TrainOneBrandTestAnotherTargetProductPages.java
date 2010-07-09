package testing;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbj.laptop.brand_ranker;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import LBJ2.classify.TestDiscrete;

public class TrainOneBrandTestAnotherTargetProductPages {

	public static void main(String[] args) throws IOException {
		DocQueryFileParser trainingData = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query_training.txt");
		DocQueryFileParser testingData = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query_testing.txt");
		
		Set<String> trainingBrands = new HashSet<String>( Arrays.asList(new String [] {"sony","nikon"}));
		Set<String> testingBrands = new HashSet<String>( Arrays.asList(new String [] {"canon"}));
		
		for (int i = 0 ; i < 2 ; i++){
			adaptationCurve(1000, trainingData,testingData , i * 0.5, trainingBrands, testingBrands);
		}
	}

	/**
	 * @param data
	 * @param threshold
	 * @param trainingBrands
	 * @param testingBrands
	 */
	private static void adaptationCurve(int nRounds , DocQueryFileParser data, DocQueryFileParser testingData,
			double threshold, Set<String> trainingBrands,
			Set<String> testingBrands) {
		System.out.println("Result for threshold: " + threshold);
		Set<Integer> trainingDocIds = getDocIdsWithBrand(data.getDocs(), trainingBrands);
		trainingDocIds.addAll(getNonDomainDocIds(data.getDocs()));
		Set<Integer> testingDocIds = getDocIdsWithBrand(data.getDocs(), testingBrands);
		Set<Integer> evalDocIds = new HashSet<Integer>();
		for (Integer doc : testingDocIds){
			if (Math.random() > 1 - threshold){  //adjust the ratio of new brand to add to training here.
				trainingDocIds.add(doc);
			}
			else{
				evalDocIds.add(doc);
			}
		}
		
		System.out.println("Total docs : " + data.getDocs().size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + evalDocIds.size());	
		brand_ranker ranker = new brand_ranker();
		
		
		learn(ranker, nRounds, data, trainingDocIds);
		evaluate(ranker, data, trainingDocIds);
		evaluate(ranker, testingData, evalDocIds);
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
	
	static private Set<Integer> getNonDomainDocIds(List<DocumentFromTrec> docs){
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	static private void learn(brand_ranker ranker, int nRounds, DocQueryFileParser data, Set<Integer> docIds){
		ranker.forget();
		brand_ranker.isTraining = true;
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			data.reset();
			count = 0;
			DocQueryPairFromFile pair = null;
			while ( (pair = (DocQueryPairFromFile) data.next()) != null){
				DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
				if (!docIds.contains(trec.getDocId()))
					continue;
				count += 1;
				
				ranker.learn(pair);
			}
		}
		System.out.println("Total " + count + " examples trained.");
		brand_ranker.isTraining = false;
	}

	/**
	 * @param data
	 * @param testingBrands
	 * @param ranker
	 * @param count
	 */
	private static void evaluate(brand_ranker ranker,DocQueryFileParser data,
			Set<Integer> testingDocIds) {
		data.reset();
		TestDiscrete results = new TestDiscrete();
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
			if (!testingDocIds.contains(trec.getDocId()))
				continue;
			results.reportPrediction(ranker.discreteValue(pair) , pair.oracle() + "");
		}
		results.printPerformance(System.out);
	}
	
	
}
