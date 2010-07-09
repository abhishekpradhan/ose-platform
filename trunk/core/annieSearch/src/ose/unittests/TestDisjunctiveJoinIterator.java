/**
 * 
 */
package ose.unittests;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.DisjunctiveJoinIterator;
import ose.processor.cascader.DocIterator;
import ose.processor.cascader.RangeConstraint;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestDisjunctiveJoinIterator extends TestCase {

	private String doc1 = "" +
			"<html><body>\n" +
			"Canon PowerShot SD1000 7.1MP Digital Elph Camera with 3x Optical Zoom (Silver)\n" +
			"List Price:  	$199.99\n" +
			"Price: 	$156.08\n" +
			"computer laptop camera" + 
			"</body></html>"; 
	
	private String doc2 = "" +
	"<html><body>" +
	"Jiawei Han\n" +
	"Professor, Department of Computer Science\n" +
	"Univ. of Illinois at Urbana-Champaign\n" +
	"Rm 2132, Siebel Center for Computer Science\n" +
	"201 N. Goodwin Avenue\n" +
	"Urbana, IL 61801, USA\n" +
	"E-mail:   hanj[at]cs.uiuc.edu\n" +
	"Ph.D. (1985), Computer Science, Univ. Wisconsin-Madison\n" + 
	"</body></html>"; 
	
	private String [] allDocs = {doc1,doc2};
	
	private String INDEX_PATH = System.getProperty("user.dir") + "/target/indexes/testDisjunctiveJoinIterator";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.setProperty("gate.home", "c:/GATE-4.0");
//		GateDocIndexer indexer = new GateDocIndexer(INDEX_PATH);
//		indexer.annotateStrings(allDocs);		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ose.processor.cascader.ConstraintTermPositionsWrapper#getDocID()}.
	 */
	@Test
	public void testSingleIterator1() throws IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termCanon = new Term("Token", "canon");
		DisjunctiveJoinIterator iterator = new DisjunctiveJoinIterator(Arrays.asList(new DocIterator[] {
				new ConstraintTermPositionsWrapper(termCanon,null, 
						reader.termPositions(termCanon))	
				}));		
		assertTrue(iterator.next());
		assertEquals(0,iterator.getDocID());
		assertFalse(iterator.next());

		Term termJiawei = new Term("Token", "jiawei");
		iterator = new DisjunctiveJoinIterator(Arrays.asList(new DocIterator[] {
				new ConstraintTermPositionsWrapper(termJiawei,null, 
						reader.termPositions(termJiawei))	
				}));		
		assertTrue(iterator.next());
		assertEquals(1, iterator.getDocID());
		assertFalse(iterator.next());
				
		
	}

	/**
	 * Test method for {@link ose.processor.cascader.ConstraintTermPositionsWrapper#next()}.
	 */
	@Test
	public void testDoubleIterator() throws IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termPrice = new Term("Token", "price");
		Term termComputer = new Term("Token", "computer");
		DisjunctiveJoinIterator iterator = new DisjunctiveJoinIterator(Arrays.asList(new DocIterator[] {
				new ConstraintTermPositionsWrapper(termPrice,null, 
						reader.termPositions(termPrice))	,
				new ConstraintTermPositionsWrapper(termComputer,null, 
						reader.termPositions(termComputer))
				}));		
		assertTrue(iterator.next());
		assertEquals(0,iterator.getDocID());
		assertTrue(iterator.next());
		assertEquals(1,iterator.getDocID());
		assertFalse(iterator.next());		

	}
	
	/**
	 * Test method for {@link ose.processor.cascader.DisjunctiveJoinIterator#skipTo()}.
	 */
	@Test
	public void testSkipTo() throws IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termPrice = new Term("Token", "price");
		Term termComputer = new Term("Token", "computer");
		DisjunctiveJoinIterator iterator = new DisjunctiveJoinIterator(Arrays.asList(new DocIterator[] {
				new ConstraintTermPositionsWrapper(termPrice,null, 
						reader.termPositions(termPrice))	,
				new ConstraintTermPositionsWrapper(termPrice,null, 
						reader.termPositions(termComputer))
				}));		
		assertTrue(iterator.skipTo(0));
		assertEquals(0,iterator.getDocID());
		assertTrue(iterator.skipTo(0));
		assertEquals(1,iterator.getDocID());
		assertFalse(iterator.skipTo(0));		

		iterator = new DisjunctiveJoinIterator(Arrays.asList(new DocIterator[] {
				new ConstraintTermPositionsWrapper(termPrice,null, 
						reader.termPositions(termPrice))	,
				new ConstraintTermPositionsWrapper(termPrice,null, 
						reader.termPositions(termComputer))
				}));		
		assertTrue(iterator.skipTo(1));
		assertEquals(1,iterator.getDocID());
		assertFalse(iterator.skipTo(1));
	}
	
	/**
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private void printPositions(IndexReader reader) throws IOException {
		Term term = new Term("Annotation", "number");
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(term,null, 
				reader.termPositions(term));
		while (wrapper.next()){
			System.out.print("Document " + wrapper.getDocID() + " :");
			do {
				System.out.print("\t" + wrapper.getPosition());
			} while (wrapper.nextPosition());
			System.out.println();
		}
	}

}
