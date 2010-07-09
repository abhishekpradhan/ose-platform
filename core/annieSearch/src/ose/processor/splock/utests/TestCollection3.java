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
import ose.processor.splock.SPLockParser;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestCollection3 extends TestCase {
	static boolean indexCreated = false;
	private String [] allDocs = UTestData.col3_docs;
	
	private String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/collection3";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (!indexCreated ){
			UTestData.createIndex(allDocs, indexPath);
			indexCreated = true;
		}
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
		phraseNodeTests.put("Phrase(Token(size),Number_body()) Phrase(Token(display),Number_body()) ", Arrays.asList(new Span[]{
				new Span(0,1,2),
				new Span(0,3,4),
				new Span(4,0,1),
				new Span(4,4,5),
				new Span(6,0,1),
				}));
		phraseNodeTests.put("Phrase(Token(size),Number_body()) Phrase(Token(display),Number_body()) Phrase(Token(camera),Number_body())", Arrays.asList(new Span[]{
				new Span(0,1,2),
				new Span(0,3,4),
				new Span(0,5,6),
				new Span(4,0,1),
				new Span(4,2,3),
				new Span(4,4,5),
				new Span(6,0,1),
				}));
		phraseNodeTests.put("Phrase(Token(display),Number_body()) Phrase(Number_body(),Token(camera))", Arrays.asList(new Span[]{
				new Span(0,3,4),
				new Span(0,4,5),
				new Span(4,1,2),
				new Span(4,4,5),
				new Span(6,0,1),
				new Span(6,1,2),
				}));
		CommonTests.callNextToAndCheckSpans(indexPath, phraseNodeTests);
	}
	
	@Test
	public void testPhraseNodeWithSelectionTests() throws IOException {
		Map<String, List<Span>> phraseNodeTests = new HashMap<String, List<Span>>();
		phraseNodeTests.put("Phrase(Token(display),Number_body(_range(25,40)))", Arrays.asList(new Span[]{
				new Span(4,4,5),
				}));
		phraseNodeTests.put("Phrase(Token(size),Number_body()) Phrase(Token(display),Number_body(_range(25,40))) Phrase(Token(camera),Number_body())", Arrays.asList(new Span[]{
				new Span(0,1,2),
				new Span(0,5,6),
				new Span(4,0,1),
				new Span(4,2,3),
				new Span(4,4,5),
				}));
		
		
		CommonTests.callNextToAndCheckSpans(indexPath, phraseNodeTests);
	}
	
	@Test
	public void testSpanWithAttributes() throws IOException {
		
		Map<String, List<Span>> spanWithAttributeTests = new HashMap<String, List<Span>>();
		
		spanWithAttributeTests.put("Number_body())", Arrays.asList(new Span[]{
				new Span(0,2,2, oneEntryMap("_numeric", 10d)),
				new Span(0,4,4, oneEntryMap("_numeric", 20d)),
				new Span(0,6,6, oneEntryMap("_numeric", 20d)),
				new Span(2,0,0, oneEntryMap("_numeric", 14d)),
				new Span(4,1,1, oneEntryMap("_numeric", 14d)),
				new Span(4,3,3, oneEntryMap("_numeric", 20d)),
				new Span(4,5,5, oneEntryMap("_numeric", 30d)),
				new Span(6,1,1, oneEntryMap("_numeric", 10d)),
				}));
		
		IndexReader reader = IndexReader.open(indexPath);
		
		for (String query : spanWithAttributeTests.keySet()){
			LuceneTermNode node = new LuceneTermNode();
			QueryTreeParser parser = new QueryTreeParser(query);
			node.parse( parser.parseQueryNode().getFirstChild(), new SPLockParser() );
			CommonTests.checkSpansWithAttributesByCallingNodeNextTo(reader, query, node, spanWithAttributeTests.get(query));
		}
	}
	
	private Map<String, Object> oneEntryMap(String key, Object value){
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(key, value);
		return res;
	}
	
}
