package lbjse.trainer.prof_inde;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.learning.professor.inde.name_ranker;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.Query;
import lbjse.trainer.Utils;

public class TrainProfessorName {

	
	private int domainId;
	
	public TrainProfessorName(int domainId) {
		this.domainId = domainId;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int domainId = 2;
		TrainProfessorName main = new TrainProfessorName(domainId);
		main.run();
	}

	/**
	 * 
	 */
	private void run() {
		long startTime = System.currentTimeMillis();
		DocQueryFileParser trainingData = new DocQueryFileParser("prof_learning3.trec","query.txt");
		
		List<String> uniqNameQueries = getUniqueQueryForField("name", trainingData.getDocs());
		for (String q : uniqNameQueries) {
			System.out.println("---" + q);
		}
		System.out.println("Number of uniq querries : " + uniqNameQueries.size());
		List<DocQueryPairFromFile> examples = generateLearningExamples(uniqNameQueries, trainingData.getDocs());
		train(examples);
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}
	
	static private List<String> getUniqueQueryForField(String fieldName, List<DocumentFromTrec> docs){
		Set<String> queries = new HashSet<String>();
		for (DocumentFromTrec doc : docs) {
			if (doc.getTagForField(fieldName).size() != 0){
				queries.add(join(doc.getTagForField(fieldName), " "));
			}
		}
		return new ArrayList<String>(queries);
	}
	
	public static String join(Collection<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
	private static final int NUM_PSEUDO_RELEVANT = 1;
	private static final int NUM_NON_RELEVANT = 1;
	
	private List<DocQueryPairFromFile> generateLearningExamples(List<String> nameQuerries, List<DocumentFromTrec> docs){
		List<DocQueryPairFromFile> examples = new ArrayList<DocQueryPairFromFile>();
		
		/* separete the non-relevant from pseudo relevant */ 
		List<DocumentFromTrec> pseudoReleventDocs = new ArrayList<DocumentFromTrec>();
		List<DocumentFromTrec> nonReleventDocs = new ArrayList<DocumentFromTrec>();
		for (DocumentFromTrec doc : docs) {
			if (doc.getTagForField("other").size() > 0){
				nonReleventDocs.add(doc);
			}
			else{
				pseudoReleventDocs.add(doc);
			}
		}
		System.out.println("Number of pseudo docs : " + pseudoReleventDocs.size());
		System.out.println("Number of non relevant docs : " + nonReleventDocs.size());
		/* create examples for each query */
		for (String nameQuery : nameQuerries) {
			Query query = new Query(domainId);
			query.setFieldValue("name", nameQuery);
			List<DocumentFromTrec> randomDocs = getRandomSample(NUM_NON_RELEVANT, nonReleventDocs);
			examples.addAll(getExamplesByPairing(query, randomDocs));
			examples.addAll(getRandomNonPseudoRelevantExamples(NUM_PSEUDO_RELEVANT, query, pseudoReleventDocs));
			examples.addAll(getPseudoRelevantExamples(query, pseudoReleventDocs));
		}
		return examples;
	}
	
	private List<DocQueryPairFromFile> getRandomNonPseudoRelevantExamples(int n, Query query, List<DocumentFromTrec> pseudoDocs){
		List<DocQueryPairFromFile> results = new ArrayList<DocQueryPairFromFile>();
		int SIZE = pseudoDocs.size();
		for (int i = 0; i < n; i++) {
			int timeout = 100;
			while (true){
				int randomI = randomizer.nextInt(SIZE);
				DocQueryPairFromFile pair = new DocQueryPairFromFile(pseudoDocs.get(randomI), query);
				if ( ! pair.oracle()){
					results.add(pair);
					break;
				}
				if (timeout -- <= 0){
					System.err.println("Can not find negative example after 100 times for query " + query);
					break;
				}
			}
		}
		return results;
	}
	
	private List<DocQueryPairFromFile> getPseudoRelevantExamples(Query query, List<DocumentFromTrec> pseudoDocs){
		List<DocQueryPairFromFile> results = new ArrayList<DocQueryPairFromFile>();
		for (DocumentFromTrec doc : pseudoDocs) {
			DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
			if ( pair.oracle()){
				results.add(pair);
			}
		}
		return results;
	}
	
	private static Random randomizer = new Random();
	/* random sample with replacement */
	private List<DocumentFromTrec> getRandomSample(int n, List<DocumentFromTrec> docs){
		List<DocumentFromTrec> results = new ArrayList<DocumentFromTrec>();
		int SIZE = docs.size();
		for (int i = 0; i < n; i++) {
			int randomI = randomizer.nextInt(SIZE);
			results.add(docs.get(randomI));
		}
		return results;
	}
	
	private List<DocQueryPairFromFile> getExamplesByPairing(Query query, List<DocumentFromTrec> docs){
		List<DocQueryPairFromFile> results = new ArrayList<DocQueryPairFromFile>();
		for (DocumentFromTrec doc : docs) {
			results.add(new DocQueryPairFromFile(doc, query));
		}
		return results;
	}
	
	private void train(List<DocQueryPairFromFile> examples) {
		List<DocQueryPairFromFile> trainingExamples = new ArrayList<DocQueryPairFromFile>();
		List<DocQueryPairFromFile> testingExamples = new ArrayList<DocQueryPairFromFile>();
		for (DocQueryPairFromFile ex : examples){
//			System.out.println("---" + ex);
			if (Math.random() < 1.0/5){
				testingExamples.add(ex);
			}
			else{
				trainingExamples.add(ex);
			}
		}
		System.out.println("Total Examples : " + examples.size());
		System.out.println("Num training docs : " + trainingExamples.size());
		System.out.println("Num testing docs : " + testingExamples.size());
		
		name_ranker ranker = new name_ranker();
//		ranker.forget();
		ranker.isTraining = true;
		Utils.learnAndTest(ranker, 10, trainingExamples, testingExamples);
		ranker.save();
		ranker.isTraining = false;
	}
	
}
