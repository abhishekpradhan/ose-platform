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

public class DebugUTestSimple {
	
	private void debug() throws Exception{
		TestCollection1 test = new TestCollection1();
		test.setUp();
		test.testDisjunctiveTests();
		test.tearDown();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		DebugUTestSimple prog = new DebugUTestSimple();
		prog.debug();
	}

}
