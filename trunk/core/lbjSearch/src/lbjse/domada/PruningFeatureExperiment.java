package lbjse.domada;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ose.query.OQuery;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.WordDict;
import lbj.professor.*;
import LBJ2.classify.Classifier;
import LBJ2.classify.TestDiscrete;

/*
 * This tries to do the following
 *    1. get the words used in title & body features
 *    2. sort them by frequency. 
 *    3. for i in (i1,i2,i3,i4...)
 *    	prune all words with frequency less than or equal to i
 *          (prune features set on those contained in this set of words)
 *      put math docs into training set
 *      put physics docs into testing_set1
 *      put 4/5 & 1/5 computer docs into training set:testing_set2
 *      train and test on the two testing sets. 
 */ 
public class PruningFeatureExperiment {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		DocQueryFileParser trainingData = new DocQueryFileParser("annotated_docs_index_30000_domain_2.trec","query.txt");
		
		Set<Integer> trainingDocIds = getDocIdsWithField(trainingData.getDocs(), "dept", new String[]{"mathematics", "computer"});
		trainingDocIds.addAll(getOtherDocIds(trainingData.getDocs()));
		
		Set<Integer> testingDocIds1 = getDocIdsWithField(trainingData.getDocs(), "dept", new String[]{"physics"});
		 
		
		System.out.println("Got training docIds " + trainingDocIds.size());
		System.out.println("Got testing docIds " + testingDocIds1.size());
		Integer [] allTrainingDocIds = trainingDocIds.toArray(new Integer[]{});
		int [] partitions = new int[allTrainingDocIds.length];
		int NPART = 5;
		
		for (int i = 0; i < partitions.length; i++) {
			partitions[i] = new Random().nextInt(NPART);
		}
		WordDict.contains("");
		System.out.println("+\t" + "training" + "\t" + "testing1" + "\t" + "testing2" + "\t" + "accuracy1" + "\t" + "accuracy2");
		for (int i = 0; i < NPART; i++) {
			System.out.print("Partition " + i + "\t");
			trainingDocIds.clear();
			Set<Integer> testingDocIds2 = new HashSet<Integer>();
			for (int j = 0; j < partitions.length; j++) {
				if (partitions[j] != i)
					trainingDocIds.add(allTrainingDocIds[j]);
				else
					testingDocIds2.add(allTrainingDocIds[j]);
			}
			
			Classifier ranker = learn(100, trainingData, trainingDocIds);
			double acc1 = evaluateTestingBrands(trainingData, testingDocIds1, ranker);
			double acc2 = evaluateTestingBrands(trainingData, testingDocIds2, ranker);
			System.out.println(trainingDocIds.size() + "\t" + testingDocIds1.size() + "\t" + testingDocIds2.size() + "\t" + acc1 + "\t" + acc2 );
		}
		
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}

	static private Set<Integer> getDocIdsWithField(List<DocumentFromTrec> docs, String field , String [] fieldLabels){
		Set<String> fieldLabelSet = new HashSet<String>(Arrays.asList(fieldLabels));
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				continue;
//			System.out.println("----" + doc.getTagForField("dept"));
			if (doc.getTagForField(field).size() == 0 || ! containAtLeastOne(fieldLabelSet, doc.getTagForField(field) ) )
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
	
	static private Classifier learn(int nRounds, DocQueryFileParser data, Set<Integer> trainingDocIds){
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
//			System.out.print(".");
		}
//		System.out.println();
//		System.out.println("Total " + count + " examples trained.");
		ranker.isTraining = false;
		ranker.save();
//		ranker.write(System.out);
		return ranker;
	}

	/**
	 * @param data
	 * @param testingBrands
	 * @param ranker
	 * @param count
	 */
	private static double evaluateTestingBrands(DocQueryFileParser data,
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
//		results.printPerformance(System.out);
//		System.out.println("Accuracy : " + results.getOverallStats()[0] );
		return results.getOverallStats()[0];
	}
	
	
}
