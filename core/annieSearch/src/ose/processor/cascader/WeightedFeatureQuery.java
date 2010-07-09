package ose.processor.cascader;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.IndexReader;


public class WeightedFeatureQuery extends FeatureQuery {
	
	protected List<Double> weights;
	
	public WeightedFeatureQuery(List<QueryPredicate> queryPredicates, List<Double> weights) {
		super(queryPredicates);
		if (queryPredicates.size() != weights.size()){
			throw new RuntimeException("Weight vector size doesn't match query predicates");
		}
		this.weights = weights;
	}
	
	@Override
	public List<QueryPredicate> getPredicates() {
		return super.getPredicates();
	}
	
	@Override
	public OSHits search(IndexReader reader) throws IOException {
		WeightedFeatureAggregator aggregator = new WeightedFeatureAggregator(featurePredicates, weights);
		OSHits result = new OSHits(reader);
		aggregator.aggregateResult(result, reader);
		return result;
	}
	
}
