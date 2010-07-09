/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexReader;

import ose.query.FeatureValue;


/**
 * @author Pham Kim Cuong
 *
 */

public class FeatureQuery extends BooleanQuery{
	
	protected List<QueryPredicate> featurePredicates;
	
	public FeatureQuery(List<QueryPredicate> queryPredicates) {
		super(queryPredicates);
		featurePredicates = queryPredicates;
	}
	
	public OSHits fillFeatures(IndexReader reader) throws IOException {
		FeatureAggregator aggregator = new FeatureAggregator(featurePredicates);
		OSHits result = new FullExampleSet(reader);
		aggregator.aggregateResult(result, reader);
		return result;
	}
	
	public OSHits search(IndexReader reader) throws IOException {
		FeatureAggregator aggregator = new FeatureAggregator(featurePredicates);
		OSHits result = new OSHits(reader);
		aggregator.aggregateResult(result, reader);
		return result;
	}
	
	public List<FeatureValue> getFeaturesForDoc(IndexReader reader, int docId) throws IOException {
		FeatureAggregator aggregator = new FeatureAggregator(featurePredicates);
		DocFeatureIterator iterator = aggregator.getFeatureIterator(reader, featurePredicates);
		if (iterator.skipTo(docId) && iterator.getDocID() == docId){
			return iterator.getFeatures();
		}
//		System.out.println("--- " + iterator.getDefaultValues() + " " + iterator);
		return iterator.getDefaultValues();
	}
	
	public DocPositionIterator getDocPositionIterator(IndexReader reader) throws IOException {
		DocPositionIterator iterator = FeatureAggregator.getDocPositionIterator(reader, featurePredicates);
		return iterator;
	}
	
	public DocFeatureIterator getDocFeatureIterator(IndexReader reader) throws IOException {
		DocFeatureIterator iterator = FeatureAggregator.getFeatureIterator(reader, featurePredicates);
		return iterator;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (QueryPredicate pred : predicates) {
			buffer.append(pred.toString(0));
		} 
		return buffer.toString();
	}
	
	static public String instantiate(String featureQuery, Map<String, String> osQuery){
		for (Entry<String, String> field : osQuery.entrySet()) {
			String fieldName = field.getKey();
			String fieldValue = field.getValue();
			featureQuery = featureQuery.replaceAll(fieldName.toUpperCase(), fieldValue);
		}
		return featureQuery;
	}
}
