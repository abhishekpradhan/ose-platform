/**
 * 
 */
package ose.database;

/**
 * @author Nam Khanh Tran
 *
 */
public class QueryTrans {
	private int queryTransId;
	private String queryString;
	private int queryInfoId;
	private int domainId;
	
	public QueryTrans(int _queryTransId, String _queryString, int _queryInfoId, int _domainId) {
		this.queryTransId = _queryTransId;
		this.queryString = _queryString;
		this.queryInfoId = _queryInfoId;
		this.domainId = _domainId;
	}
	
	public int getQueryTransId() {
		return this.queryTransId;
	}
	
	public String getQueryString() {
		return this.queryString;
	}
	
	public int getQueryInfoId() {
		return this.queryInfoId;
	}
	
	public int getDomainId() {
		return this.domainId;
	}
	
	@Override
	public String toString() {
		return "Query Translation : " + queryTransId + " " + queryString + " " + queryInfoId + " " + domainId;
	}

}
