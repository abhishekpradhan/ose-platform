package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.query.FeatureValue;


public class WeightedFeatureAggregator extends FeatureAggregator {
	
	protected List<Double> weights;
	
	public WeightedFeatureAggregator(List<QueryPredicate> featurePredicates, List<Double> weights) {
		super(featurePredicates);
		this.weights = weights;
	}
	
	@Override
	public void aggregateResult(IndexReader reader) throws IOException {
		aggregateResult(new OSHits(reader), reader);
	}
	
	@Override
	public void aggregateResult(OSHits result, IndexReader reader)
			throws IOException {
		this.result = result;
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
//		iterator.skipTo(1004595); //for debugging
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			
			double score = 0;
			int i = 0;
			List<FeatureValue> features = iterator.getFeatures();
			for (FeatureValue featureValue : features) {
				if (featureValue != null) {
					score += featureValue.toNumber() * weights.get(i);
				}
				i += 1;
			}
//			List<FeatureValue> onlyOneFeature = Arrays.asList(new FeatureValue[]{new DoubleFeatureValue(score)});
			result.addNewDocument(score, docId, features );
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.print("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
		
//		result.sortByScore();
	}
	
}
