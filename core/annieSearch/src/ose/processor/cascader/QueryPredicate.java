package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;


public interface QueryPredicate {
	public String toString(int level) ;
	public DocIterator getInvertedListIterator(IndexReader reader) throws IOException;
	public String getID();
}
