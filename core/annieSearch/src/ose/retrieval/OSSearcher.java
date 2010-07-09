/**
 * 
 */
package ose.retrieval;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.BooleanQuery;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.OSHits;
import ose.processor.cascader.WeightedFeatureQuery;

/**
 * @author Pham Kim Cuong
 *
 */
public class OSSearcher {
	
	public static final boolean bDEBUG = true;
	public String indexDir;
	Analyzer analyzer = new StandardAnalyzer();
	private IndexReader reader ;
	
	public OSSearcher(String directory) {
		indexDir = directory;
	}
	
	public String getIndexDir() {
		return indexDir;
	}
	
	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
	
	public IndexReader getReader(){
		return reader;
	}
	
	public OSHits booleanSearch(String queryString) throws IOException, InstantiationException, IllegalAccessException{
		reader = IndexReader.open(indexDir);
		OSQueryParser parser = new OSQueryParser();
		BooleanQuery osQuery = parser.parseBooleanQuery(queryString);
		return osQuery.search(reader);
	}
	

	public OSHits featureSearch(String queryString) throws IOException, InstantiationException, IllegalAccessException{
		reader = IndexReader.open(indexDir);
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery osQuery = parser.parseFeatureQuery(queryString);
		if (bDEBUG) System.out.println("Feature tree : \n" + osQuery.toString());
		return osQuery.search(reader);
	}
	
	public OSHits weightedFeatureSearch(String queryString) throws IOException, InstantiationException, IllegalAccessException{
		reader = IndexReader.open(indexDir);
		OSQueryParser parser = new OSQueryParser();
		WeightedFeatureQuery osQuery = parser.parseWeightedFeatureQuery(queryString);
		if (bDEBUG) System.out.println("Feature tree : \n" + osQuery.toString());
		return osQuery.search(reader);
	}
	
	public OSHits featureFill(String queryString) throws IOException, InstantiationException, IllegalAccessException{
		reader = IndexReader.open(indexDir);
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery osQuery = parser.parseFeatureQuery(queryString);
		return osQuery.fillFeatures(reader);
	}
}
