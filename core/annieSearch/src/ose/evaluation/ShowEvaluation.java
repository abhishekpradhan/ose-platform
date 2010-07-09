package ose.evaluation;

import java.io.File;

import ose.retrieval.CacheManager;
import ose.retrieval.CombineResult;
import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.RankingEvaluation;

public class ShowEvaluation {

	public ShowEvaluation() {
	}
	
	public void show(String resultFilePath) throws Exception {
		String fileName = new File(resultFilePath).getName();
		
		System.out.println("Loading...");
		CombineResult cacheObj = new CombineResult(0);
//		cacheObj.loadFromFile(resultFilePath);
		System.err.println("Broken here while refactoring");
		int queryIdForEvaluation = CacheManager.getQueryIdFromCacheString(fileName);
		int indexIdForEvaluation = CacheManager.getIndexIdFromCacheString(fileName);
		
		RankingEvaluation evaluation = new ObjectRankingEvaluation(queryIdForEvaluation, indexIdForEvaluation);
		
		
		System.out.println("Evaluation...");
		evaluation.evaluate(cacheObj.getRankedOrder());
		evaluation.prettyPrint();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		final String DIR = "C:/tmp/annieSearch/eval";
		final String RESULT_FILE = DIR + "/professor!11!305!null!null.cache";  
		ShowEvaluation eval = new ShowEvaluation();
		eval.show(RESULT_FILE);
	}

}
