/**
 * 
 */
package ose.processor.splock.utests;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.index.TrecIndexer;
import ose.parser.ParentNode;
import ose.parser.QueryTreeParser;
import ose.processor.Span;
import ose.processor.TreeNode;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.RangeConstraint;
import ose.processor.splock.SPLockParser;
import ose.processor.splock.DisjunctiveNode;
import ose.processor.splock.LuceneTermNode;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestCollection1 extends TestCase {
	static boolean indexCreated = false;
	private String [] allDocs = UTestData.col1_docs;
	private Map<String, List<Span>> spanTests;
	private Map<String, List<Span>> tokenAfterNumberTest;
	
	
	private String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/collection1";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (!indexCreated ){
			UTestData.createIndex(allDocs, indexPath);
			indexCreated = true;
		}
		
		spanTests = new HashMap<String, List<Span>>();
		spanTests.put("Token(screen)", Arrays.asList(new Span[]{
				new Span(0,0,0),
				new Span(1,0,0),
				new Span(3,0,0)
				}));
		spanTests.put("Token(size)", Arrays.asList(new Span[]{
				new Span(0,1,1),
				new Span(4,0,0),
				}));
		spanTests.put("Number_body()", Arrays.asList(new Span[]{
				new Span(0,2,2),
				new Span(2,0,0),
				new Span(4,1,1),
				new Span(6,1,1),
				}));
		spanTests.put("Token(display)", Arrays.asList(new Span[]{
				new Span(0,3,3),
				new Span(1,1,1),
				new Span(2,1,1),
				new Span(5,0,0),
				new Span(6,0,0),
				}));
		
		tokenAfterNumberTest = new HashMap<String, List<Span>>();
		tokenAfterNumberTest.put("Token(display)", Arrays.asList(new Span[]{
				new Span(0,3,3),
				new Span(1,1,1),
				new Span(2,1,1),
				new Span(5,0,0),
				new Span(6,0,0),
				}));
		
		
//		printSpans("And(Token(screen),Token(size))");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 */
	@Test
	public void testGetSpans() throws IOException {
		IndexReader reader = IndexReader.open(indexPath);
		
		for (String query : spanTests.keySet()){
			LuceneTermNode node = new LuceneTermNode();
			QueryTreeParser parser = new QueryTreeParser(query);
			node.parse( parser.parseQueryNode().getFirstChild() , new SPLockParser());
			CommonTests.checkSpansByCallingNodeNextTo(reader, query, node, spanTests.get(query));
		}
	}

	/**
	 */
	@Test
	public void testTokenAfterNumberTests() throws IOException {
		IndexReader reader = IndexReader.open(indexPath);
		
		for (String query : tokenAfterNumberTest.keySet()){
			LuceneTermNode node = new LuceneTermNode();
			QueryTreeParser parser = new QueryTreeParser(query);
			node.parse( parser.parseQueryNode().getFirstChild() , new SPLockParser());
			System.out.println("Testing node " + node );
			node.initialize(reader);
			int i = 0;
			List<Span> answers = tokenAfterNumberTest.get(query);
			while (node.nextDoc().isAvailable()){
				do {
					assertTrue( i < answers.size());
					Span span = node.getCurrentSpan();
					span.setAttributes(null); //don't care about attributes
					assertEquals("Testing query " + query + " , span " + i ,  span, answers.get(i) );
					i ++;
				} while (node.nextSpan());
			}
		}
	}
	
	@Test
	public void testDisjunctiveTests() throws IOException {
		Map<String, List<Span>> disjunctiveNodeTests = new HashMap<String, List<Span>>();
		disjunctiveNodeTests.put("Token(screen) Token(size)", Arrays.asList(new Span[]{
				new Span(0,0,0),
				new Span(0,1,1),
				new Span(1,0,0),
				new Span(3,0,0),
				new Span(4,0,0),
				}));
		CommonTests.callNextToAndCheckSpans(indexPath, disjunctiveNodeTests);
	}
	
	@Test
	public void testConjunctiveTests() throws IOException {
		Map<String, List<Span>> conjunctiveNodeTests = new HashMap<String, List<Span>>();
		conjunctiveNodeTests.put("And(Token(display),Number_body())", Arrays.asList(new Span[]{
				new Span(0,2,3),
				new Span(2,0,1),
				new Span(6,0,1),
				}));
		conjunctiveNodeTests.put("And(Token(screen),Token(size))", Arrays.asList(new Span[]{
				new Span(0,0,1),
				}));
		CommonTests.callNextToAndCheckSpans(indexPath, conjunctiveNodeTests);
	}
	
	@Test
	public void testShareSkipToTests() throws IOException {
		Map<String, List<Span>> shareSkipToNodeTests = new HashMap<String, List<Span>>();
		shareSkipToNodeTests.put("And(Token(size),Number_body()) And(Token(display),Number_body())", Arrays.asList(new Span[]{
				new Span(0,1,2),
				new Span(0,2,3),
				new Span(2,0,1),
				new Span(4,0,1),
				new Span(6,0,1),
		}));
		CommonTests.callNextToAndCheckSpans(indexPath, shareSkipToNodeTests);
	}
	
	private void printSpans(String query) throws IOException {
		System.out.println("======== IGNORE THIS =================");
		IndexReader reader = IndexReader.open(indexPath);
		
		
		SPLockParser parser = new SPLockParser();
		TreeNode node = parser.parse(query) ;
		System.out.println("Testing node " + node );
		node.initialize(reader);
		while (node.nextDoc().isAvailable()){
			do {
				Span span = node.getCurrentSpan();
				System.out.println(span);
			} while (node.nextSpan());
		}
		System.out.println("======================================");
	}

}
