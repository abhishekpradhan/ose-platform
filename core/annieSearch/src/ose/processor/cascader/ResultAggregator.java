/**
 * 
 */
package ose.processor.cascader;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

/**
 * @author Pham Kim Cuong
 *
 */
public interface ResultAggregator {
	public void aggregateResult(IndexReader reader) throws IOException;
}
