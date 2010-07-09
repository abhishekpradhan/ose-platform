package lbjse.datacleaning;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lbj.common.WordsInBody;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.ObjectInfo;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import ose.database.DocTag;
import ose.database.DocTagManager;
import ose.database.IndexInfoManager;
import ose.index.TrecDocument;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;
import common.profiling.Profile;

public class TagGuesser {

	private String cachePath;
	public static Map<Integer, Set<String>> fieldValueMap ;
	private int indexId;
	
	public TagGuesser(int indexId) throws SQLException{
		this.indexId = indexId;
		cachePath = new IndexInfoManager().getIndexForId(indexId).getCachePath();
		fieldValueMap = new HashMap<Integer, Set<String>>();
	}
	
	/* 
	 * return fieldId --> list of tag values for that field name
	 */
	public Map<Integer, List<String>> guessTag(int domainId, int docId) {
		ObjectInfo oinfo = new ObjectInfo(domainId);
		Map<Integer, List<String>> tags = new HashMap<Integer, List<String>>();
		for (Integer fieldId : oinfo.getIdNameMap().keySet()){
			if (!fieldValueMap.containsKey(fieldId)){
				fieldValueMap.put(fieldId, getAllTagsForFieldId(indexId, fieldId));
			}
			if ("other".equals( oinfo.getFieldName(fieldId) ) ){ //for "other" tag, add all existing tags
				tags.put(fieldId, new ArrayList<String>(fieldValueMap.get(fieldId)));
				System.out.println(tags.get(fieldId).size() + " other tags ");
			} else
				tags.put(fieldId, new ArrayList<String>());
		}
		try {
			Profile.getProfile("guessing").start();
			IndexReader reader = IndexReader.open(cachePath);
			TrecDocument trec = new TrecDocument(reader, docId);
			if (!trec.isParsed())
				trec.parseHtml(false);
			DocumentFromTrec lbjTrec = new DocumentFromTrec(docId, trec.getUrl(), trec.getTitle(), trec.getPlainBody());
			WordsInBody tokenGenerator = new WordsInBody();
			FeatureVector fvector = tokenGenerator.classify(new DocQueryPairFromFile(lbjTrec, null));
			for (Object df : fvector.features){
				DiscreteFeature dfeature = (DiscreteFeature) df;
				String val = dfeature.getValue();
				for (Integer fieldId : oinfo.getIdNameMap().keySet()){
					if (fieldValueMap.get(fieldId).contains(val)){
						if (tags.get(fieldId).indexOf(val) == -1) //only add the tag once
							tags.get(fieldId).add(val);
					}
				}
			}			
			reader.close();
			Profile.getProfile("guessing").end();			
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tags;
		
	}
	
	public static Set<String> getAllTagsForFieldId(int indexId, int fieldId) {
		Set<String> tagValueSet = new HashSet<String>();
		DocTagManager dtMan = new DocTagManager();
		try {
			for (DocTag dt : dtMan.getAllTagForIndexFieldId(indexId, fieldId)){
				tagValueSet.add(dt.getValue());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tagValueSet;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"domainId", "indexId","docId"});
		int domainId = options.getInt("domainId");
		int indexId = options.getInt("indexId");
		int docId = options.getInt("docId");
		TagGuesser guesser = new TagGuesser(indexId);
		System.out.println("Tags : " + guesser.guessTag(domainId, docId));
		Profile.printAll();
	}
}
