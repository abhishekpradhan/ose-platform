/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.SetBasedFieldSelector;
import org.apache.lucene.index.IndexReader;

import ose.query.FeatureValue;


/**
 * @author Pham Kim Cuong
 *
 */
public class OSHits implements Iterable<Document>, Iterator<Document> {
	protected List<Document> docs;
	protected List<Integer> docIDs;
	protected List<Double> scores;
	protected Integer[] order;
	protected List< List<FeatureValue> > features;
	protected IndexReader reader;
	protected int currentDoc;

	private Set<String> FIELD_TO_LOAD = new HashSet<String>(Arrays.asList(
			new String[]{}));
//			new String[]{IndexFieldConstant.FIELD_DOCUMENT_ID, IndexFieldConstant.FIELD_DOCUMENT_TITLE}));
	private FieldSelector DOC_FIELD_SELECTOR = new SetBasedFieldSelector(FIELD_TO_LOAD, new HashSet<String>());
	
	public OSHits(IndexReader reader){
		this.reader = reader;
		currentDoc = 0;
		docs = new ArrayList<Document>();
		docIDs = new ArrayList<Integer>();
		scores = new ArrayList<Double>();
		features = new ArrayList< List<FeatureValue> >();
		order = null;
	}
	
	public void addNewDocument(double score, int docId, List<FeatureValue> featureList) throws IOException{
		Document doc = reader.document(docId, DOC_FIELD_SELECTOR);		
		docIDs.add(docId);
		docs.add(doc);
		features.add(featureList);
		scores.add(score);
		order = null; //reset the order since it's no longer valid
	}
	
	
	public Iterator<Document> iterator() {
		currentDoc = 0;
		return this;
	}
	
	public boolean hasNext() {
		return currentDoc < docs.size(); 
	}
	
	public int getDocID(){
		if (order == null)
			return docIDs.get(currentDoc - 1);
		else
			return docIDs.get(order[currentDoc-1]);
	}
	
	public Document next() {
		currentDoc += 1;
		if (order == null)
			return docs.get(currentDoc - 1);
		else
			return docs.get(order[currentDoc - 1]);
	}
	
	public double score(){
		if (order == null)
			return scores.get(currentDoc-1);
		else
			return scores.get(order[currentDoc-1]);
	}
	
	public List<FeatureValue> docFeatures(){
		if (order == null)
			return features.get(currentDoc-1);
		else
			return features.get(order[currentDoc-1]);
	}
	
	public void remove() {
		throw new RuntimeException("Method not implemented");
	}
	
	public void sortByScore(){
		order = new Integer[scores.size()];
		for (int i = 0; i < order.length; i++) {
			order[i] = i;
		}
		Arrays.sort(order,new OrderComparator());
	}
	
	public int getSize() {
		return docs.size();
	}
	
	class OrderComparator implements Comparator<Integer>{
		public int compare(Integer o1, Integer o2) {
			double t = - (scores.get(o1) - scores.get(o2) ) ; //reverse order
			if (t < 0)
				return -1;
			else if (t > 0)
				return 1;
			else
				return 0;
		}
	}
}
