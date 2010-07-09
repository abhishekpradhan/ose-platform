/**
 * 
 */
package ose.processor.cascader;



/**
 * @author Pham Kim Cuong
 *
 */
public abstract class BaseQueryPredicate implements QueryPredicate {
	protected String id;

	protected BaseQueryPredicate() {
		id = null;
	}
	
	public BaseQueryPredicate(String idString) {
		id = idString;
	}
	
	public String getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return toString(0);
	}

}
