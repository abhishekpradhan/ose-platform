/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.IndexReader;


/**
 * @author Pham Kim Cuong
 *
 */
public class BooleanQuery{
	
	protected List<? extends QueryPredicate> predicates;
	
	public BooleanQuery(List<? extends QueryPredicate> queryPredicates){
		predicates = queryPredicates;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BooleanQuery:\n");
		for (QueryPredicate predicate : predicates) {
			buffer.append(predicate.toString(1) + "\n");
		}
		return buffer.toString();
	}
	
	public List<QueryPredicate> getPredicates() {
		List<QueryPredicate> result = Arrays.asList(predicates.toArray(new QueryPredicate[]{}));
		return result;
	}
	
	public OSHits search(IndexReader reader) throws IOException {
		//TODO : fix this, broken while cleaning
		return null;
	}
	
}

