package lbjse.tools;

import lbjse.rank.ObjectRanker;
import lbjse.rank.ObjectRanker.AggregationMode;

import common.CommandLineOption;
import common.profiling.Profile;

public class RunRanker {

	static String rankerOpt;
	static AggregationMode aggMode;
	
	static public ObjectRanker getRanker(String trecFile, int queryId, String outputPath) throws Exception{
		if ("cond".equals(rankerOpt))
			return new lbjse.rank.conditional.ProfessorRank(trecFile, queryId, outputPath, aggMode);
		else if ("inde".equals(rankerOpt))
			return new lbjse.rank.inde.ProfessorRank(trecFile, queryId, outputPath, aggMode);
		else
			return null;
	}
	/**
	 * @param args
	 * Examples : 
	 * ProfessorRank 
	 * --ranker cond|inde
	 * --mode rankAll 
	 * --result "C:\\working\\lbjSearch\\professor.ranked_result" 
	 * --index 30000 
	 * --query 224 
	 * --trec "combine_prof.trec"
	 */
	
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"ranker","mode", "trec"});
		aggMode = AggregationMode.MULTIPLY;
		if (options.hasArg("agg")){
			aggMode = AggregationMode.valueOf(options.getString("agg").toUpperCase());
		}
		System.out.println("AggMode : " + aggMode);
		rankerOpt = options.getString("ranker");
		boolean withDatabase = ! options.hasArg("nodb");
		String mode = options.getString("mode");
		Profile.getProfile("main").start();
		
		String trecFile = options.getString("trec");
		System.out.println("Mode : " + mode);
		if (mode.equals("rankOne")){
			options.require(new String[]{"query"});
			int queryId = options.getInt("query");
			options.require(new String[]{"doc"});
			int docId = options.getInt("doc");
			ObjectRanker ranker = getRanker(trecFile, queryId, "");
			ranker.showScoreForDocId(docId);
		}
		else{
			options.require(new String[]{"result"});
			String resultPath = options.getString("result");
			
			if (mode.equals("rankBatch")){
				options.require(new String[]{"fromQuery", "toQuery","index"});
				int fromQueryId = options.getInt("fromQuery");
				int toQueryId = options.getInt("toQuery");
				int indexId = options.getInt("index");
				for (int queryId = fromQueryId; queryId <= toQueryId; queryId ++){
					ObjectRanker ranker = getRanker(trecFile, queryId, resultPath + "/" + queryId + ".ranked_result");
					System.out.println("Ranking query " + queryId);
					ranker.rankQueryAndOutputAP();
					if (withDatabase ){
						ranker.saveCacheToDatabase(queryId, indexId);
						ranker.addFeedbackToDatabase(queryId, indexId);
					}
				}
			}
			else{
				options.require(new String[]{"query"});
				int queryId = options.getInt("query");
				
				ObjectRanker ranker = getRanker(trecFile, queryId, resultPath);
				
				if (mode.equals("rankAll")){
					options.require(new String[]{"index"});
					int indexId = options.getInt("index");
					
					ranker.rankQueryAndOutputAP();			
					if (withDatabase ){
						ranker.saveCacheToDatabase(queryId, indexId);
						ranker.addFeedbackToDatabase(queryId, indexId);
					}
				}
				else  if (mode.equals("feedback")){
					options.require(new String[]{"index"});
					int indexId = options.getInt("index");
					
					ranker.addFeedbackToDatabase(queryId, indexId);
				}
				else if (mode.equals("cache2db")){
					options.require(new String[]{"index"});
					int indexId = options.getInt("index");
					
					ranker.saveCacheToDatabase(queryId, indexId);
				}
				else {
					System.err.println("Unknown mode " + mode);
				}
			}
		}
		
		
		
		Profile.getProfile("main").end();
		System.out.println("Done in " + Profile.getProfile("main").getTotalTime() + " millisec");
	}

}
