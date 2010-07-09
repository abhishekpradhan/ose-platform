package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class BooleanFeaturePredicate extends BaseQueryPredicate {

protected QueryPredicate childPred;
	
	public BooleanFeaturePredicate(String idString, QueryPredicate pred) {
		this.id = idString;
		childPred = pred;
	}
	
	public String toString(int level) {
		return CommonUtils.tabString(level) + "%BooleanFeature[" + childPred + "]\n";
	}
	
	public DocFeatureIterator getInvertedListIterator(IndexReader reader) throws IOException {
		return new BooleanFeatureIterator( childPred.getInvertedListIterator(reader));
	}

}
