package ose.processor.shallow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;

import ose.processor.SpanStatus;
import ose.processor.TreeNode;
import ose.processor.cascader.OSHits;
import ose.processor.splock.BaseTreeNode;
import ose.processor.splock.ConnectorNode;
import ose.processor.splock.PreProcessor;
import ose.processor.splock.ProcessingNode;
import ose.processor.splock.ShareSkipToNode;
import ose.utils.CommonUtils;

public class ShallowProcessor {
	private TreeNode queryTree;
	private IndexReader reader;
	private OSHits result;
	private boolean deduplication;
	private PreProcessor preProcessor = new PreProcessor();
	
	public ShallowProcessor(IndexReader reader, boolean dedup) {
		this.reader = reader;
		deduplication = dedup;
	}
	
	int CHECK_POINT = 10000;
	
	public void processTree(BaseTreeNode treeRoot) throws IOException{
		if (deduplication)
			treeRoot = preProcessor.deduplicate(treeRoot);
		
		this.queryTree = treeRoot;
		System.out.println("Shallow tree : ");
//		printTree(0, queryTree);
		
		treeRoot.initialize(reader);
		
		SpanStatus res = null;
		int countDoc = 0 ;
		result = new OSHits(reader);
		int lastCheckPoint = 0;
		SpanStatus lastRes = null;
		int holdCount = 0;
		int countNextDoc = 0;
		while (true){
			res = treeRoot.nextDoc();
			countNextDoc += 1;
			if (res.equals(lastRes)){
//				System.out.println("Stalled : " + res  );
				holdCount += 1;
				if (holdCount > 4){
					System.out.println("Hanged: " + holdCount);
					printTree(0,treeRoot);
					break;
				}
			}
			lastRes = res.clone();
			if (res.isDone())
				break;
			if (res.isAvailable()){
				countDoc += 1;
				holdCount = 0;
//				System.out.println("====" + treeRoot.getCurrentSpan());
				int docId = res.docId;
//				result.addNewDocument(0.0, docId, null);
				if (docId - lastCheckPoint > CHECK_POINT){
					System.out.print("..check point : " + docId);
					lastCheckPoint = docId;
				}
			}
		}
		System.out.println("==================== count doc : " + countDoc);
		System.out.println("==================== countNextDoc : " + countNextDoc);
		
	}

	public OSHits getResult() {
		return result;
	}
	
	private void printTree(int level, TreeNode queryTree) {
		System.out.println( CommonUtils.tabString(level) + "[@" + queryTree.hashCode() + ", " + queryTree.getParents().size() + "]" + queryTree + "[status=" + queryTree.getCurrentStatus() + "]" );
		if (queryTree instanceof ShareSkipToNode) {
			ShareSkipToNode shareNode = (ShareSkipToNode) queryTree;
			shareNode.printDebug(level + 1);
		}
		if (queryTree instanceof ProcessingNode) {
			ProcessingNode pNode = (ProcessingNode) queryTree;
			for (TreeNode child : pNode.getChildren()){
				printTree(level + 1 , child);
			}
		}
	}

}
