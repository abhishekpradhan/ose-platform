package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import ose.index.IndexFieldConstant;
import ose.parser.OSQueryParser;
import ose.utils.CommonUtils;

public class NumberPredicate extends BaseQueryPredicate {
	protected String entityName;
	protected Constraint postingConstraint; // constraint/filter for each posting in the inverted index
	protected DocPositionIterator iterator;
	
	public NumberPredicate(String idString, String entity, Constraint constraint) {
		super(idString);
		entityName = entity;
		postingConstraint = constraint;
		iterator = null;
	}
	
	public DocPositionIterator getInvertedListIterator(IndexReader reader) throws IOException{
		if (iterator == null){
			Term term ;
			if (entityName.equals(OSQueryParser.PRED_NUMBER_TITLE)){
				term = new Term(IndexFieldConstant.FIELD_HTMLTITLE,IndexFieldConstant.TERM_NUMBER);
			}
			else { //Number_body
				term = new Term(IndexFieldConstant.FIELD_BODY,IndexFieldConstant.TERM_NUMBER);
			}
			
			iterator = new ConstraintTermPositionsWrapper(term, postingConstraint, reader.termPositions(term) );
		}
		return iterator;
	}
	
	public String toString(int level) {
		return CommonUtils.tabString(level) + "NumberPredicate:" + entityName + " | " + postingConstraint;
	}
	
}
