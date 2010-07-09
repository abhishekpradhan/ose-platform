package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import ose.index.Utils;

public class NullPredicate extends BaseQueryPredicate {

	public DocIterator getInvertedListIterator(IndexReader reader)
			throws IOException {
		return null;
	}

	public String toString(int level) {
		return "NullPredicate";
	}

}
