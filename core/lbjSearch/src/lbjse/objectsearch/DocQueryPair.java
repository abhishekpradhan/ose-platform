package lbjse.objectsearch;

import lbjse.data.Document;


public interface DocQueryPair {
	public boolean oracle();
	public Document getDoc() ;
	public Query getQuery() ;
	public String toString() ;
}
