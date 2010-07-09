package lbjse.features;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.CommandLineOption;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

public class PosteriorID3Ranker extends ID3Ranker{
	
	
	
	public PosteriorID3Ranker(int sessionId, int dataId) throws SQLException, IOException{
		super(sessionId, dataId);
	}
	
	public void run() throws Exception {
		processDocQueryPairs();
		rankFeatures();
	}
	
	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void processDocQueryPairs() throws IOException {
		System.out.println("rank feature based on data & classifier : " + session.getClassifierClass());
		
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		int count = 0 ;
		for (Query query : allQueries) {
			for (DocumentFromTrec doc = (DocumentFromTrec) trecParser.next(); doc != null; doc = (DocumentFromTrec) trecParser.next()){
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				
				FeatureVector fvector = trainingSession.getFeatureVector(pair);
				double weight;
				String predLabel = trainingSession.discreteValue(pair);
				if ( (pair.oracle() + "").equals(predLabel)){
//					weight = 1 - 2 * Math.abs(0.5 - weightBefore);
					weight = 1;
				}
				else {
//					weight = 2 * 2 * Math.abs(0.5 - weightBefore);
					weight = 10;
				}
//				System.out.println("\t" + predLabel + "\t" + pair.oracle() + "\t" + weightBefore + "\t" + weight);
				Map<DiscreteFeature, Double> theMap = null;
				if (pair.oracle()){
					theMap = posFeatureMap;
					positiveCount += weight;
				} else {
					theMap = negFeatureMap;
					negativeCount += weight;
				}
				Set<DiscreteFeature> featureSet = new HashSet<DiscreteFeature>();
				for (Object feature : fvector.features){
					featureSet.add((DiscreteFeature)feature);
				}
				for (DiscreteFeature f : featureSet){
					Double t = theMap.get(f);
					if (t == null) t = 0.0;
					theMap.put(f, t + weight);
				}
				count += 1;
				if (count % 10 == 0){
					System.out.print(".");
					if (count % 100 == 0){
						System.out.println();
					}
				}
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"sessionId", "dataId", "featureFile", "jsonFile"});
		int sessionId = options.getInt("sessionId");
		int dataId = options.getInt("dataId");
		String featureFile = options.getString("featureFile");
		String jsonFile = options.getString("jsonFile");
		PosteriorID3Ranker ranker = new PosteriorID3Ranker(sessionId, dataId);
		ranker.run();
		ranker.saveToFile(featureFile);
		ranker.saveJSONToFile(jsonFile);
		ranker.print();
	}

}
