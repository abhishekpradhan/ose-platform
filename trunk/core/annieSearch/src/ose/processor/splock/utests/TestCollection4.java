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
public class TestCollection4 extends TestCase {
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
	public void testPhraseNodeWithBufferedSpanTests() throws IOException {
		Map<String, List<Span>> phraseNodeTests = new HashMap<String, List<Span>>();
//		phraseNodeTests.put("Phrase(Token(display),Number_body()) Phrase(Number_body(),Token(camera))", Arrays.asList(new Span[]{
//				new Span(0,3,4),
//				new Span(0,4,5),
//				new Span(4,1,2),
//				new Span(4,4,5),
//				new Span(6,0,1),
//				new Span(6,1,2),
//				}));
		CommonTests.callNextToAndCheckSpans(UTestData.indexPath4, phraseNodeTests);
	}
	
	@Test
	public void testProximityNode() throws IOException {
		Map<String, List<Span>> proximityNodeTests = new HashMap<String, List<Span>>();
		proximityNodeTests.put("Proximity(Token(camera),Number_body(),-1,1)", Arrays.asList(new Span[]{
				new Span(0,4,5),
				new Span(4,1,2),
				new Span(6,1,2),
				}));
		CommonTests.callNextToAndCheckSpans(UTestData.indexPath4, proximityNodeTests);
	}
	
	@Test
	public void testPhraseNodeTests() throws IOException {
		Map<String, List<Span>> phraseNodeTests = new HashMap<String, List<Span>>();
		phraseNodeTests.put("Phrase(Number_body(),Token(size))", Arrays.asList(new Span[]{				
				new Span(2,1,2),
				new Span(2,5,6),
		}));
		phraseNodeTests.put("Phrase(camera digital)", Arrays.asList(new Span[]{
				new Span(3,1,2),
		}));
		
		CommonTests.callNextToAndCheckSpans(UTestData.indexPath4, phraseNodeTests);
	}
	
}
