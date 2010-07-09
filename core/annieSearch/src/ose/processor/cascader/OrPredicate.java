package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class OrPredicate extends BaseQueryPredicate {

	protected List<QueryPredicate> predicateList;
	protected DisjunctiveJoinIterator orIterator;
	
	public OrPredicate(String idString, List<QueryPredicate> subPredicates) {
		super(idString);
		predicateList = subPredicates;
		orIterator = null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (orIterator == null){
			List<DocPositionIterator> subPredicateIterators = new ArrayList<DocPositionIterator>();
			for (QueryPredicate subPredicate : predicateList) {
				try {
					subPredicateIterators .add( (DocPositionIterator)subPredicate.getInvertedListIterator(reader) );
				} catch (ClassCastException e) {
					System.err.println("can not cast !!!");
					throw e; 
				}
			}
			orIterator = new DisjunctivePositionIterator(subPredicateIterators);
		}
		return orIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "OrPredicate\n" ); 
		for (QueryPredicate predicate : predicateList) {
			buffer.append(predicate.toString(level + 1));
			buffer.append("\n");
		}
		return buffer.toString();
	}
	

}
