package lbjse.objectsearch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

import ose.database.LBJSEQuery;
import ose.database.LBJSEQueryManager;

import LBJ2.parse.Parser;

public class DocQueryPairParser implements Parser{

	private List<Integer> docIds;
	private List<Query> queries;
	private int currentDoc, currentQuery;
	private DocAnnotations annotations;
	private String sourceName;
	private Configuration conf;
	
	public DocQueryPairParser(String confFile) {
		conf = new Configuration();
		conf.addResource(confFile);
		
		if (conf.get("lbjse.lucene.IndexPath") == null || conf.get("lbjse.lucene.CachePath") == null ||
				conf.get("lbjse.examples.DocIds") == null || conf.get("lbjse.examples.QueryIds") == null
				|| conf.get("lbjse.domainId") == null || conf.get("lbjse.indexId") == null){
			throw new RuntimeException("invalid configuration file : " + confFile);
		}
		sourceName = confFile;
		int domainId = conf.getInt("lbjse.domainId",1);
		int indexId = conf.getInt("lbjse.indexId",8);
		try {
			LBJSEQueryManager tqiMan = new LBJSEQueryManager();
			
			queries = new ArrayList<Query>();
			
			for (String queryIdStr : conf.getStrings("lbjse.examples.QueryIds")){
				int queryId = Integer.parseInt(queryIdStr.trim());
				LBJSEQuery lbjseQuery = tqiMan.getById(queryId);
				Query query = new Query(domainId, lbjseQuery.getValue());
				queries.add(query);
			}
			
			System.out.println("\tNumber of sample queries found " + queries.size());
			
			annotations = new DocAnnotations(domainId, indexId);
			annotations.addTagsFromDatabase();
			annotations.addTagRulesFromDatabase();
			
			//docIds = annotations.getAllDocIds();
			docIds = new ArrayList<Integer>();
			for (String docIdStr : conf.getStrings("lbjse.examples.DocIds")){
				docIds.add(Integer.parseInt(docIdStr.trim()));
			}
			System.out.println("\tNumber of annotated docIds found " + docIds.size());
			currentDoc = 0;
			currentQuery = 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//this constructor is for the search servlet
	public DocQueryPairParser(int domainId, int indexId, Query query) {
		queries = new ArrayList<Query>();
		queries.add(query);
		try {
			annotations = new DocAnnotations(domainId, indexId);
			annotations.addTagsFromDatabase();
			annotations.addTagRulesFromDatabase();
			
			//docIds = annotations.getAllDocIds();
			docIds = new ArrayList<Integer>();
			for (Integer docId : annotations.getAllDocIds()){
				docIds.add(docId);
			}
			System.out.println("\tNumber of annotated docIds found " + docIds.size());
			currentDoc = 0;
			currentQuery = 0;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Configuration getConf() {
		return conf;
	}
	
	public String getSourceName() {
		return sourceName;
	}
	
	public Object next() {
		if (currentQuery >= queries.size())
			return null;
		int docId = docIds.get(currentDoc);
		
		DocQueryPair pair = new DocQueryPairFromDatabase(annotations.getAnnotatedDocumentFromId(docId),queries.get(currentQuery));
		
		currentDoc += 1;
		if (currentDoc >= docIds.size()){
			currentDoc = 0;
			currentQuery += 1;			
		}
		return pair;
	}
	
	public void reset() {
		currentDoc = 0;
		currentQuery = 0;
	}
	
	public DocAnnotations getDocAnnotations() {
		return annotations;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DocQueryPairParser parser = new DocQueryPairParser("training_brand.xml");
		int count = 0;
		while (parser.next() != null) 
			count += 1;
		System.out.println("Total " + count + " examples. ");
	}

}
