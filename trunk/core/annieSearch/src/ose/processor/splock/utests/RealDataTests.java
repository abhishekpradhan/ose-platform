/**
 * 
 */
package ose.processor.splock.utests;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.processor.Span;
import ose.processor.TreeNode;
import ose.processor.splock.BaseTreeNode;
import ose.processor.splock.SPLockParser;
import ose.processor.splock.SPLockProcessor;

/**
 * @author Pham Kim Cuong
 *
 */
public class RealDataTests extends TestCase {
	
	private String [] allTests = new String [] {
			"utests/Test1.xml",
			"utests/Test2.xml",
			"utests/Test3.xml",
			"utests/Test4.xml",
			"utests/Test5.xml",
			"utests/Test6.xml",
			"utests/Test7.xml",
			"utests/Test8.xml",
			"utests/Test9.xml",
			"utests/Test10.xml",
			};
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/camera_10";
		String trecPath = System.getProperty("user.dir") + "/utests/camera_10docs.trec";
		if (! (new File(indexPath).exists()) ){
			UTestData.createIndex(trecPath, indexPath);
		}
		
		indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/random100pages";
		trecPath = System.getProperty("user.dir") + "/utests/random100pages.trec";
		if (! (new File(indexPath).exists()) ){
			UTestData.createIndex(trecPath, indexPath);
		}
		
		for (int i = 0; i < allTests.length; i++) {
			if (! new File(allTests[i]).exists()){
				throw new Exception("Test " + allTests[i] + " doesn't exist");
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleFeatureWith10CameraPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test1.xml");
	}
	
	@Test
	public void testCameraFeaturesWith10CameraPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test2.xml");
	}
	
	@Test
	public void testProfessorFeaturesWith10CameraPages() throws IOException {
		checkSpansWithStartPosOnly( "utests/Test3.xml");
	}
	
	@Test
	public void testLaptopFeaturesWith10CameraPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test4.xml");
	}

	@Test
	public void testCourseFeaturesWith10CameraPages() throws IOException {
		checkSpansWithStartPosOnly( "utests/Test5.xml");
	}
	
	@Test
	public void testWith100CameraPages() throws IOException {
		checkSpansWithStartPosOnly( "utests/Test6.xml");
	}
	
	@Test
	public void testCameraFeatureWith100CameraPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test7.xml");
	}
	
	@Test
	public void testProfessorFeatureWith100RandomPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test8.xml");
	}
	
	@Test
	public void testLaptopFeatureWith100RandomPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test9.xml");
	}
	
	@Test
	public void testCourseFeatureWith100RandomPages() throws IOException {
		checkSpansWithStartPosOnly("utests/Test10.xml");
	}
	
//	@Test
//	public void testBig() throws IOException {
//		checkSpansWithStartPosOnly("utests/Test11.xml");
//	}
	
	
	/**
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	static public void checkSpansWithStartPosOnly(String testResultFile) throws CorruptIndexException,
			IOException {
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
				assertNotNull("Extra span " + span, correctSpan );
				assertEquals("Different docId",  correctSpan.docId, span.docId  );
				System.out.println("------- " + span + "  ------- " + correctSpan);
				assertEquals("Different start position " + span,  correctSpan.startPos, span.startPos );				
				Span nextSpan = processor.nextSpan();
				//since the old processor does not output two spans with the same startPos, we have to ignore them here.
				while (nextSpan != null && nextSpan.docId == span.docId && nextSpan.startPos == span.startPos){
					nextSpan = processor.nextSpan();
				}
				span = nextSpan;
			}
		}
		assertNull("There is missing span ", testReader.nextSpan());
		reader.close();
	}
	
}
