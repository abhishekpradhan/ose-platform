package ose.retrieval;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONException;

import ose.database.DatabaseManager;
import ose.database.QueryInfoManager;
import ose.index.ExtensionFilenameFilter;
import ose.index.FileCorpus;

public class CacheManager {
	private Map<String, CombineResult> cacheResult;
	private static final String SEPARATOR = "!";
	private String cacheDir;
	
	public CacheManager(){
		cacheResult = new HashMap<String, CombineResult>();
	}
	
	public CacheManager(String dir) {
		this();
		cacheDir = dir;
		loadCacheFromDirectory(dir);
	}
	
	private void loadCacheFromDirectory(String dir){
		List<String> fileList = FileCorpus.findAllFiles(new File(dir), new ExtensionFilenameFilter("cache",-1), false);
		for (String fileName : fileList) {
			loadCacheFromFile(dir,new File(fileName).getName());
		}
	}
	
	private void loadCacheFromFile(String dir, String fileName){
		try {
			System.out.println("Loading cache from file " + fileName);
			String searchType = getSearchTypeFromCacheString(fileName);
			int indexId = getIndexIdFromCacheString(fileName);
			int queryId = getQueryIdFromCacheString(fileName);
			QueryInfoManager qMan = new QueryInfoManager(DatabaseManager.getDatabaseManager());
			String structQuery = qMan.queryByKey(queryId).getQueryString();
			String cacheString = getCacheString(searchType, indexId, queryId, null, structQuery);
			CombineResult cacheObj = new CombineResult(0);
			//TODO : broken
			
//			cacheObj.loadFromFile(new File(dir,fileName).getAbsolutePath());
			putCacheNoSave(cacheString, cacheObj);
			throw new RuntimeException("Broken here");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot load cache from file " + fileName);
		} 
	}
	
	static public String getCacheString(String searchType, int indexId, int queryId, String featureQuery, String structQuery){
		String cacheString = searchType + SEPARATOR + indexId + SEPARATOR + queryId + SEPARATOR + featureQuery + SEPARATOR + structQuery ;
		System.out.println("Cache String :" + cacheString );
		return cacheString ;
	}
	
	static public String getSearchTypeFromCacheString(String cacheString){
		StringTokenizer tokenizer = new StringTokenizer(cacheString, SEPARATOR);
		return tokenizer.nextToken();
	}
	
	static public int getIndexIdFromCacheString(String cacheString){
		StringTokenizer tokenizer = new StringTokenizer(cacheString, SEPARATOR);
		List<String> fields = new ArrayList<String>();
		
		while (tokenizer.hasMoreTokens()){
			fields.add(tokenizer.nextToken());
		}
		
		if (fields.size() == 5){
			return Integer.parseInt(fields.get(1));
		}
		else{
			System.out.println("----- got " + fields.size() + " parts ");
			return -1;
		}
			
	}

	static public int getQueryIdFromCacheString(String cacheString){
		StringTokenizer tokenizer = new StringTokenizer(cacheString, SEPARATOR);
		List<String> fields = new ArrayList<String>();
		
		while (tokenizer.hasMoreTokens()){
			fields.add(tokenizer.nextToken());
		}
		
		if (fields.size() == 5){
			return Integer.parseInt(fields.get(2));
		}
		else{
			System.out.println("----- got " + fields.size() + " parts ");
			return -1;
		}
	}

	public CombineResult getCache(String cacheString){
		return cacheResult.get(cacheString) ;
	}
	
	public boolean putCache(String cacheString, CombineResult obj) throws IOException{
		int indexId = getIndexIdFromCacheString(cacheString);
		int queryId = getQueryIdFromCacheString(cacheString);
		String fileName = getSearchTypeFromCacheString(cacheString) + SEPARATOR + indexId + SEPARATOR + queryId + SEPARATOR + "null" + SEPARATOR + "null.cache" ;
		if (indexId != -1 && queryId != -1){
//			obj.saveToFile(new File(cacheDir,fileName).getAbsolutePath());
			throw new RuntimeException("Broken here");
		}
		return putCacheNoSave(cacheString, obj);
	}
	
	public boolean putCacheNoSave(String cacheString, CombineResult obj) throws IOException{
		boolean update = cacheResult.containsKey(cacheString);
		cacheResult.put(cacheString, obj);
		return update;
	}
	
	public void clearCache(){
		cacheResult.clear();
	}
	
}
