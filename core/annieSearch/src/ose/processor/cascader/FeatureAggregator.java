package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;


public class FeatureAggregator implements ResultAggregator {

//	private static Logger LOG = Logger.getLogger(RankingAggregator.class);
	
	OSHits result = null;
	
	protected List<QueryPredicate> featurePredicateList;
	
	protected IndexReader reader;
	
	public FeatureAggregator(List<QueryPredicate> featurePredicates) {
		if (featurePredicates == null)
			featurePredicates = new ArrayList<QueryPredicate>();
		featurePredicateList = featurePredicates;
	}
	
	/* 
	 * TODO: refactor this to use MultipleTermIterator for the boolean part
	 */
	public void aggregateResult(IndexReader reader) throws IOException {
		aggregateResult(new OSHits(reader),reader);
	}

	protected final int CHECK_POINT = 10000;
	/**
	 * @param reader
	 * @throws IOException
	 */
	public void aggregateResult(OSHits result, IndexReader reader) throws IOException {
		this.result = result;
		DocFeatureIterator iterator = getFeatureIterator(reader, featurePredicateList);
//		iterator.skipTo(1004595);
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			result.addNewDocument(0.0, docId, iterator.getFeatures());
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.print("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
		
//		result.sortByScore();
	}

	static public DocFeatureIterator getFeatureIterator(IndexReader reader, List<QueryPredicate> featurePredicateList)
			throws IOException {
		List<DocFeatureIterator> featureGenIterList = new ArrayList<DocFeatureIterator>();
		for (QueryPredicate pred : featurePredicateList) {
			featureGenIterList.add( (DocFeatureIterator) pred.getInvertedListIterator(reader) );
		}
		DocFeatureIterator iterator;
		if (featureGenIterList.size() == 1){
			iterator = featureGenIterList.get(0);
		}
		else{
			iterator = new CompositeFeatureIterator(featureGenIterList);
		}
		return iterator;
	}

	public OSHits getHits() {
		return result;
	}

	static public DocPositionIterator getDocPositionIterator(IndexReader reader, List<QueryPredicate> featurePredicateList) throws IOException{
		List<DocPositionIterator> featureGenIterList = new ArrayList<DocPositionIterator>();
		for (QueryPredicate pred : featurePredicateList) {
			featureGenIterList.add( (DocPositionIterator) pred.getInvertedListIterator(reader) );
		}
		DocPositionIterator iterator;
		if (featureGenIterList.size() == 1){
			iterator = featureGenIterList.get(0);
		}
		else{
			iterator = new DisjunctivePositionIterator(featureGenIterList);
		}
		return iterator;
	}
}
