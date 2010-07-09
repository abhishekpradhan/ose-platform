/**
 * 
 */
package ose.processor.cascader;


/**
 * @author Pham Kim Cuong
 *
 */
public interface CompositeQueryPredicate extends QueryPredicate {
	public QueryPredicate getSubPredicate(String id);
}
