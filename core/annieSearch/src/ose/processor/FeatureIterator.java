package ose.processor;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;


public interface FeatureIterator {
	/*
	 * methods for processing tree
	 */
	
	public void initialize(IndexReader reader) throws IOException;
	
	/**
	 * This is to navigate the inverted list one by one
	 * @return SpanResult.STATUS_AVALABLE
	 * 			SpanResult.STATUS_ON_HOLD
	 *         SpanResult.STATUS_INVALID : no more is left  
	 * @throws IOException
	 */
	public SpanStatus nextDoc() throws IOException;
	
	/*
	 * return the current span only if currentStatus = available. nextSpan == false <--> currentSpan = null <--> no span is left.
	 */
	public boolean nextSpan() throws IOException;
	
	/**
	 * This is to navigate the inverted list by jumping forward. 
	 * Usage : skipTo(docIdToSkip); docId = getDocID(). Post condition ---> : docId >= docIdToSkip
	 * @return
	 * @throws IOException
	 */
	public SpanStatus skipTo(int docID) throws IOException;
	
	/**
	 * This is to navigate the inverted list by jumping forward.
	 * However, it only attempts to jump to docID, without going any further.
	 * If docId has a span, it return STATUS_AVAILABLE, if not, it returns STATUS_NO_MORE_SPAN 
	 * Usage : lazySkipTo(docIdToSkip); docId = getDocID(). Post condition ---> : docId >= docIdToSkip
	 * @return
	 * @throws IOException
	 */
	public SpanStatus lazySkipTo(int docID) throws IOException;
	
	/**
	 * return the current span, or holding doc_id, or an invalid status meaning no more span is left in the current doc id. 
	 * @return
	 */
	public SpanStatus getCurrentStatus();
	public Span getCurrentSpan();
	
	/**
	 * This is for search interface to extract clues/snippets from documents. Mostly for debugging 
	 * @return string contains the snippet
	 */
	public String getClue();

}
