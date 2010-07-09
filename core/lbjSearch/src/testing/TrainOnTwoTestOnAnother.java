package testing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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

public class TrainOnTwoTestOnAnother {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		DocQueryFileParser trainingData = new DocQueryFileParser("annotated_docs_index_30000_domain_2.trec","C:\\working\\query.txt");
		
//		Set<String> trainingBrands = new HashSet<String>( Arrays.asList(new String [] {"canon"}));
//		Set<String> testingBrands = new HashSet<String>( Arrays.asList(new String [] {"sony"}));
		String [] trainingDepts = new String [] {"computer", "mathematics",  };
		String [] testingDepts = new String [] {"physics"};
		trainAndTestBrands(trainingData, trainingDepts, testingDepts);
		
//		String [] depts = new String [] {"mathematics","computer", "physics" };
//		learnBoth(trainingData, depts);
		
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}

	private static void learnBoth(DocQueryFileParser dataParser, String[] trainingDepts) {
		Set<String> partitions = new HashSet<String>( Arrays.asList(trainingDepts));
		Set<Integer> allDocIds = getDocIdsWithBrand(dataParser.getDocs(),"dept",  partitions);
		allDocIds.addAll( getOtherDocIds(dataParser.getDocs()) );
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
		System.out.println("Total docs : " + dataParser.getDocs().size());
		System.out.println("Num training docs : " + trainingDocIds.size());
		System.out.println("Num testing docs : " + testingDocIds.size());		
		learnAndTest(20, dataParser, trainingDocIds, testingDocIds );
	}


	private static void trainAndTestBrands(DocQueryFileParser trainingData,
			String [] trainingSet,  String [] testingSet) {
		trainAndTestBrands(trainingData, new HashSet<String>(Arrays.asList(trainingSet)), 
				new HashSet<String>(Arrays.asList(testingSet)));
	}
	
	private static void trainAndTestBrands(DocQueryFileParser trainingData,
			Set<String> trainingBrands, Set<String> testingBrands) {
		Set<Integer> trainingDocIds = getDocIdsWithBrand(trainingData.getDocs(), "dept", trainingBrands);
		Set<Integer> testingDocIds = getDocIdsWithBrand(trainingData.getDocs(), "dept", testingBrands);
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
		learnAndTest(100, trainingData, trainingDocIds, evalDocIds );
	}
	
	static private Set<Integer> getDocIdsWithBrand(List<DocumentFromTrec> docs, String field , Set<String> fieldLabels){
		
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				continue;
//			System.out.println("----" + doc.getTagForField("dept"));
			if (doc.getTagForField(field).size() == 0 || ! containAtLeastOne(fieldLabels, doc.getTagForField(field) ) )
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
		ranker.forget();
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
