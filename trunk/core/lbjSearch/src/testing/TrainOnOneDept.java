package testing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ose.query.OQuery;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbj.professor.*;
import lbjse.trainer.Utils;

public class TrainOnOneDept {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		DocQueryFileParser trainingData = new DocQueryFileParser("annotated_docs_index_30000_domain_2.trec","query.txt");
		
		String [] trainingDepts = new String [] {"physics"};
		learnBoth(trainingData, trainingDepts);
		
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}

	private static void learnBoth(DocQueryFileParser dataParser, String[] trainingDepts) {
		Set<String> partitions = new HashSet<String>( Arrays.asList(trainingDepts));
		Set<Integer> allDocIds = Utils.getDocIdsWithField(dataParser.getDocs(),"dept",  partitions);
		allDocIds.addAll( Utils.getOtherDocIds(dataParser.getDocs()) );
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
		learnAndTest(100, dataParser, trainingDocIds, testingDocIds );
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
		Utils.evaluateClassifier(data, trainingDocIds, ranker);
		Utils.evaluateClassifier(data, testingDocIds, ranker);
		
//		ranker.write(System.out);
	}
	
	
}
