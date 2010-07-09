package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class TFFeaturePredicate extends BaseQueryPredicate {
	
	protected QueryPredicate childPred;
	
	public TFFeaturePredicate(String idString, QueryPredicate pred) {
		this.id = idString;
		childPred = pred;
	}
	
	public String toString(int level) {
		return CommonUtils.tabString(level) + "%TFFeature[" + childPred + "]\n";
	}
	
	public DocFeatureIterator getInvertedListIterator(IndexReader reader) throws IOException {
		return new TFFeatureIterator( (DocPositionIterator) childPred.getInvertedListIterator(reader));
	}
}
