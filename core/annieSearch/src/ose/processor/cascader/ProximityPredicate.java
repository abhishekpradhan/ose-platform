/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

/**
 * @author Pham Kim Cuong
 *
 */
public class ProximityPredicate extends  BaseQueryPredicate implements CompositeQueryPredicate{
	
	protected ConjunctiveJoinIterator proximityIterator;
	protected QueryPredicate childPredicate1, childPredicate2;
	protected double lower, upper;
	
	public ProximityPredicate(String id, QueryPredicate child1, QueryPredicate child2, double lower, double upper) {
		super(id);
		childPredicate1 = child1;
		childPredicate2 = child2;
		proximityIterator = null;
		this.lower = lower; 
		this.upper = upper;
	}
	
	public QueryPredicate getSubPredicate(String id) {
		if (childPredicate1.getID().equals(id))
			return childPredicate1;
		else if (childPredicate2.getID().equals(id))
			return childPredicate2;
		else 
			return null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (proximityIterator == null){
			proximityIterator = new ProximityConjunctiveJoinIterator(
					(DocPositionIterator) childPredicate1.getInvertedListIterator(reader),
					(DocPositionIterator) childPredicate2.getInvertedListIterator(reader),
					lower, upper
					);
		}
		return proximityIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "ProximityPredicate (" + lower + "," + upper + ")\n"); 
		buffer.append(childPredicate1.toString(level + 1));
		buffer.append("\n");
		buffer.append(childPredicate2.toString(level + 1));
		buffer.append("\n");
		return buffer.toString();
	}
}
