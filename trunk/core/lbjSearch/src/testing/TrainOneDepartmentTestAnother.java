package testing;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ose.query.OQuery;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbj.professor.*;
import LBJ2.classify.Classifier;
import LBJ2.classify.TestDiscrete;

public class TrainOneDepartmentTestAnother {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		DocQueryFileParser trainingData = new DocQueryFileParser("C:\\working\\annotated_docs_index_30000_domain_2.trec","C:\\working\\query.txt");
		
//		Set<String> trainingBrands = new HashSet<String>( Arrays.asList(new String [] {"canon"}));
//		Set<String> testingBrands = new HashSet<String>( Arrays.asList(new String [] {"sony"}));
		String [] depts = new String [] {"mathematics","computer", };
		trainOneTestAnother(trainingData, depts);
//		learnPartitions(trainingData, depts);
		
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @param startTime
	 * @param trainingData
	 * @param depts
	 */
	private static void trainOneTestAnother(DocQueryFileParser trainingData, String[] depts) {
		for (int i = 0 ; i < depts.length; i ++){
			for (int j = 0 ; j < depts.length ; j++)
				if (i != j){
					Set<String> trainingBrands = new HashSet<String>( );
					trainingBrands.add(depts[i]);
					Set<String> testingBrands = new HashSet<String>( );
					testingBrands.add(depts[j]);
					System.out.println("Training on : " + depts[i]);
					System.out.println("Testing on : " + depts[j]);
					trainAndTestBrands(trainingData, trainingBrands, testingBrands);
					break;
				}
			break;
		}
		
	}
	
	private static void learnPartitions(DocQueryFileParser trainingData, String[] depts) {
		Set<String> partitions = new HashSet<String>( Arrays.asList(depts));
		Set<Integer> allDocIds = getDocIdsWithBrand(trainingData.getDocs(), partitions);
		allDocIds.addAll( getOtherDocIds(trainingData.getDocs()) );
		Set<Integer> trainingDocIds = new HashSet<Integer>();
		Set<Integer> testingDocIds = new HashSet<Integer>();
		for (Integer docId : allDocIds) {
			if (Math.random() < 1.0/5){
				testingDocIds.add(docId);
			}
			else{
				trainingDocIds.add(docId);
			}
		}
		System.out.println("Total docs : " + trainingData.getDocs().size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + testingDocIds.size());		
		learnAndTest(0, trainingData, trainingDocIds, testingDocIds );
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
		Set<Integer> otherDocIds =  getOtherDocIds(trainingData.getDocs());
		System.out.println("Got training docIds " + trainingDocIds.size());
		System.out.println("Got testing docIds " + testingDocIds.size());
		System.out.println("Got other docIds " + otherDocIds.size());
		trainingDocIds.addAll(otherDocIds);
		Set<Integer> evalDocIds = new HashSet<Integer>();
		evalDocIds.addAll(testingDocIds);
//		for (Integer doc : testingDocIds){
//			if (Math.random() > 1){  //adjust the ratio of new brand to add to training here.
//				trainingDocIds.add(doc);
//			}
//			else{
//				evalDocIds.add(doc);
//			}
//		}
		System.out.println("Total docs : " + trainingData.getDocs().size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + evalDocIds.size());		
		learnAndTest(20, trainingData, trainingDocIds, evalDocIds );
	}
	
	static private Set<Integer> getDocIdsWithBrand(List<DocumentFromTrec> docs, Set<String> brands){
		
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				continue;
//			System.out.println("----" + doc.getTagForField("dept"));
			if (doc.getTagForField("dept").size() == 0 || ! containAtLeastOne(brands, doc.getTagForField("dept") ) )
				continue;
			docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	//return true if tagSet contains at least one of tags
	private static boolean containAtLeastOne(Set<String> tagSet, List<String> tags){
		for (String tag : tags) {
			if (tagSet.contains(tag))
				return true;
		}
		return false;
	}
	
	static private Set<Integer> getOtherDocIds(List<DocumentFromTrec> docs){
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	static private void learnAndTest(int nRounds, DocQueryFileParser data, Set<Integer> trainingDocIds, Set<Integer> testingDocIds){
		other_ranker ranker = new other_ranker ();
//		ranker.forget();
		ranker.isTraining = true;
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
			System.out.print(".");
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		ranker.isTraining = false;
		ranker.save();
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
			Set<Integer> testingDocIds, Classifier ranker) {
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
		System.out.println("Accuracy : " + results.getOverallStats()[0] );
	}
	
	
}
