package lbjse.objectsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.GenericPair;

import lbjse.data.DocumentFromTrec;

import LBJ2.parse.Parser;

public class DocQueryFileParser implements Parser{

	private List<DocumentFromTrec> docs;
	private List<Query> queries;
	private int currentPair;
	
	private LBJTrecFileParser trecParser;	
	private ObjectInfo objectInfo;
	private List<DocQueryPairFromFile> pairIds;
	
	public DocQueryFileParser(List<DocumentFromTrec> docs, List<Query> queries) {
		this.docs = docs;
		this.queries = queries;
		trecParser = null;
		objectInfo = null;
		initializeAll();
	}
	
	private void initializeAll(){
		pairIds = new ArrayList<DocQueryPairFromFile>();
		for(int i = 0; i < docs.size() ; i++)
			for (int j = 0 ; j < queries.size(); j++)
				pairIds.add(new DocQueryPairFromFile(docs.get(i),queries.get(j)));
		reset();
	}
	
	public void initializeWithNegativeSampling(double negativeRatio){
		//first, count pos/neg examples
		double positive = 0;
		double negative = 0;
		
		while (true){
			DocQueryPairFromFile pair = (DocQueryPairFromFile) next();
			if (pair == null) break;
			if (pair.oracle())
				positive += 1;
			else
				negative += 1;
		}
		
		double odd = negativeRatio / (negative / positive) ;
		reset();
		//sampling
		Random rand = new Random(System.currentTimeMillis());
		ArrayList<DocQueryPairFromFile> temp = new ArrayList<DocQueryPairFromFile>();
		while (true){
			DocQueryPairFromFile pair = (DocQueryPairFromFile) next();
			if (pair == null) break;
			if (pair.oracle())
				temp.add(pair);
			else{
				if (rand.nextDouble() < odd){
					temp.add(pair);
				}
			}
		}
		pairIds = temp;
		reset();
		System.out.println("Done sampling.");
	}
	
	public DocQueryFileParser(String trecFile, String queryFile) {
		try {
			readQueries(queryFile);
			trecParser = new LBJTrecFileParser(trecFile);
			docs =  trecParser.getDocs();
			initializeAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reset();
	}

	public List<Query> getQueries() {
		return queries;
	}
	
	public List<DocumentFromTrec> getDocs() {
		return docs;
	}
	
	private void readQueries(String queryFile) throws IOException{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(queryFile));
		} catch (FileNotFoundException e) {
			queryFile =
			      DocQueryFileParser.class.getResource(queryFile).getFile();
			reader = new BufferedReader(new FileReader(queryFile));
		}
		
		
		
		String domainInfoFile = reader.readLine();
		File qFile = new File(queryFile);
		File dFile = new File(qFile.getParent(), domainInfoFile);
		objectInfo = new ObjectInfo(dFile.getPath());
		
		queries = new ArrayList<Query>();
		String line ;
		while ( (line = reader.readLine()) != null){
			queries.add(new Query(objectInfo, line));
		}
		reader.close();
	}
	
	public Object next() {
		if (currentPair >= pairIds.size())
			return null;
		
		DocQueryPairFromFile pair = pairIds.get(currentPair);
		currentPair += 1;
		return pair;
	}
	
	public void reset() {
		currentPair = 0;
	}
	
	public ObjectInfo getObjectInfo() {
		return objectInfo;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
//		DocQueryFileParser parser = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query.txt");
		DocQueryFileParser parser = new DocQueryFileParser("C:\\working\\annotated_docs_index_30000_domain_2.trec","C:\\working\\query.txt");
		parser.initializeAll();
		int count = 0;
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) parser.next()) != null){ 
			count += 1;
			System.out.println(pair.getDoc() + "\t" + pair.getQuery() + "\t" + pair.oracle());
		}
		System.out.println("Total " + count + " examples. ");
	}

}
