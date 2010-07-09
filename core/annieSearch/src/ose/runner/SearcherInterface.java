package ose.runner;

import ose.retrieval.CombineResult;

public interface SearcherInterface {
	public void setIndexPath(String indexPath);
	public CombineResult search(String featureQuery, String structQuery) throws Exception ;
}
