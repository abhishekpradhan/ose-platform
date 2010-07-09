package ose.processor.cascader;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;


public class TextTermPositionsWrapper extends TermPositionsWrapper {

	public TextTermPositionsWrapper(Term term, TermPositions positions) {		
		super(term, positions);
	}
	
	public String getClue() {
		return theTerm + "@" + nextPositionCache;
	}

}
