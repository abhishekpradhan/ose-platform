package lbjse.rank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.utils.OrderPair;
import lbjse.utils.Sorter;
import ose.database.Feedback;
import ose.database.FeedbackManager;
import ose.database.LBJSECache;
import ose.database.LBJSECacheManager;
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;
import LBJ2.learn.Learner;
import LBJ2.learn.Sigmoid;

public class ObjectRanker {
	protected DocQueryFileParser data ;
	
	protected String [] fieldNames;
	protected Learner [] rankers;
	protected String outputFile;
	protected List<ResultItem> details ;
	
	public enum AggregationMode {
		MULTIPLY, BORDA
	}
	
	protected AggregationMode aggMode;
	protected List<Boolean> labels;
	protected List<OrderPair> scores;
	
	protected ObjectRanker(String[] fieldNames, Learner [] rankers, String outputFile, AggregationMode aggMode) {
		this.outputFile = outputFile;
		this.fieldNames = fieldNames;
		this.rankers = rankers;
		this.aggMode = aggMode;
	}

	protected void initializeData(String lbjTrecFile, String queryFile){
		data = new DocQueryFileParser(lbjTrecFile,queryFile);
	}
	
	protected void initializeData(String lbjTrecFile, int queryId) throws IOException, SQLException{
		LBJTrecFileParser trecParser = new LBJTrecFileParser(lbjTrecFile);
		QueryInfo qInfo = new QueryInfoManager().queryByKey(queryId);
		if (qInfo == null)
			throw new SQLException("Can not find query " + queryId);
		Query query = new Query(qInfo.getDomainId(), qInfo.getQueryString());
		List<Query> querries = new ArrayList<Query>();
		querries.add(query);
		data = new DocQueryFileParser(trecParser.getDocs(),querries);
	}
	
	public void rankQueryAndOutputAP() throws IOException{
		PrintWriter writer = new PrintWriter(outputFile);
		
		switch (aggMode) {
			case MULTIPLY:
				computeResultAndRank();
				break;
			case BORDA:
				computeBordaRank();
				break;
			default:
				throw new RuntimeException("Unknown aggregation mode");
		}
		
		
		
		int count = 0 ;
		int posCount = 0;
		double totalPrecision = 0;
		for (OrderPair p : scores) {
			writer.println(details.get(p.getId()));
			count += 1;
			if (labels.get(p.getId()) == true){
				posCount += 1;
				totalPrecision += posCount * 1.0 / count;
			}
		}
		System.out.println("Number of documents : " + labels.size());		
		System.out.println("Number of positive documents : " + posCount) ;
		System.out.println("Average precision : ");
		System.out.println(totalPrecision / posCount);
		writer.close();
	}
	
	private void computeResultAndRank(){
		details = new ArrayList<ResultItem>();		
		labels = new ArrayList<Boolean>();		
		Sorter sorter = new Sorter();
		DocQueryPair pair = null;
		int count = 0 ;
		count = 0;
		while ( (pair = (DocQueryPair )data.next()) != null) {
			double score = getRelevantScore(pair,false);
			boolean label = pair.oracle();
			sorter.addPair(new OrderPair(score, count));
			labels.add(label);
			count += 1;
			details.add(new ResultItem(pair.getDoc().getDocId() ,pair.getDoc().getUrl(), 
					pair.getDoc().getTitle(), score, label));
		}
		
		scores = sorter.getOrderedList();
		Collections.reverse(scores);
	}
	
