package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

public class LogFeaturePredicate extends BaseQueryPredicate {

	protected UnaryFunctionFeatureIterator logIterator;
	protected QueryPredicate childPredicate;
	
	public LogFeaturePredicate(String id, QueryPredicate child) {
		super(id);
		childPredicate = child;
		logIterator = null;
	}
	
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException {
		if (logIterator == null){
			logIterator = new UnaryFunctionFeatureIterator(UnaryFunctionFeatureIterator.FUNC_LOG, null,
					(DocFeatureIterator) childPredicate.getInvertedListIterator(reader)	, reader				
					);
		}
		return logIterator;
	}
	
	public String toString(int level) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( CommonUtils.tabString(level) + "LogPredicate\n"); 
		buffer.append(childPredicate.toString(level + 1));
		buffer.append("\n");
		return buffer.toString();
	}

}
