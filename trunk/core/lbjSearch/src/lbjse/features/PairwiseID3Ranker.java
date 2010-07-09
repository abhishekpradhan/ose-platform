package lbjse.features;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;

import org.json.JSONException;
import org.json.JSONObject;

import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class PairwiseID3Ranker extends ID3Ranker {
	
	protected Map<DiscreteFeature, Double> posTotalMap ; 
	protected Map<DiscreteFeature, Double> negTotalMap ;
	
	public PairwiseID3Ranker(int sessionId, int dataId) throws SQLException,
			IOException {
		super(sessionId, dataId);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void processDocQueryPairs() throws IOException {
		LBJTrecFileParser docParser = new LBJTrecFileParser(trecFile);
		List<DocumentFromTrec> docs = docParser.getDocs();
		
		System.out.println("Total docs : " + docs.size());
		
		posTotalMap = new HashMap<DiscreteFeature, Double>();
		negTotalMap = new HashMap<DiscreteFeature, Double>();
		
		int count = 0;
		
		for (Query query : allQueries){
			Map<DiscreteFeature, Double> theMap = null;
			Map<DiscreteFeature, Double> posCount = new HashMap<DiscreteFeature, Double>();
			Map<DiscreteFeature, Double> negCount = new HashMap<DiscreteFeature, Double>();
			double positiveCount = 0; 
			double negativeCount = 0;
			for (DocumentFromTrec doc : docs){
				DocQueryPair pair = new DocQueryPairFromFile(doc, query);
				
				boolean oracle = pair.oracle();
				FeatureVector fvector = trainingSession.getFeatureVector(pair);
				
				if (oracle) {
					positiveCount += 1;
					theMap = posCount;
				} else {
					negativeCount += 1;
					theMap = negCount;
				}
				Set<DiscreteFeature> featureSet = new HashSet<DiscreteFeature>();
				for (Object feature : fvector.features){
					featureSet.add((DiscreteFeature)feature);
				}
				for (DiscreteFeature feature : featureSet){
					increaseMapValue(theMap, feature, 1.0);
				}
				count += 1;
				if (count % 10 == 0){
					System.out.print(".");
					if (count % 100 == 0){
						System.out.println();
					}
				}
			}
			//after tally all docs for this query, compute the artificial pairwise counts 
			Set<DiscreteFeature> keys = new HashSet<DiscreteFeature>();
			keys.addAll(posCount.keySet());
			keys.addAll(negCount.keySet());
			for (DiscreteFeature df : keys){
				Double A = posCount.get(df);
				if ( A == null ) A = 0.0;
				Double B = negCount.get(df);
				if ( B == null ) B = 0.0;
				increaseMapValue(posFeatureMap, df, 2 * A * (negativeCount - B));
				increaseMapValue(negFeatureMap, df, 2 * B * (positiveCount - A));
				increaseMapValue(posTotalMap, df, 2 * A * (negativeCount - B) + A * B + (positiveCount - A) * ( negativeCount - B));
				increaseMapValue(negTotalMap, df, 2 * B * (positiveCount - A) + A * B + (positiveCount - A) * ( negativeCount - B));
			}
		}
		System.out.println();
		System.out.println("Total " + count + " examples trained.");
		
		System.out.println();
		System.out.println("Number of queries : " + allQueries.size());
		System.out.println("Number of docs : " + docs.size());
	}
	
	protected void rankFeatures() throws JSONException {
		Set<DiscreteFeature> allFeatures = new HashSet<DiscreteFeature>();
		allFeatures.addAll(posFeatureMap.keySet());
		allFeatures.addAll(negFeatureMap.keySet());
		System.out.println("Total # active features : " + allFeatures.size());
		rankedFeatures.clear();
		for(DiscreteFeature f : allFeatures){
			Double posCount = getDefaultValue(posFeatureMap,f,0.0);
			Double negCount = getDefaultValue(negFeatureMap,f,0.0);
			Double positiveCount = getDefaultValue(posTotalMap, f, 0.0);
			Double negativeCount = getDefaultValue(negTotalMap, f, 0.0);
			double info = Suggestor.information(1.0 * positiveCount/(positiveCount + negativeCount));
			double infoYes = Suggestor.information(1.0 * posCount / (posCount + negCount));
			double infoNo = Suggestor.information(1.0 * (positiveCount-posCount) / (positiveCount + negativeCount - posCount - negCount) );
			double probYes = 1.0 * (posCount + negCount ) / (positiveCount + negativeCount);  
			double expInfoGain = info - (probYes * infoYes + (1 - probYes) * infoNo);
//			System.out.println("F : " + f 
//					+ "\t" + posCount + "\t" + negCount 
//					+ "\t" + positiveCount + "\t" + negativeCount
//					+ "\t" + expInfoGain
//					+ "\t" + probYes + "\t" + infoYes + "\t" + infoNo);
			JSONObject object = new JSONObject();
			object.put("feature", f.toString());
			object.put("score", expInfoGain);
			object.put("posCount", posCount);
			object.put("negCount", negCount);
			rankedFeatures.add(object);
			
		}
		Collections.sort(rankedFeatures, new Comparator<JSONObject>(){
			public int compare(JSONObject o1, JSONObject o2) {
				try {
					if (o1.getDouble("score") < o2.getDouble("score"))
						return -1;
					else if (o1.getDouble("score") > o2.getDouble("score"))
						return 1;
					else
						return 0;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
			}
		});
		
		Collections.reverse(rankedFeatures);
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
		PairwiseID3Ranker ranker = new PairwiseID3Ranker(sessionId, dataId);
		ranker.run();
		ranker.saveToFile(featureFile);
		ranker.saveJSONToFile(jsonFile);
//		ranker.print();
	}

}