	static Set<Integer> debugDocId = new HashSet<Integer>(Arrays.asList(new Integer[]{37005,39801}));
	static Set<Integer> debugLid = new HashSet<Integer>();
	private void computeBordaRank(){
		details = new ArrayList<ResultItem>();		
		labels = new ArrayList<Boolean>();		
		Sorter [] sorters = new Sorter[10]; //TODO : fix magic number
		int [] positiveClass = new int[10];
		DocQueryPair pair = null;
		int count = 0 ;
		count = 0;
		while ( (pair = (DocQueryPair )data.next()) != null) {
			Double [] scores = getRelevantScoreForAllFields(pair);
			boolean label = pair.oracle();
			for (int i = 0; i < scores.length; i++) {
				if (sorters[i] == null){
					sorters[i] = new Sorter();
				}
				sorters[i].addPair(new OrderPair(scores[i], count));
				if (scores[i] > 0)
					positiveClass[i] += 1;
			}
			if (debugDocId.contains(pair.getDoc().getDocId() ) ){
				debugLid.add( count );
				System.out.println("Doc id " + pair.getDoc().getDocId() + "--> lid : " + count );
			}
			labels.add(label);
			count += 1;
			details.add(new ResultItem(pair.getDoc().getDocId() ,pair.getDoc().getUrl(), 
					pair.getDoc().getTitle(), 0.0, label)); //set score = 0.0 because we dont know it yet
			
		}
		
		Map<Integer, Double> borderCountMap = new HashMap<Integer, Double>();
		for (int i = 0; i < sorters.length; i++) {
			if (sorters[i] == null) continue;
			List<OrderPair> sorted = sorters[i].getOrderedList();
			Collections.reverse(sorted);
			int rank = 0;
			double lastScore = Double.MAX_VALUE;
			int lastRank = 0;
			for (OrderPair orderPair : sorted) {
				int id = orderPair.getId();
				
				double bcount = 0;
				if (borderCountMap.containsKey(id))
					bcount = borderCountMap.get(id);
				int thisRank = lastRank;
				if ((Double)orderPair.getValue() < lastScore){
					thisRank = rank;
					lastRank = rank;
				}
				
				if (debugLid.contains(id)){
					System.out.println(" --- lid " + id);
					System.out.println(" --- bcount " + bcount);
					System.out.println(" --- thisRank " + thisRank);
					System.out.println(" --- positives " + positiveClass[i]);
					System.out.println(" --- rank " + rank);
					System.out.println(" --- pair " + orderPair);
				}
				
				if (positiveClass[i] > 0){
					borderCountMap.put(id, bcount + 1.0d/positiveClass[i] * thisRank);
				}
				
				rank += 1;
				lastScore = (Double) orderPair.getValue();
			}
		}
		
		Sorter sorter = new Sorter();
		for (Integer id : borderCountMap.keySet()){
			Double score = borderCountMap.get(id);
			sorter.addPair(new OrderPair(score, id));
			details.get(id).score = score;
		}
		scores = sorter.getOrderedList();
		
	}

	public void saveCacheToDatabase(int queryId, int indexId) {
		LBJSECacheManager cacheManager = new LBJSECacheManager();
		try {
			LBJSECache cache = cacheManager.getCacheForQuery(queryId, indexId);
			if (cache == null){
				cache = new LBJSECache(-1, queryId, indexId, outputFile, new Date(System.currentTimeMillis()));
				cacheManager.insert(cache);
			}
			else {
				cache.setCacheFile(outputFile);
				cache.setDateCreated(new Date(System.currentTimeMillis()));
//				System.out.println("Updating " + cache);
				cacheManager.update(cache);
			}
		} catch (SQLException e) {
			System.err.println("Can not insert cache record");
			e.printStackTrace();
		}
		
	}
	
	public void addFeedbackToDatabase(int queryId, int indexId) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(outputFile));
		List<Feedback> allFb = new ArrayList<Feedback>();
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			try {
				item = new ResultItem(line);
				Feedback fb = new Feedback(queryId, item.docId, indexId, -1, item.label);
				allFb.add(fb);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		
		try {
			new FeedbackManager().insertUpdateBatch(allFb);			
		} catch (SQLException e) {
			System.err.println("Can not insert feedback");
			e.printStackTrace();
		}
		System.out.println("Finish inserting " + allFb.size());
	}
	
	public void showScoreForDocId(int docId) {
		DocQueryPair pair = null;
		
		while ( (pair = (DocQueryPair )data.next()) != null) {
			if (pair.getDoc().getDocId() != docId)
				continue;
			double score = getRelevantScore(pair, true);
			System.out.println("Score : " + score);
			
		}
	}
	
	/**
	 * @param pair
	 * @return
	 */
	private double getRelevantScore(DocQueryPair pair, boolean printOut) {
		Query q = pair.getQuery();
		if (q.getFieldValue("other").length() == 0)
			q.setFieldValue("other", "yes");
		double score = 1.0;		
		for (int i = 0; i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			if (q.getFieldValue(fieldName) != null && q.getFieldValue(fieldName).length() > 0){
				double s = new Sigmoid().normalize(rankers[i].scores(pair)).get("true");
				score *= s;
				if (printOut)
					System.out.println("----------- " + fieldName + "\t" + s);
			}
		}
		return score;
	}
	
	private Double[] getRelevantScoreForAllFields(DocQueryPair pair) {
		List<Double> scores = new ArrayList<Double>();
		Query q = pair.getQuery();
		if (q.getFieldValue("other").length() == 0)
			q.setFieldValue("other", "yes");
		for (int i = 0; i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			if (q.getFieldValue(fieldName) != null && q.getFieldValue(fieldName).length() > 0){
				double s = rankers[i].scores(pair).get("true");
				scores.add(s);
			}
		}
		return scores.toArray(new Double[]{});
	}
	
}
