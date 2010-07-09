package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.query.FeatureValue;


public class FullExampleSet extends OSHits {
	private int numDocs;
	protected Map<Integer, Integer> docIDMap;
	private List<FeatureValue> nullValues;
	
	public FullExampleSet(IndexReader reader){
		super(reader);
		numDocs = reader.numDocs();
		docIDMap = new HashMap<Integer,Integer>();
		nullValues = null;
	}
	
	public void addNewDocument(double score, int docId, List<FeatureValue> featureList) throws IOException{
		super.addNewDocument(score, docId, featureList);
		if (nullValues == null){
			nullValues = new ArrayList<FeatureValue>();
			for (int i = 0; i < featureList.size(); i++) {
				nullValues.add(null);
			}
		}
		docIDMap.put(docId, docs.size() - 1); //map this one to the last element of the list
		
	}
	
	public Iterator<Document> iterator() {
		currentDoc = 0;
		return this;
	}
	
	public boolean hasNext() {
		return currentDoc < numDocs;
	}
	
	public Document next() {
		currentDoc += 1;
		if (docIDMap.containsKey(currentDoc-1)){
			return docs.get(docIDMap.get(currentDoc-1));
		}
		else
			return null;
	}
	
	public double score(){
		if (docIDMap.containsKey(currentDoc-1)){
			return scores.get(docIDMap.get(currentDoc-1));
		}
		else
			return 0.0;
	}
	
	public List<FeatureValue> docFeatures(){
		if (docIDMap.containsKey(currentDoc-1)){
			return features.get(docIDMap.get(currentDoc-1));
		}
		else {
			return nullValues;
		}
	}
	
}
