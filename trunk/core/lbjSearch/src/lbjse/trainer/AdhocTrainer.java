package lbjse.trainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import LBJ2.learn.Learner;

import common.CommandLineOption;

public class AdhocTrainer {
	public boolean bForget = false;
	public String trainingTrec;
	public String testingTrec;
	public String valueFile;
	public String fieldName;
	public int numRounds = 10;
	
	
	String[] fieldValues;
	Learner ranker ;
	
	public void init() throws Exception{
		
		fieldValues = getFieldValuesFromFile(valueFile).toArray(new String[]{});
		ranker = new lbj.professor.other_ranker();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("");
		options.require(new String[] {"trainTrec","testTrec","field","valueList","ranker"});
		AdhocTrainer trainer = new AdhocTrainer();
		
		trainer.bForget = options.hasArg("forget");
		trainer.trainingTrec = options.getString("trainTrec");
		trainer.testingTrec = options.getString("testTrec");
		
		System.out.println("Trec : " + trainer.trainingTrec );
		System.out.println("Forget : " + trainer.bForget );
		
		if (options.hasArg("nrounds")){
			trainer.numRounds = Integer.parseInt(options.getString("nrounds"));
			System.out.println("Number of rounds: " + trainer.numRounds );
		}
		
		trainer.init();
		
		long startTime = System.currentTimeMillis();
		
		trainer.train();
		
		System.out.println("Time : " + (System.currentTimeMillis() - startTime));
	}
	
	static private List<String> getFieldValuesFromFile(String fileName) throws IOException{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			fileName =
			      AdhocTrainer.class.getResource(fileName).getFile();
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
	
	public void train() throws IOException{
		List<Query> queries = new ArrayList<Query>();
		for (String uni : fieldValues) {
			Query q = new Query();
			q.setFieldValue(fieldName, uni);
			queries.add(q);
		}
		
		LBJTrecFileParser trecReader = new LBJTrecFileParser (trainingTrec);
		List<DocumentFromTrec> trainingDocs = trecReader.getDocs();
		trecReader = new LBJTrecFileParser (testingTrec);
		List<DocumentFromTrec> testingDocs = trecReader.getDocs();
		
		System.out.println("Total docs : " + trainingDocs.size());
		DocQueryFileParser trainingData = new DocQueryFileParser(trainingDocs, queries);
		DocQueryFileParser testingData = new DocQueryFileParser(testingDocs, queries);
		
		if (bForget )
			ranker.forget();
		
		((lbjse.learning.professor.inde.other_ranker)ranker).isTraining = true;
		
		Utils.learnAndTest(ranker, numRounds, trainingData, testingData);
		ranker.save();		
	}
	
}
