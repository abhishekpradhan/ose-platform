/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;

/**
 * @author Pham Kim Cuong
 *
 */
public interface DocIterator {
	
	/**
	 * This is to navigate the inverted list one by one
	 * @return
	 * @throws IOException
	 */
	public boolean next() throws IOException;
	
	/**
	 * This is to navigate the inverted list by jumping forward. 
	 * Usage : skipTo(docIdToSkip); docId = getDocID(). Post condition ---> : docId >= docIdToSkip
	 * @return
	 * @throws IOException
	 */
	public boolean skipTo(int docID) throws IOException;
	
	/**
	 * return the current doc id from the inverted list. 
	 * @return
	 */
	public int getDocID();
	
	
	/**
	 * This is for search interface to extract clues/snippets from documents
	 * @return string contains the snippet
	 */
	public String getClue();
	
}
