package lbjse.experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;

import lbjse.search.LbjseFeatureQuery;
import lbjse.search.LbjseObjectQuery;
import lbjse.search.LbjseObjectQueryBorda;
import lbjse.tools.UpdateRankedResultOracle;
import lbjse.tools.ViewCache;

import common.CommandLineOption;

public class LbjSearchExperiment {
	
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec","output"});
		options.require(new String[]{"fromQuery", "toQuery","index"});
		int fromQueryId = options.getInt("fromQuery");
		int toQueryId = options.getInt("toQuery");
		int indexId = options.getInt("index");
		String outputPrefix = options.getString("output");
		String trecFile = options.getString("trec");
		int topK = Integer.MAX_VALUE;
		if (options.hasArg("topK")){
			topK = options.getInt("topK");
		}
		LbjseFeatureQuery.PAIRWISE = options.hasArg("pairwise");
		
		boolean borda = options.hasArg("borda"); 
		for (int queryId = fromQueryId; queryId <= toQueryId; queryId ++){
			System.out.println("Ranking query " + queryId);
			String outputFile = outputPrefix + "." + queryId;
			if (borda)
				LbjseObjectQueryBorda.rankQueryOnIndexOutputToFile(queryId, indexId, outputFile);
			else
				LbjseObjectQuery.rankQueryOnIndexOutputToFile(queryId, indexId, outputFile);
			String labeledResult = outputFile + ".update";
			UpdateRankedResultOracle.updateOracles(queryId, trecFile, outputFile, labeledResult);
			UpdateRankedResultOracle.updateCacheEntryInDatabase(queryId, indexId, labeledResult);
			if (topK == Integer.MAX_VALUE)
				ViewCache.showMAP(labeledResult );
			else{
				String shortened = labeledResult + ".top_" + topK;
				trimTopKResult(labeledResult, shortened, topK);
				ViewCache.showMAP(shortened );
			}
		}
	}
	private static void trimTopKResult(String resultFile, String shortedFile, int topK) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		PrintWriter writer = new PrintWriter(shortedFile, "utf-8");
		for (int i = 0; i < topK; i++) {
			String line = reader.readLine();
			if (line == null) break;
			writer.println(line);
		}
		writer.close();
		reader.close();
	}
}
