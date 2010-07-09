package ose.testing;

import org.apache.lucene.index.IndexReader;

import ose.parser.OSQueryParser;
import ose.processor.cascader.BooleanQuery;
import ose.processor.cascader.DocIterator;

public class TestAndPredicate {
	public static void main(String[] args) throws Exception{
		String INDEX_PATH = "D:\\My Documents\\PhD\\Research\\ObjectSearch\\svn\\GATE\\workspace\\data\\output\\index\\vldb";
//		String query = "Phrase(Token(price) Number(_range(0,300) ) ) ";
		String query = "And(Token(nikon) Token(price) Number(_range(200,300) ) )";
//		String query = "%CountNumber(Phrase(Token(price) Number(_range(0,300) ) ) )";
		IndexReader reader = IndexReader.open(INDEX_PATH);
		OSQueryParser parser = new OSQueryParser();
		BooleanQuery osQuery = parser.parseBooleanQuery(query);
		DocIterator iterator = (DocIterator ) osQuery.getPredicates().get(0).getInvertedListIterator(reader);
		int count = 0;
		while (iterator.next()){
			System.out.println("Doc : " + iterator.getDocID() + " , clue : " + iterator.getClue());
			count ++;
		}
		System.out.println("Done " + count);
	}
}
