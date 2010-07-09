package ose.processor.cascader;

import java.io.IOException;


/**
 * An empty list, every is null. This is to serve when a feature is empty, like Token() --> Token must take some terms. 
 * @author Pham Kim Cuong
 *
 */
public class EmptyDocPositionIterator implements DocPositionIterator {

	public EmptyDocPositionIterator(){
	}

	public int getFrequency() {
		return 0;
	}

	public int getPosition() throws IOException {
		return -1;
	}

	public boolean nextPosition() throws IOException {
		return false;
	}

	public boolean skipToPosition(int nextPos) throws IOException {
		return false;
	}

	public String getClue() {
		return null;
	}

	public int getDocID() {
		return -1;
	}

	public boolean next() throws IOException {
		return false;
	}

	public boolean skipTo(int docID) throws IOException {
		return false;
	}

}
