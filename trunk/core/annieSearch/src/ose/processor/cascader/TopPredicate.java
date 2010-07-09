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
public class TopPredicate extends  BaseQueryPredicate {
	
	protected DocIterator cachedIterator;
	protected QueryPredicate childPredicate;
	protected int topK;
	
	public TopPredicate(String id, QueryPredicate child, int k) {
		super(id);
		childPredicate = child;
		topK = k; 
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (cachedIterator == null){
			cachedIterator = new TopKPositionIterator(
					(DocPositionIterator) childPredicate.getInvertedListIterator(reader),
					topK
					);
		}
		return cachedIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "TopPredicate (" + topK + ")\n"); 
		buffer.append(childPredicate.toString(level + 1));
		buffer.append("\n");
		return buffer.toString();
	}
}
