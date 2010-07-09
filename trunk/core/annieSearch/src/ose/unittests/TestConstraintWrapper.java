/**
 * 
 */
package ose.unittests;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.index.TrecIndexer;
import ose.processor.cascader.ConstraintTermPositionsWrapper;
import ose.processor.cascader.RangeConstraint;

/**
 * @author Pham Kim Cuong
 *
 */
public class TestConstraintWrapper extends TestCase {

	private String doc1 = "" +
			"<html><body>\n" +
			"Canon PowerShot SD1000 7.1MP Digital Elph Camera with 3x Optical Zoom (Silver)\n" +
			"List Price:  	$199.99\n" +
			"Price: 	$156.08\n" + 
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
	
	private String INDEX_PATH = "C:/tmp/annieSearch/index/unittests/indexConstraintWrapper";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.setProperty("gate.home", "c:/GATE-4.0");
		TrecIndexer indexer = new TrecIndexer(INDEX_PATH);
//		indexer.indexStrings(allDocs);
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
	public void testDoc1() throws IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termCanon = new Term("Token", "canon");
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(termCanon,null, 
				reader.termPositions(termCanon));
		assertTrue(wrapper.next());
		assertEquals(wrapper.getDocID(), 0);
		assertEquals(wrapper.getPosition(),0);
				
	}

	/**
	 * Test method for {@link ose.processor.cascader.ConstraintTermPositionsWrapper#next()}.
	 */
	@Test
	public void testDoc2() throws IOException {
		IndexReader reader = IndexReader.open(INDEX_PATH);
		Term termHan = new Term("Token", "han");
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(termHan,null, 
				reader.termPositions(termHan));
		assertTrue(wrapper.next());
		assertEquals(wrapper.getDocID(), 1);
		assertEquals(wrapper.getPosition(),1);
	}
	
	
	public void testNumberPositions() throws IOException {
		int [] docs = { 0,1 };
		int [][] positions = { {3,11,22,28},{17,24,32,53} };
		
		IndexReader reader = IndexReader.open(INDEX_PATH);
		printPositions(reader);
		
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(null,null, 
				reader.termPositions(new Term("Annotation", "number")));
		for (int i = 0; i < docs.length; i++) {
			assertTrue(wrapper.next());
			assertEquals(wrapper.getDocID(), docs[i]);
			for (int j = 0 ; j < positions[i].length; j++){
				assertEquals(wrapper.getPosition(), positions[i][j]);
				if (j < positions[i].length - 1)
					assertTrue(wrapper.nextPosition());
				else
					assertFalse(wrapper.nextPosition());
			}
		}
		assertFalse(wrapper.next());
	}
	
	public void testNumberConstraint() throws IOException {
		int [] docs = { 0,1 };
		int [][] positions = { {22,28},{24} };
		
		IndexReader reader = IndexReader.open(INDEX_PATH);
		printPositions(reader);
		
		Term termNumber = new Term("Annotation", "number");
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(termNumber,new RangeConstraint(100,201), 
				reader.termPositions(termNumber));
		for (int i = 0; i < docs.length; i++) {
			assertTrue(wrapper.next());
			assertEquals(wrapper.getDocID(), docs[i]);
			for (int j = 0 ; j < positions[i].length; j++){
				assertEquals(wrapper.getPosition(), positions[i][j]);
				if (j < positions[i].length - 1)
					assertTrue(wrapper.nextPosition());
				else
					assertFalse(wrapper.nextPosition());
			}
		}
		assertFalse(wrapper.next());
	}

	/**
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private void printPositions(IndexReader reader) throws IOException {
		Term termNumber = new Term("Annotation", "number");
		ConstraintTermPositionsWrapper wrapper = new ConstraintTermPositionsWrapper(termNumber,null, 
				reader.termPositions(termNumber));
		while (wrapper.next()){
			System.out.print("Document " + wrapper.getDocID() + " :");
			do {
				System.out.print("\t" + wrapper.getPosition());
			} while (wrapper.nextPosition());
			System.out.println();
		}
	}

}
