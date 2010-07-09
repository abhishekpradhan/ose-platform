package ose.testing;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import ose.index.FileCorpus;
import ose.index.Utils;
import ose.processor.cascader.RangeConstraint;

/**
 * 
 */

/**
 * @author Pham Kim Cuong
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		String str = "hello\tblah\nhhaha";
		System.out.println(str);
		System.out.println(str.replaceAll("[\\n\\t]", " "));
	}

}
