/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

import common.profiling.Profile;

import ose.index.Utils;

/**
 * Pay attention to getPosition usage
 * @author Pham Kim Cuong
 *
 */
public class ConstraintTermPositionsWrapper extends TermPositionsWrapper{
	protected Constraint constraint;
	
	private String theClue;
	
	public ConstraintTermPositionsWrapper(Term term, Constraint constraint, TermPositions positions) {
		super(term, positions);
		this.constraint = constraint;
		nextPositionCache = -1;
		currentPosition = 0;
	}
	
	@Override
	public boolean next() throws IOException {
		profiler.start();		
		currentPosition = 0;
		nextPositionCache = -1;
		nextCounter.increment();
		while ( termPositions.next() ){			
			currentPosition = 0;
			nextPositionCache = -1;
			if (nextPosition() ){
				profiler.end();
				return true;
			}
			nextCounter.increment();
		}
		profiler.end();
		return false;
	}
	
	private boolean checkConstraintOnCurrentTermPositions()throws IOException  {
		nextPosCounter.increment();
		nextPositionCache = termPositions.nextPosition();
		double value = Utils.getPayloadNumber(termPositions);
		theClue = value + "@" + nextPositionCache;
		if (constraint == null){
			return true;			//empty constraint --> satisfied.
		}
		else{
			if (constraint.satisfy(value)){
				return true; 
			}
			nextPositionCache = -1;
			return false;
		}
	}

	@Override
	public boolean skipTo(int docID) throws IOException{
		profiler.start();
		currentPosition = 0;
		nextPositionCache = -1;
		skipToCounter.increment();
		while (termPositions.skipTo(docID)){
			currentPosition = 0;
			nextPositionCache = -1;
			if ( nextPosition() ){
				profiler.end();
				return true;
			}
			skipToCounter.increment();
		}
		profiler.end();
		return false;
	}
	
	@Override
	public int getPosition() throws IOException{
		return nextPositionCache ;
	}
	
	@Override
	public boolean nextPosition() throws IOException {
		nextPositionCache = -1;
		while ( currentPosition++ < termPositions.freq() ){
			if (checkConstraintOnCurrentTermPositions()){
				return true;
			}
		}
		return false;
	}
	
	public boolean skipToPosition(int nextPos) throws IOException {
		do {
			if (!nextPosition()) {
				return false;
			}
		} while (nextPositionCache < nextPos);
		return true ;
	}
	
	public String getClue() {
		return theClue;
	}
}
