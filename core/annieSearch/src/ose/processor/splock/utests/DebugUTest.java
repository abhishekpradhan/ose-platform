package ose.processor.splock.utests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;

import ose.processor.Span;
import ose.processor.TreeNode;
import ose.processor.splock.BaseTreeNode;
import ose.processor.splock.SPLockParser;
import ose.processor.splock.SPLockProcessor;

public class DebugUTest {
	
	private void debug() throws Exception{
		String testResultFile = "utests/Test7.xml";
		TestResultReader testReader = new TestResultReader(testResultFile);
		
		IndexReader reader = IndexReader.open(testReader.getIndex());
		System.out.println("Testing " + testResultFile);
		System.out.println("Index : " + testReader.getIndex());
		String query  = testReader.getFeatureString();
		System.out.println("Query : " + query);
		
		SPLockParser parser = new SPLockParser();
		TreeNode node = parser.parse(query) ;
		System.out.println("Testing node " + node );
		
		SPLockProcessor processor = new SPLockProcessor(reader);
		processor.initialize( (BaseTreeNode) node);
		while (processor.hasNext()){
			Span span = processor.firstSpan();
			while (span != null) {
				Span correctSpan = testReader.nextSpan();
				System.out.println("------- " + span + "  ------- " + correctSpan);
				Span nextSpan = processor.nextSpan();
				while (nextSpan != null && nextSpan.docId == span.docId && nextSpan.startPos == span.startPos){
					nextSpan = processor.nextSpan();
				}
				span = nextSpan;
			}
		}
		reader.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		DebugUTest prog = new DebugUTest();
		prog.debug();
	}

}
