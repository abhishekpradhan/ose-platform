/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;


/**
 * @author Pham Kim Cuong
 *
 */
public interface DocPositionIterator extends DocIterator {
	/**
	 * 
	 * Usage :
	 * while (next()){
	 * 	  do { // --> because next() already call one nextPosition() already. why? --> ConstraintTermWrapper
 	 *       do something with getPosition()
	 *    } while (nextPosition());
	 * }
	 * 
	 */
	public boolean nextPosition() throws IOException;
	
	public boolean skipToPosition(int nextPos) throws IOException;	
	
	/**
	 * Must be called after next() (the first time) or after nextPosition() (the consequent times)
	 * @return
	 * @throws IOException
	 */
	public int getPosition() throws IOException;

	/*
	 * hack : to get termPosition.freq() the total number of entities
	 * for the feature : #satisfied / #total
	 */
	public int getFrequency();
}
