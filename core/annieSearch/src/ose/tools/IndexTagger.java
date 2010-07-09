package ose.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.json.JSONArray;
import org.json.JSONObject;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;
import ose.query.OQuery;

import common.CommandLineOption;

public class IndexTagger {

	private String indexPath;
	private int indexId, domainId;
	private Map<String, Integer> urlToDocIdMap;
	private Map<Integer, String> docTagsMap;
	
	public IndexTagger(int indexId, int domainId) throws Exception{
		this.indexId = indexId;
		this.domainId = domainId;
		IndexInfo indexInfo = new IndexInfoManager().getIndexForId(indexId);
		indexPath = indexInfo.getIndexPath();
		readUrlFromIndex();
		countTagFromDatabase();
	}
	
	private void readUrlFromIndex() throws Exception {
		IndexReader reader = IndexReader.open(indexPath);
		urlToDocIdMap = new HashMap<String, Integer>();
		for (int docId = 0; docId < reader.numDocs(); docId++) {
			if (!reader.isDeleted(docId)){
				Document doc = reader.document(docId);
				String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
				urlToDocIdMap.put(url, docId);
			}
		}
		System.out.println("Done reading " + reader.numDocs() + " documents.");
	}
	
	private void countTagFromDatabase() throws Exception {
		System.out.println("Reading existing tags from database");
		docTagsMap = new HashMap<Integer, String>();
		DocTagManager docTagMan = new DocTagManager();
		for (DocTag dt : docTagMan.getAllTagForIndex(indexId)){
			int docId = dt.getDocId();
			if (!docTagsMap.containsKey(docId))
				docTagsMap.put(docId, dt.getValue());
			else
				docTagsMap.put(docId, docTagsMap.get(docId) + " " + dt.getValue());
		}
		System.out.println("Found " + docTagsMap.size() + " tagged documents for this domain");
	}
	
	public void tag(String tagurlFile) throws Exception{
		List<DocTag> pendingTags = new ArrayList<DocTag>();
		BufferedReader reader = new BufferedReader(new FileReader(tagurlFile));
		while (true){
			String tagString = reader.readLine();
			if (tagString == null) break;
			JSONObject json = new JSONObject(tagString);
			String url = reader.readLine();
			if (url == null){
				System.err.println("Missing URL after " + tagString);
				break;
			}			 
			//check both url and its normalized form
			int docId = -1;
			if (urlToDocIdMap.containsKey(url)){
				docId = urlToDocIdMap.get(url);
			}
			else if (url.endsWith("/") && urlToDocIdMap.containsKey(url.substring(0, url.length()-1))){
				docId = urlToDocIdMap.get(url.substring(0, url.length()-1));
			}
		
			if (docId == -1){
				System.err.println("No url found in index " + url);
			}
			else {
				List<DocTag> tags = convertJSONToTag(json, docId);
				if (tags == null){
					System.err.println("Error converting " + tagString);
					return;
				}
				pendingTags.addAll(tags);
			}
		}
		
		System.out.println("Inserting to database");
		DocTagManager docTagMan = new DocTagManager();
		for (DocTag docTag : pendingTags) {
//			System.out.println("Inserting " + docTag);
			docTagMan.insert(docTag);
		}		
		System.out.println("Done inserting " + pendingTags.size() + " tags");
	}
	
	private List<DocTag> convertJSONToTag(JSONObject json, int docId) throws Exception{
		List<DocTag> results = new ArrayList<DocTag>();
		if (docTagsMap.containsKey(docId)){
			System.out.println("Doc " + docId + " already had tags : " + docTagsMap.get(docId) + " ");
			System.out.println("Ignore tags : " + json);
			return results;
		}
		Iterator<String> keyIter = json.keys();
		OQuery query = new OQuery(domainId);
		
		while (keyIter.hasNext()){
			String key = keyIter.next();
			int fieldId = query.getFieldIdFromName(key); 
			if (fieldId == -1) 
				return null; //invalid domain tags
			/* old code that map key->space separated list of tags
			String tagString = json.getString(key).toLowerCase().replaceAll("\\W+", " ");
			for (String tag : tagString.split(" ")){
				DocTag dt = new DocTag(indexId, docId, fieldId, tag);
				results.add(dt);
			}
			*/
			JSONArray tagList = json.getJSONArray(key);
			for (int i = 0; i < tagList.length() ; i++){
				String tag = tagList.getString(i);
				DocTag dt = new DocTag(indexId, docId, fieldId, tag);
				results.add(dt);
			}
		}
		return results;
	}
	
	private static final String USAGE = "" +
			"This program read (url,tags) info from .tagurl file, and insert tags into DocTag database." +
			"If an url has been tagged before, tag from .tagurl file will be ignored.\n" +
			"Usage : IndexTagger --domainId [domain id] \n" +
			"					 --indexId [id] \n" +
			"					 --tagurl [targul file] \n" +
			""; 
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage(USAGE);
		options.require(new String[]{"tagurl","indexId", "domainId"});
		
		IndexTagger tagger = null;
		int indexId = options.getInt("indexId");
		int domainId = options.getInt("domainId");
		tagger = new IndexTagger(indexId, domainId);
		String tagurlFile= options.getString("tagurl");
		tagger.tag(tagurlFile);
	}

}
