package lbjse.trainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import common.GenericPair;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import LBJ2.classify.Classifier;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.RealFeature;
import LBJ2.classify.TestDiscrete;
import LBJ2.learn.Learner;

public class Utils {

	
	public static Set<Integer> getDocIdsWithField(List<DocumentFromTrec> docs, String field , Set<String> fieldLabels){
			
			Set<Integer> docIds = new HashSet<Integer>();
			for (DocumentFromTrec doc : docs){
				if (doc.getTagForField("other").size() > 0 )
					continue;
				if (doc.getTagForField(field).size() == 0 || ! containAtLeastOne(fieldLabels, doc.getTagForField(field) ) )
					continue;
				docIds.add(doc.getDocId());
			}
			return docIds;
		}

	/*
	 * same as getDocIdsWithField, but include 'non' docs
	 */
	public static Set<Integer> getDocIdsWithFieldValues(List<DocumentFromTrec> docs, String field , Set<String> fieldLabels){
		
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField(field).size() == 0 || ! containAtLeastOne(fieldLabels, doc.getTagForField(field) ) )
				continue;
			docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	public static Set<Integer> getOtherDocIds(List<DocumentFromTrec> docs){
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			if (doc.getTagForField("other").size() > 0 )
				docIds.add(doc.getDocId());
		}
		return docIds;
	}
	
	public static Set<Integer> getAllDocIds(List<DocumentFromTrec> docs){
		Set<Integer> docIds = new HashSet<Integer>();
		for (DocumentFromTrec doc : docs){
			docIds.add(doc.getDocId());
		}
		return docIds;
	}

	//return true if tagSet contains at least one of tags
	public static boolean containAtLeastOne(Set<String> tagSet, List<String> tags){
		for (String tag : tags) {
			if (tagSet.contains(tag))
				return true;
		}
		return false;
	}

	/**
	 * @param data
	 * @param testingBrands
	 * @param ranker
	 * @param count
	 */
	public static void evaluateClassifier(DocQueryFileParser data,
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
	
	public static double evaluateClassifier(DocQueryFileParser data,
			Classifier ranker, boolean print) {
		data.reset();
		TestDiscrete results = new TestDiscrete();
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			results.reportPrediction(ranker.discreteValue(pair) , pair.oracle() + "");			
		}
		if (print) {
			results.printPerformance(System.out);
			System.out.println("Accuracy : " + results.getOverallStats()[0] );
		}
		return results.getOverallStats()[0];
	}
	
	public static double evaluatePairwiseClassifier(DocQueryFileParser data,
			Classifier ranker, Classifier featureGen, boolean print) {
		data.reset();
		TestDiscrete results = new TestDiscrete();
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			FeatureVector features = lbjse.objectsearch.Utils.pointwiseFeatureVector(featureGen, pair,ranker);
			results.reportPrediction(ranker.discreteValue(features) , pair.oracle() + "");			
		}
		if (print) {
			results.printPerformance(System.out);
			System.out.println("Accuracy : " + results.getOverallStats()[0] );
		}
		return results.getOverallStats()[0];
	}
	
	public static double evaluatePairClassifier(List<GenericPair<DocQueryPair, DocQueryPair>> data, Classifier ranker, boolean print) {
		TestDiscrete results = new TestDiscrete();
		for ( GenericPair<DocQueryPair, DocQueryPair> pair : data){
			results.reportPrediction(ranker.discreteValue(pair) , lbjse.objectsearch.Utils.oracle(pair) + "");			
		}
		if (print) {
			results.printPerformance(System.out);
			System.out.println("Accuracy : " + results.getOverallStats()[0] );
		}
		return results.getOverallStats()[0];
	}
	
	public static double evaluateClassifierSkipNegative(DocQueryFileParser data,
			Classifier ranker, boolean print, double negativeProb) {
		data.reset();
		TestDiscrete results = new TestDiscrete();
		DocQueryPairFromFile pair = null;
		Random rand = new Random();
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			if (!pair.oracle() && rand.nextDouble() > negativeProb)
				continue;
			results.reportPrediction(ranker.discreteValue(pair) , pair.oracle() + "");			
		}
		if (print) {
			results.printPerformance(System.out);
			System.out.println("Accuracy : " + results.getOverallStats()[0] );
		}
		return results.getOverallStats()[0];
	}
	
	public static double evaluateClassifier(List<? extends DocQueryPair> data,
			Classifier ranker, boolean print) {
		TestDiscrete results = new TestDiscrete();
		for (DocQueryPair pair :  data){
			results.reportPrediction(ranker.discreteValue(pair) , pair.oracle() + "");			
		}
		if (print) {
			results.printPerformance(System.out);
			System.out.println("Accuracy : " + results.getOverallStats()[0] );
		}
		return results.getOverallStats()[0];
	}

	static public List<DocumentFromTrec> getSubset(List<DocumentFromTrec> docs, Set<Integer> docIds){
		List<DocumentFromTrec> result = new ArrayList<DocumentFromTrec>();
		for (DocumentFromTrec doc : docs) {
			if (docIds.contains(doc.getDocId()))
				result.add(doc);
		}
		return result;
	}

	public static void learnAndTest(Learner ranker, int nRounds, DocQueryFileParser trainingData, DocQueryFileParser testingData ){
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			trainingData.reset();
			count = 0;
			DocQueryPairFromFile pair = null;
			while ( (pair = (DocQueryPairFromFile) trainingData.next()) != null){
				count += 1;
				ranker.learn(pair);
			}
			System.out.print(".");
			if ( (i + 1) % 10 == 0) {
				System.out.println(" round : " + i);
				System.out.println("Progress .... ");
				System.out.println("\tTrain : " + evaluateClassifier(trainingData, ranker, false) );
				System.out.println("\tTest : " + evaluateClassifier(testingData, ranker, false) );
			}
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		
		evaluateClassifier(trainingData, ranker, true);
		evaluateClassifier(testingData, ranker, true);
	}
	
	public static void learnAndTest(Learner ranker, int nRounds, List<DocQueryPairFromFile> trainingExamples, List<DocQueryPairFromFile> testingExamples){
		int count = 0;
		for (int i = 0; i < nRounds; i++) {
			count = 0;
			for (DocQueryPairFromFile pair : trainingExamples){
				ranker.learn(pair);
			}
			System.out.print(".");
			if ( (i + 1) % 10 == 0) {
				System.out.println(" round : " + i);
				System.out.println("Progress .... ");
				System.out.println("\tTrain : " + evaluateClassifier(trainingExamples, ranker, false) );
				System.out.println("\tTest : " + evaluateClassifier(testingExamples, ranker, false) );
			}
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		
		evaluateClassifier(trainingExamples, ranker, true);
		evaluateClassifier(testingExamples, ranker, true);
	}

	static public List<String> getFieldValuesFromFile(String fileName) throws IOException{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			fileName =
			      Utils.class.getResource(fileName).getFile();
			reader = new BufferedReader(new FileReader(fileName));
		}
		
		List<String> values = new ArrayList<String>();
		while (true){
			String line = reader.readLine();
			if (line == null) break;
			values.add(line.trim());
		}
		return values;
	}

	public static FeatureVector convertListToFeatureVector(List diffFeatures, DiscreteFeature label, String containingPackage, String featureName) {
		FeatureVector result = new FeatureVector();
	    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
	    {
		      result.addFeature(lbjse.objectsearch.Utils.convertPointwiseFeatureToPairwise((RealFeature)it.next(), featureName, containingPackage));
	    }
	    result.addLabel(label);
	    return result;
	}

}
