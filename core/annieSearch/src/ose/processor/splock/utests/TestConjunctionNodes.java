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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.parser.QueryTreeParser;
import ose.processor.Span;
import ose.processor.splock.LuceneTermNode;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestConjunctionNodes extends TestCase {
	static boolean indexCreated = false;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		UTestData.makeSureIndexExists();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLeftJoinOnCollection1() throws IOException {
		Map<String, List<Span>> conjunctiveNodeTests = new HashMap<String, List<Span>>();
		conjunctiveNodeTests.put("AndL(Token(display),Number_body())", Arrays.asList(new Span[]{
				new Span(0,2,3),
				new Span(2,0,1),
				new Span(6,0,1),
				}));
		conjunctiveNodeTests.put("AndL(Token(screen),Token(size))", Arrays.asList(new Span[]{
				new Span(0,0,1),
				}));
		CommonTests.callNextToAndCheckSpans(UTestData.indexPath1, conjunctiveNodeTests);
	}
	
	@Test
	public void testLeftJoinOnCollection4() throws IOException {
		Map<String, List<Span>> conjunctiveNodeTests = new HashMap<String, List<Span>>();
		conjunctiveNodeTests.put("AndL(Token(camera),Number_body())", Arrays.asList(new Span[]{
				new Span(0,2,5),
				new Span(4,1,2),
				new Span(6,1,2),
				}));
		CommonTests.callNextToAndCheckSpans(UTestData.indexPath4, conjunctiveNodeTests);
	}
}
