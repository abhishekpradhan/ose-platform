/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.learning.IntegerFeatureValue;
import ose.utils.CommonUtils;

/**
 * @author Pham Kim Cuong
 */

public class CountNumberPredicate extends BaseQueryPredicate  {

	protected QueryPredicate childPredicate;
	protected QueryPredicate numberPredicate;
	protected DocPositionIterator childIterator;
//	protected DocPositionIterator numberIterator;
	
	public CountNumberPredicate(String idString, QueryPredicate pred) {
		super(idString);
		childPredicate = pred;
//		if (pred.getID().equals("Number")){
//			numberPredicate = pred;
//		}
//		else if (pred instanceof CompositeQueryPredicate) {
//			CompositeQueryPredicate subPred = (CompositeQueryPredicate) pred;
//			numberPredicate = subPred.getSubPredicate("Number");
//		}
	}
	
	public DocFeatureIterator getInvertedListIterator(IndexReader reader)
			throws IOException {
		if (childIterator == null){
			childIterator = (DocPositionIterator) childPredicate.getInvertedListIterator(reader);
//			numberIterator = (DocPositionIterator) childPredicate.getInvertedListIterator(reader);
		}
		return new CountNumberFeatureIterator(childIterator);
	}
	

	
	public String toString(int level) {
		return CommonUtils.tabString(level) + "%CountNumber(" + childPredicate + ")";
	}

}
