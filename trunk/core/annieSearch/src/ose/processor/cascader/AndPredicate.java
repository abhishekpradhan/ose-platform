package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class AndPredicate extends BaseQueryPredicate implements
		CompositeQueryPredicate {

	protected List<QueryPredicate> predicateList;
	protected ConjunctiveJoinIterator andIterator;
	
	public AndPredicate(String idString, List<QueryPredicate> subPredicates) {
		super(idString);
		predicateList = subPredicates;
		andIterator = null;
	}
	
	public QueryPredicate getSubPredicate(String id) {
		for (QueryPredicate subPred : predicateList) {
			if (subPred.getID().equals(id))
				return subPred;
		}
		return null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (andIterator == null){
			List<DocIterator> subPredicateIterators = new ArrayList<DocIterator>();
			for (QueryPredicate subPredicate : predicateList) {
				try {
					subPredicateIterators .add( subPredicate.getInvertedListIterator(reader) );
				} catch (ClassCastException e) {
					System.err.println("can not cast !!!");
					throw e; 
				}
			}
			andIterator = new ConjunctiveJoinIterator(subPredicateIterators);
		}
		return andIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "AndPredicate\n" ); 
		for (QueryPredicate predicate : predicateList) {
			buffer.append(predicate.toString(level + 1));
			buffer.append("\n");
		}
		return buffer.toString();
	}
	


}
