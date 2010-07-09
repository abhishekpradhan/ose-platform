package ose.benchmark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.ModelInfo;
import ose.database.ModelInfoManager;
import ose.parser.OSQueryParser;
import ose.parser.ParentNode;
import ose.parser.ParsingNode;
import ose.parser.QueryTreeParser;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.LogisticRankingQuery;
import ose.processor.cascader.OSHits;
import ose.query.Constant;
import ose.retrieval.ResultPresenter;

public class TestLuceneReadSequentialVSParallel {

	Set<String> leafFeatureSet = new TreeSet<String>();
	int countLeaf = 0;
	
	public TestLuceneReadSequentialVSParallel() {
		// TODO Auto-generated constructor stub
	}
	
	public void scanSequentially(int indexId, String terms) throws Exception{
		Set<String> termSet = new HashSet<String>(Arrays.asList(terms.split("\\s+")));
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());

		List<TermPositions> termPosList = new ArrayList<TermPositions>();
		
		for (String term : termSet){
			termPosList.add(reader.termPositions(new Term("Token",term)));
			System.out.println("Adding term : " + term);
		}

		long startTime = System.currentTimeMillis();			
		System.out.println("Scanning sequentially");
		int count = 0;
		for (TermPositions termPos : termPosList){
			while (termPos.next()){
				count += 1;
			}
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total documents scanned : " + count + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
	}
	
	public void scanPararelly(int indexId, String terms) throws Exception{
		Set<String> termSet = new HashSet<String>(Arrays.asList(terms.split("\\s+")));
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());

		List<TermPositions> termPosList = new ArrayList<TermPositions>();
		
		for (String term : termSet){
			termPosList.add(reader.termPositions(new Term("Token",term)));
//			System.out.println("Adding term : " + term);
		}

		long startTime = System.currentTimeMillis();			
		System.out.println("Scanning pararelly");
		int count = 0;
		boolean stop = false;
		while (!stop){
			stop = true;
			for (TermPositions termPos : termPosList){
				if (termPos.next()){
					count += 1;
					stop = false;
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total documents scanned : " + count + "<br>");
		System.out.println("Total time : " + (endTime - startTime) );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		TestLuceneReadSequentialVSParallel evaluator = new TestLuceneReadSequentialVSParallel();
		String featureQuery1 = "_number gb ghz laptop lenovo price : processor speed screen size \" in inch $ availability descriptions description specification specifications display drive hdd gb ghz laptop lenovo manufacturer price product rpm warranty widescreen wxga xga tft";
		int indexId = 10000;
		evaluator.scanSequentially(indexId, featureQuery1);
		evaluator.scanPararelly(indexId, featureQuery1);
	}

}
