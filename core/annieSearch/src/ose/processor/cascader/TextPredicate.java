package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.utils.CommonUtils;

public class TextPredicate extends  BaseQueryPredicate {
	protected String entityName;
	List<String> listOfTerms;
	
	DocPositionIterator termCombiterator;
	
	public TextPredicate(String entity, List<String> terms ){
		super(entity);
		entityName = entity;
		//TODO : filter duplicates
		listOfTerms = terms;
		termCombiterator = null;
	}
	
	public DocPositionIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (termCombiterator == null){
			List<DocPositionIterator> iterList = new ArrayList<DocPositionIterator>();
			for (String termText : listOfTerms) {
				Term term = new Term(entityName, termText);
//				System.out.println("AAA = new term " + entityName + " "  + termText + " " +term );
				if (termText == null){ //empty text like Token()
					iterList.add( new EmptyDocPositionIterator());
				}
				else{
					iterList.add( new TextTermPositionsWrapper(term,reader.termPositions(term)));
				}
			}
			if (iterList.size() == 0)
				return new EmptyDocPositionIterator();
			else if (iterList.size() == 1)
				return iterList.get(0);
			else
				termCombiterator = new DisjunctivePositionIterator(iterList);
		}
		return termCombiterator;
	}
	
	public String toString(int level) {
		return CommonUtils.tabString(level) + "TextPredicate:" + entityName + " | " + listOfTerms.toString();
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
}
