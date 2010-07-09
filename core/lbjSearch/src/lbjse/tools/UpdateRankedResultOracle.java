package lbjse.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.rank.ResultItem;
import lbjse.trainer.TrainingSession;
import lbjse.trainer.Utils;
import lbjse.utils.CommonUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.LBJSECache;
import ose.database.LBJSECacheManager;
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;
import ose.index.IndexFieldConstant;
import ose.processor.cascader.CompositeFeatureIterator;
import ose.processor.cascader.DocFeatureIterator;
import ose.processor.cascader.OSHits;
import ose.processor.cascader.QueryPredicate;
import ose.query.FeatureQuery;
import ose.query.FeatureValue;
import ose.query.OQuery;
import LBJ2.classify.FeatureVector;
import LBJ2.learn.Learner;

import common.CommandLineOption;
import common.profiling.Profile;

public class UpdateRankedResultOracle extends FeatureQuery {
	
	protected OSHits result;
	protected OQuery oquery;
	protected int domainId;
	List<String> fields;
	List<Learner> rankers;
	List<List<String>> features;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"rresult","trec","queryId"});
		int queryId = options.getInt("queryId");
		String trecFile = options.getString("trec");
		String rresultFile = options.getString("rresult");
		
		updateOracles(queryId, trecFile, rresultFile, rresultFile + ".update");
	}

	/**
	 * @param queryId
	 * @param trecFile
	 * @param rresultFile
	 * @throws IOException
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * Update oracle value using tags in trec file
	 */
	public static void updateOracles(int queryId, String trecFile,
			String rresultFile, String updatedOutputFile) throws IOException, SQLException,
			FileNotFoundException, UnsupportedEncodingException {
		QueryInfo qinfo = new QueryInfoManager().queryByKey(queryId);
		Query query = new Query(qinfo.getDomainId(), qinfo.getQueryString());
		query.setFieldValue("other", "yes");
		
		updateOracles(trecFile, rresultFile, updatedOutputFile, query);
	}
	
	public static void updateOracles(int domainId, String queryString, String trecFile,
			String rresultFile, String updatedOutputFile) throws IOException, SQLException,
			FileNotFoundException, UnsupportedEncodingException {
		Query query = new Query(domainId, queryString);
		query.setFieldValue("other", "yes");
		
		updateOracles(trecFile, rresultFile, updatedOutputFile, query);
	}

	public static void updateOracles(String trecFile, String rresultFile,
			String updatedOutputFile, Query query) throws IOException,
			SQLException, FileNotFoundException, UnsupportedEncodingException {
		Map<Integer, Boolean> oracles = getOracleMapFromTrecForQuery(query,
				trecFile);
		
		BufferedReader reader = new BufferedReader(new FileReader(rresultFile));
		PrintStream output = new PrintStream(updatedOutputFile, "utf8");
		
		int count = 0;
		int dontKnow = 0;
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			try {
				item = new ResultItem(line);
				if (oracles.containsKey(item.docId)){
					item.label = oracles.get(item.docId);
				}
				else {
					dontKnow += 1;
				}
				output.println(item);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}			
			count += 1;
			
		}
		reader.close();
		output.close();
		System.out.println("Done updating " + count + " items");
		System.out.println("\t" + dontKnow + " unanswered items ");
		System.out.println("File written to " + updatedOutputFile);
	}

	/**
	 * @param queryId
	 * @param trecFile
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Map<Integer, Boolean> getOracleMapFromTrecForQuery(
			Query query, String trecFile) throws IOException, SQLException {
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		Map<Integer, Boolean> oracles = new HashMap<Integer, Boolean>();
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) trecParser.next();
			if (doc == null)
				break;
			if (!CommonUtils.hasEnoughTags(doc, query))
				continue;
			DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
			oracles.put(doc.getDocId(), pair.oracle());
		}
		System.out.println("Found oracles for " + oracles.size() + " examples.");
		return oracles;
	}
	
	static public void updateCacheEntryInDatabase(int queryId, int indexId, String rresultFile) 
		throws SQLException{
		LBJSECacheManager cacheManager = new LBJSECacheManager();
		LBJSECache cache = cacheManager.getCacheForQuery(queryId, indexId);
		if (cache == null){
			cache = new LBJSECache(-1, queryId, indexId, rresultFile, new Date(System.currentTimeMillis()));
			cacheManager.insert(cache);
		}
		else {
			cache.setCacheFile(rresultFile);
			cache.setDateCreated(new Date(System.currentTimeMillis()));
			cacheManager.update(cache);
		}
	}
	
}
