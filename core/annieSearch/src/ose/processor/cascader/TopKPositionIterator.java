package ose.processor.cascader;

import java.io.IOException;


/*
 * TODO : make this an abstract class for all classes that "shadows" another one
 */
public class TopKPositionIterator implements DocPositionIterator {

	protected int topK;
	protected DocPositionIterator childIter;
	protected int currentPos;
	
	public TopKPositionIterator (
			DocPositionIterator iter, 
			int topK) throws IOException {
		this.topK = topK;
		childIter = iter;
	}
	
	public boolean next() throws IOException {
		currentPos = 1; //since getPosition is called before nextPosition. 
		return childIter.next();
	}
	
	public boolean skipTo(int docID) throws IOException {
		currentPos = 1; //since getPosition is called before nextPosition.
		return childIter.skipTo(docID);
	}
	
	public int getFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean nextPosition() throws IOException {
		if (currentPos < topK){
			currentPos += 1;
			return childIter.nextPosition();
		}
		else
			return false;
	}

	public int getDocID() {
		return childIter.getDocID();
	}
	
	public String getClue() {
		return childIter.getClue();
	}
	
	public int getPosition() throws IOException {
		return childIter.getPosition();
	}

	public boolean skipToPosition(int nextPos) throws IOException {
		boolean skipOnce = false;
		while (childIter.getPosition() < nextPos){
			skipOnce = true;
			if (! nextPosition() ) return false; 
		}
		if (!skipOnce)
			return nextPosition();
		else
			return true;
	}
	
}
