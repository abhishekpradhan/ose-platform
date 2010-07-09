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

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.processor.Span;
import ose.processor.TreeNode;
import ose.processor.splock.SPLockParser;
import ose.processor.splock.LuceneTermNode;

/**
 * @author Pham Kim Cuong
 *
 */
public class CommonTests extends TestCase {
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
	public void testShareSkipToTests() throws IOException {
		Map<String, List<Span>> shareSkipToNodeTests = new HashMap<String, List<Span>>();
		shareSkipToNodeTests.put("And(Token(camera),Number_body()) And(Token(display),Number_body())", Arrays.asList(new Span[]{
				new Span(0,2,3),
				new Span(2,0,1),
				new Span(4,1,2),
				new Span(6,0,1),
				new Span(6,1,2),
		}));
		
		callNextToAndCheckSpans(UTestData.indexPath2, shareSkipToNodeTests);
	}
	
	

	/**
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	static public void callNextToAndCheckSpans(String indexPath,  Map<String, List<Span>> testData) throws CorruptIndexException,
			IOException {
 		IndexReader reader = IndexReader.open(indexPath);
		
		for (String query : testData.keySet()){
			SPLockParser parser = new SPLockParser();
			TreeNode node = parser.parse(query) ;
			System.out.println("Testing node " + node );
			node.initialize(reader);
			int i = 0;
			List<Span> answers = testData.get(query);
			while (node.nextDoc().isAvailable()){
				do {
					assertTrue("Testing query " + query + " , span " + i + ", too many answers ", i < answers.size());
					Span span = node.getCurrentSpan();
					span.setAttributes(null); //don't care about attributes
					assertEquals("Testing query " + query + " , span " + i ,  answers.get(i) ,span );
					i ++;
				} while (node.nextSpan());
			}
			assertEquals("Produced less spans than expected [" + query + "]", answers.size(), i );
		}
	}
	
	private void printSpans(String indexPath, String query) throws IOException {
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

	/**
	 * @param reader
	 * @param query
	 * @param node
	 * @throws IOException
	 */
	static void checkSpansByCallingNodeNextTo(IndexReader reader,
			String query, LuceneTermNode node, List<Span> answers ) throws IOException {
		System.out.println("Testing node " + node );
		node.initialize(reader);
		int i = 0;
		
		while (node.nextDoc().isAvailable()){
			do {
				assertTrue( i < answers.size());
				Span span = node.getCurrentSpan();
				span.setAttributes(null); //don't care about attributes
				assertEquals("Testing query " + query + " , span " + i ,  answers.get(i), span );
				i++;
			} while (node.nextSpan());
		}
	}
	
	static void checkSpansWithAttributesByCallingNodeNextTo(IndexReader reader,
			String query, LuceneTermNode node, List<Span> answers ) throws IOException {
		System.out.println("Testing node " + node );
		node.initialize(reader);
		int i = 0;
		
		while (node.nextDoc().isAvailable()){
			do {
				assertTrue( i < answers.size());
				Span span = node.getCurrentSpan();
				assertEquals("Testing query " + query + " , span " + i ,  answers.get(i), span );
				i++;
			} while (node.nextSpan());
		}
	}

}
