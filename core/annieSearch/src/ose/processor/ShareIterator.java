package ose.processor;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;


public interface ShareIterator {
	
//	public void initialize(IndexReader reader, TreeNode caller) throws IOException ;
	
	public SpanStatus nextDoc(TreeNode caller) throws IOException;
	
	/*
	 * return the current span only if currentStatus = available. nextSpan == false <--> currentSpan = null <--> no span is left.
	 */
	public boolean nextSpan(TreeNode caller) throws IOException;
	public Span getCurrentSpan(TreeNode caller);
	
	/**
	 * This is to navigate the inverted list by jumping forward. 
	 * Usage : skipTo(docIdToSkip); docId = getDocID(). Post condition ---> : docId >= docIdToSkip
	 * @return
	 * @throws IOException
	 */
	public SpanStatus skipTo(int docID, TreeNode caller) throws IOException;
	
	public SpanStatus lazySkipTo(int docID, TreeNode caller) throws IOException;
}
