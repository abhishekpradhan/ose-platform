package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class PhrasePredicate extends BaseQueryPredicate implements CompositeQueryPredicate {
	protected List<QueryPredicate> predicateList;
	protected ConjunctiveJoinIterator phraseIterator;
	
	public PhrasePredicate(String idString, List<QueryPredicate> subPredicates) {
		super(idString);
		predicateList = subPredicates;
		phraseIterator = null;
	}
	
	public QueryPredicate getSubPredicate(String id) {
		for (QueryPredicate subPred : predicateList) {
			if (subPred.getID().equals(id))
				return subPred;
		}
		return null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (phraseIterator == null){
			List<DocPositionIterator> subPredicateIterators = new ArrayList<DocPositionIterator>();
			for (QueryPredicate subPredicate : predicateList) {
				try {
					subPredicateIterators .add( (DocPositionIterator) subPredicate.getInvertedListIterator(reader) );
				} catch (ClassCastException e) {
					System.err.println("can not cast !!!");
					throw e; 
				}
			}
			phraseIterator = new ConsecutiveConjunctiveJoinIterator(subPredicateIterators);
		}
		return phraseIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "PhrasePredicate\n" ); 
		for (QueryPredicate predicate : predicateList) {
			buffer.append(predicate.toString(level + 1));
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	
}
