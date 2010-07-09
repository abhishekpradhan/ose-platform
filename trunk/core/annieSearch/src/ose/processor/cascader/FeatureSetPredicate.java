/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;

import ose.utils.CommonUtils;

/**
 * @author Pham Kim Cuong
 *
 */
public class FeatureSetPredicate extends BaseQueryPredicate {

	private List<QueryPredicate> featureGenerators;
	
	private CompositeFeatureIterator iterator;
	public FeatureSetPredicate(String id, List<QueryPredicate> generators) {
		super(id);
		featureGenerators = generators;
		
	}
	
	/* (non-Javadoc)
	 * @see ose.query.QueryPredicate#getInvertedListIterator(org.apache.lucene.index.IndexReader)
	 */
	public DocIterator getInvertedListIterator(IndexReader reader)
			throws IOException {
		if (iterator == null){
			List<DocFeatureIterator> composingFeatureIterators = new ArrayList<DocFeatureIterator>();
			for (QueryPredicate pred : featureGenerators) {
				composingFeatureIterators.add((DocFeatureIterator)pred.getInvertedListIterator(reader));
			}
			iterator = new CompositeFeatureIterator(composingFeatureIterators);
		}
		return iterator;
	}

	/* (non-Javadoc)
	 * @see ose.query.QueryPredicate#toString(int)
	 */
	public String toString(int level) {
		return CommonUtils.tabString(level) + "%FeatureSet[" + featureGenerators + "]";
	}

}
