/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

import common.profiling.Profile;


/**
 * Pay attention to getPosition usage
 * @author Pham Kim Cuong
 *
 */
public abstract class TermPositionsWrapper implements DocPositionIterator {
	protected TermPositions termPositions;
	protected int currentPosition;
	protected int nextPositionCache;
	protected Term theTerm;
	
	protected Profile profiler = Profile.getProfile("IOcost");
	protected Profile nextCounter = Profile.getProfile("IONextCount");
	protected Profile skipToCounter = Profile.getProfile("IOSkipToCount");
	protected Profile nextPosCounter = Profile.getProfile("IONextPosCount");
	
	public TermPositionsWrapper(Term term, TermPositions positions) {		
		termPositions = positions;
		currentPosition = -1;
		theTerm = term;
	}
	
	public int getDocID() {
		return termPositions.doc();
	}

	public boolean next() throws IOException {
		profiler.start();
		currentPosition = -1;
		nextCounter.increment();
		boolean res = termPositions.next();
		profiler.end();
		return res;
	}

	public boolean skipTo(int docID) throws IOException{
		profiler.start();
		currentPosition = -1;
		skipToCounter.increment();
		boolean res = termPositions.skipTo(docID);
		profiler.end();
		return res;
	}
	
	public int getPosition() throws IOException{
		if (currentPosition == -1){
			if (!nextPosition()){
				System.err.println("Wierd: no position found in document " + getDocID());
			}
		}
		return nextPositionCache;
	}
	
	public boolean nextPosition() throws IOException {		
		nextPositionCache = -1;
		currentPosition += 1;
		if ( currentPosition < termPositions.freq() ){
			nextPosCounter.increment();
			nextPositionCache = termPositions.nextPosition();
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean skipToPosition(int nextPos) throws IOException {
		do {
			if (!nextPosition()) return false;
		} while (nextPositionCache < nextPos);
		return true ;
	}
	
	public int getFrequency() {
		return termPositions.freq();
	}
}
