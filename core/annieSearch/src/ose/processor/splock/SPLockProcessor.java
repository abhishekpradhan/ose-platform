package ose.processor.splock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;

import common.profiling.Profile;

import ose.processor.Span;
import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.processor.cascader.OSHits;
import ose.utils.CommonUtils;

public class SPLockProcessor {
	private TreeNode queryTree;
	private IndexReader reader;
	
	private int currentDocId; 
	private SpanStatus res ;
	private PreProcessor preProcessor = new PreProcessor();
	int nextDocCount = 0;
	public SPLockProcessor(IndexReader reader) {
		this.reader = reader;
	}
	
	private boolean bDedup = true;
	
	int CHECK_POINT = 10000;
	public void processTree(BaseTreeNode treeRoot) throws IOException{
		initialize(treeRoot);
		OSHits result = new OSHits(reader);
		int lastCheckPoint = 0;
		int countDoc = 0;
		nextDocCount = 0;
		while (hasNext()){
			countDoc += 1;
			int docId = getCurrentDocId();
//			System.out.println("--- Doc : " + getCurrentDocId());
//			result.addNewDocument(0.0, docId, null);
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.print("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println("==================== count doc : " + countDoc);
		System.out.println("==================== nextDoc count : " + nextDocCount);
	}
	
	public boolean hasNext() {
		try {
			res = null;
			SpanStatus lastRes = null;
			int holdCount = 0;
			while (true){
				res = queryTree.nextDoc();
//				if (res.docId > 200)
//					printTree(0, queryTree);
				nextDocCount += 1;
				if (res.equals(lastRes)){
//					System.out.println("Stalled : " + res  );
					holdCount += 1;
					if (holdCount > 4){
						System.out.println("Hanged: " + holdCount);
						printTree(0,queryTree);
						return false;
					}
				}
				else
					holdCount = 0;
				if (res.isDone())
					return false;
				else if (res.isAvailable()){
					currentDocId = res.docId;
					return true;
				}
				lastRes = res.clone();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean skipTo(int target){
		try {
			res = queryTree.skipTo(target);
			if (res.isAvailable()){
				currentDocId = res.docId;
				return true;
			}
			else {
				return hasNext();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public int getCurrentDocId() {
		return currentDocId;
	}
	
	public Span firstSpan() throws IOException{
		if (res.isAvailable()){
			return queryTree.getCurrentSpan();
		}
		return null;
	}
	
	public Span nextSpan() throws IOException{
		if (res.isAvailable()){
			if (queryTree.nextSpan())
				return queryTree.getCurrentSpan();
		}
		return null;
	}

	/**
	 * @param treeRoot
	 * @throws IOException
	 */
	public void initialize(BaseTreeNode treeRoot) throws IOException {
		if (bDedup)
			treeRoot = preProcessor.deduplicate(treeRoot);
		this.queryTree = treeRoot;
		
//		printTree(0,treeRoot);
//		treeRoot = (BaseTreeNode) ((DisjunctiveNode) treeRoot).children.get(0);
		
		treeRoot.initialize(reader);
	}

	public TreeNode getQueryTree() {
		return queryTree;
	}
	
	public void setDeduplication(boolean dedup) {
		bDedup = dedup;
	}
	
	static private void printTree(int level, TreeNode queryTree) {
		System.out.println( CommonUtils.tabString(level) + "[@" + queryTree.hashCode() + ", " + queryTree.getParents().size() + "]" + queryTree + "[status=" + queryTree.getCurrentStatus() + "]" );
//		if (queryTree instanceof ShareSkipToNode) {
//			ShareSkipToNode shareNode = (ShareSkipToNode) queryTree;
//			shareNode.printDebug(level + 1);
//		}
		if (queryTree instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) queryTree;
			for (TreeNode child : pNode.getChildren()){
				printTree(level + 1 , child);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String indexPath = "C:\\working\\annieIndex\\combine_testing_index";
//		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/random100pages";
//		String indexPath = System.getProperty("user.dir") + "/target/unittests/indexes/camera_10";		
		
//		query = "Phrase(Token(size), Number_body())";
//		query = "Phrase(Number_body(),Token(display))";
//		String query = "Phrase(Token(display),Number_body(_range(100,200))) Phrase(Number_body(_range(100,200)),Token(camera))";
//		String query = "Phrase(Token('$'),Number_body(_range(150,160))) Phrase(Number_body(_range(6,6)),Token(mp))";
//		String query = "Phrase(Token('$'),Number_body(_range(150,160))) Phrase(Number_body(_range(6,6)),Token(mp))";


		String queryLaptopReplaceNumberWithDot = "HTMLTitle(lenovo) Proximity(Token(lenovo),Token(manufacturer),-3,3) Token(lenovo) Proximity(Token('.'),Token('\"',in,inch),-2,0) Proximity(Token('.'),HTMLTitle('\"',in,inch),-2,0) Proximity(Token('.'),Or(Phrase(screen size),Token(display)),0,7) Proximity(Token('.'),Token(widescreen,wxga,xga,tft),-8,0) Proximity(Token('.'),Token(gb),-2,0) Proximity(Token('.'),HTMLTitle(gb),-2,0) Proximity(Token('.'),Token(rpm),-5,5) Proximity(Token('.'),Token(drive,hdd),-5,5) Proximity(Phrase(processor speed),Token('.'),-4,0) Proximity(Token('.'),Token(ghz),-2,0) Proximity(Token('.'),HTMLTitle(ghz),-2,0) Phrase(Token('$'),Token('.')) Proximity(Token(price),Phrase(Token('$'),Token('.')),-4,3) Phrase(Top(Token('$'),3),Token('.')) Proximity(Top(Phrase(price,':'),1),Token('.'),-10,0) HTMLTitle(laptop)  Token(availability)  Token(laptop)  Phrase(Token(product),Token(descriptions,description,specification,specifications))  Token(warranty)  Token(manufacturer)";
		String queryLaptop = "HTMLTitle(lenovo) Proximity(Token(lenovo),Token(manufacturer),-3,3) Token(lenovo) Proximity(Number_body(_range(12.1,15.6)),Token('\"',in,inch),-2,0) Proximity(Number_title(_range(12.1,15.6)),HTMLTitle('\"',in,inch),-2,0) Proximity(Number_body(_range(12.1,15.6)),Or(Phrase(screen size),Token(display)),0,7) Proximity(Number_body(_range(12.1,15.6)),Token(widescreen,wxga,xga,tft),-8,0) Proximity(Number_body(_range(119.95,384.05)),Token(gb),-2,0) Proximity(Number_title(_range(119.95,384.05)),HTMLTitle(gb),-2,0) Proximity(Number_body(_range(119.95,384.05)),Token(rpm),-5,5) Proximity(Number_body(_range(119.95,384.05)),Token(drive,hdd),-5,5) Proximity(Phrase(processor speed),Number_body(_range(1.95,2.55)),-4,0) Proximity(Number_body(_range(1.95,2.55)),Token(ghz),-2,0) Proximity(Number_title(_range(1.95,2.55)),HTMLTitle(ghz),-2,0) Phrase(Token('$'),Number_body(_range(563.83,1597.94))) Proximity(Token(price),Phrase(Token('$'),Number_body(_range(563.83,1597.94))),-4,3) Phrase(Top(Token('$'),3),Number_body(_range(563.83,1597.94))) Proximity(Top(Phrase(price,':'),1),Number_body(_range(563.83,1597.94)),-10,0) HTMLTitle(laptop)  Token(availability)  Token(laptop)  Phrase(Token(product),Token(descriptions,description,specification,specifications))  Token(warranty)  Token(manufacturer)";
		String queryCamera = "HTMLTitle(canon)  Token(canon) HTMLTitle(g10)  Token(g10)  Proximity(Number_body(_range(8,30)),Token(megapixel,megapixels,mp),-3,1) Proximity(Number_body(),Token(megapixel,megapixels,mp),-3,1)  Proximity(Number_title(_range(8,30)),HTMLTitle(megapixel,megapixels,mp),-3,0) Proximity(Number_body(_range(5,20)),Phrase(Token(optical),Token(zoom)),-4,3) Proximity(Number_body(),Phrase(Token(optical),Token(zoom)),-4,3)) Phrase(Number_title(_range(5,20)),HTMLTitle(x)) Phrase(Token('$'),Number_body(_range(300,500))) Proximity(Token(price),Phrase(Token('$'),Number_body(_range(300,500))),-3,3) Proximity(Phrase(Token(your,our,special,sale),Token(price)),Phrase(Token('$'),Number_body(_range(300,500))),-7,0)  Token(checkout)  Token(shop shopping)  Token(shipping shipped ships)  Phrase(add to cart) Token(availability)";
		String queryProfessor = "HTMLTitle(john)  Token(john)  Token(science) Proximity(Token(department),Token(science),-5,5) Proximity(Token(professor),Token(science),-3,3) Proximity(Token(university),Token(illinois),-5,5) Token(illinois) Proximity(Token(professor),Token(illinois),-6,0) Proximity(Phrase(research group),Token(computer),-10,10) Proximity(Phrase(group),Token(computer),-3,3) Proximity(Or(Phrase(my research),Phrase(research interests),Phrase(research summary)),Token(computer),-50,0) Token(computer) HTMLTitle(homepage)  Token(professor)  Token(publication,publications,papers)  Phrase(Token(associate,adjunct,assistant),Token(professor))  Token(graduated,received) Phrase(program,Token(chair,committee)) Phrase(my research) Phrase(research interests) Token(biography) Phrase(curriculum vitae) Token(cv)";
		String queryCourse = "HTMLTitle(system)  Token(system)  Phrase(system) Token(john) Phrase(john) Proximity(Token(instructor),Token(john),-5,0) Proximity(Token(fall),Token(semester,term),-5,5) Proximity(Token(fall),Number_body(_range(1900,2100)),-3,3) Proximity(Token(fall,winter,summer,spring,autumn),Number_body(_range(2009,2009)),-3,3) Number_body(_range(2009,2009)) Number_title(_range(2009,2009)) Proximity(Token(edu),Token(illinois),-3,3) Proximity(Top(Token(edu),1),Token(illinois),-3,3) Proximity(Token(university),Token(illinois),-3,3) Token(illinois) Token(computing) Phrase(computing) Token(instructor) Token(semester) Token(grading) Token(homework) Token(textbook) Token(announcements) Token(midterm) Phrase(course information) Phrase(class schedule) Token(spring,fall,summer,winter)";
		String querySharedBranch = "%BooleanFeature(Top(Proximity(Number_body(_range(12.1,15.6)),Token('\"',in,inch),-2,0),1)) %BooleanFeature(Proximity(Number_body(_range(12.1,15.6)),Token('\"',in,inch),-2,0)) ";
		String query = queryLaptop;
		
//		IndexReader reader = IndexReader.open(indexPath);
		IndexReader reader = IndexReader.open(new RAMDirectory(indexPath), null);
		System.out.println("Done reading into RAM");
		
		SPLockParser parser = new SPLockParser();
		SPLockProcessor processor = new SPLockProcessor(reader);  
		
		BaseTreeNode node = (BaseTreeNode) parser.parse(query);
		
		long startTime = System.currentTimeMillis();	
		
//		processor.processAndShowSpans(node);
		processor.processTree(node);
		
		System.out.println("Done, Time : " + (System.currentTimeMillis() - startTime));
		reader.close();
		
		Profile.printAll();
	}

	/**
	 * @param processor
	 * @param node
	 * @throws IOException
	 */
	private void processAndShowSpans(BaseTreeNode node) throws IOException {
				

		initialize( (BaseTreeNode) node);
//		System.out.println("Next : " + processor.hasNext() );
//		System.out.println("Skip to : " + processor.skipTo(36) );
//		{
//			Span span = processor.firstSpan();
//			while (span != null) {
//				System.out.println("------- " + span );
//				span = processor.nextSpan();
//			}
//		}
		while (hasNext()){
			Span span = firstSpan();
			while (span != null) {
//				System.out.println("------- " + span );
				span = nextSpan();
			}
		}
		
		
	}

}
