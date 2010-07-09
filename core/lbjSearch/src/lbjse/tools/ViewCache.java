package lbjse.tools;

import java.io.FileNotFoundException;
import java.io.IOException;

import lbjse.experiments.ResultMeasure;
import ose.database.LBJSECache;
import ose.database.LBJSECacheManager;

import common.CommandLineOption;

public class ViewCache {

	public void showMAP(int queryId, int indexId) throws Exception {
		LBJSECacheManager man = new LBJSECacheManager();
		LBJSECache cache = man.getCacheForQuery(queryId, indexId);
		String resultFile = cache.getCacheFile();
		System.out.println("Query " + queryId );
		showMAP(resultFile);
	}

	/**
	 * @param resultFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static public double showMAP(String resultFile) throws FileNotFoundException,
			IOException {
		ResultMeasure cache = new ResultMeasure();
		cache.calculateMAP(resultFile);
		
		System.out.println("\t Positive Count : " + cache.getPosCount() );
		System.out.println("\t Average Precision : " + cache.getAvgPres() );
		return cache.getAvgPres();
	}

	public void showPositiveRanks(int queryId,int indexId) throws Exception {
		LBJSECacheManager man = new LBJSECacheManager();
		LBJSECache cache = man.getCacheForQuery(queryId, indexId);
		String cacheFile = cache.getCacheFile();
		System.out.println("Rank for positive document for query " + queryId);
		ResultMeasure measure = new ResultMeasure();
		measure.showPositiveRanks(cacheFile);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"opt","fromQuery","toQuery", "index"});
		int indexId = options.getInt("index");
		ViewCache test = new ViewCache();
		
		int fromQuery = options.getInt("fromQuery");
		int toQuery = options.getInt("toQuery");
		System.out.println("Query" + "\t" + "NPos" + "\t" + "AP");
		for (int queryId = fromQuery ; queryId <= toQuery; queryId ++){
			if ("map".equals(options.getString("opt"))){
				test.showMAP(queryId,indexId);
			}
			else if ("pos".equals(options.getString("opt"))){
				test.showPositiveRanks(queryId,indexId);
			}
			else if ("cache".equals(options.getString("opt"))){
				options.require(new String[]{"cachePath"});
				ViewCache.showMAP(options.getString("cachePath") + "." + queryId + ".update");
			}
		}
	}
}
