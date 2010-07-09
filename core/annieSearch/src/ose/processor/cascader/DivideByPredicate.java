package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class DivideByPredicate extends BaseQueryPredicate {
	
	protected DivideByFeatureIterator divideByIterator;
	protected QueryPredicate childPredicate1, childPredicate2;
	
	public DivideByPredicate(String id, QueryPredicate child1, QueryPredicate child2) {
		super(id);
		childPredicate1 = child1;
		childPredicate2 = child2;
		divideByIterator = null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (divideByIterator == null){
			divideByIterator = new DivideByFeatureIterator(
					(DocFeatureIterator) childPredicate1.getInvertedListIterator(reader),
					(DocFeatureIterator) childPredicate2.getInvertedListIterator(reader)
					);
		}
		return divideByIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "DivideByPredicate\n"); 
		buffer.append(childPredicate1.toString(level + 1));
		buffer.append("\n");
		buffer.append(childPredicate2.toString(level + 1));
		buffer.append("\n");
		return buffer.toString();
	}

}
