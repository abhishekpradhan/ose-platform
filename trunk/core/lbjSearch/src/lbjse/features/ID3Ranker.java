package lbjse.features;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.trainer.TrainingSession;
import lbjse.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;
import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class ID3Ranker {
	
	protected int sessionId;
	protected int dataId;
	protected LbjseTrainingSession session;
	protected TrainingSession trainingSession;
	protected String trecFile ;
	protected List<Query> allQueries;
	
	protected Map<DiscreteFeature, Double> posFeatureMap = new HashMap<DiscreteFeature, Double>();
	protected Map<DiscreteFeature, Double> negFeatureMap = new HashMap<DiscreteFeature, Double>();
	protected int positiveCount = 0;
	protected int negativeCount = 0;
	protected ArrayList<JSONObject> rankedFeatures = new ArrayList<JSONObject>();
	
		
	public ID3Ranker(int sessionId, int dataId) throws SQLException, IOException{
		this.sessionId = sessionId;
		this.dataId = dataId;
		try {
			initialize();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} catch (InstantiationException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		}
	}
	
	protected void initialize() throws SQLException, IOException,
		ClassNotFoundException, InstantiationException,
		IllegalAccessException{
		LbjseTrainingSessionManager sessMan = new LbjseTrainingSessionManager();
		session = sessMan.getSessionForId(sessionId) ;
		System.out.println("Got session :" + session);
		System.out.println("FeatureGen Class :" + session.getFeatureGeneratorClass());
		System.out.println("Classifier Class :" + session.getClassifierClass());
		
		trainingSession = new TrainingSession(session);
		
		LbjseDataManager man = new LbjseDataManager();
		LbjseData data = man.getDataForId(dataId);
		if (data == null)
			throw new RuntimeException("dataId not found");
		System.out.println("Got data " + data);
		trecFile = data.getPath();
		
		allQueries = new ArrayList<Query>();
		LbjseQueryValueManager qvMan = new  LbjseQueryValueManager();
		for (LbjseQueryValue lbjQValue : qvMan.getQueryValueForSession(session.getId())){
			Query query = new Query(session.getDomainId());
			query.setFieldValue(session.getFieldId(), lbjQValue.getValue());
			allQueries.add(query);
		}
		
	}
	
	public void run() throws Exception{
		processDocQueryPairs();
		rankFeatures();
	}

	/**
	 * @throws JSONException
	 */
	protected void rankFeatures() throws JSONException {
		Set<DiscreteFeature> allFeatures = new HashSet<DiscreteFeature>();
		allFeatures.addAll(posFeatureMap.keySet());
		allFeatures.addAll(negFeatureMap.keySet());
		System.out.println("Total # active features : " + allFeatures.size());
		System.out.println("Positive/Negative : " + positiveCount + "/" + negativeCount);
		rankedFeatures.clear();
		for(DiscreteFeature f : allFeatures){
			Double posCount = getDefaultValue(posFeatureMap, f, 0.0);
			Double negCount = getDefaultValue(negFeatureMap, f, 0.0);
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

	protected Double getDefaultValue(Map<DiscreteFeature, Double> map, DiscreteFeature f, Double defaultVal) {
		Double posCount = map.get(f);
		if (posCount == null) posCount = defaultVal;
		return posCount;
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void processDocQueryPairs() throws IOException {
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		
		Random rand = new Random();
		int count = 0 ;
		int countDoc = 0;
		for (Query query : allQueries) {
			countDoc = 0;
			trecParser.reset();
			for (DocumentFromTrec doc = (DocumentFromTrec) trecParser.next(); doc != null; doc = (DocumentFromTrec) trecParser.next()){
				if (! CommonUtils.hasEnoughTags(doc,query))
					continue;
				countDoc += 1;
				
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				boolean oracle = pair.oracle();
//				if (!oracle && rand.nextDouble() > NEGATIVE_EX_PROB)
//					continue; //skip this example. 
				FeatureVector fvector = trainingSession.getFeatureVector(pair);
				
				Map<DiscreteFeature, Double> theMap = null;
				
				if (oracle){
					theMap = posFeatureMap;
					positiveCount += 1;
				} else {
					theMap = negFeatureMap;
					negativeCount += 1;
				}
				Set<DiscreteFeature> featureSet = new HashSet<DiscreteFeature>();
				for (Object feature : fvector.features){
					featureSet.add((DiscreteFeature)feature);
				}
				for (DiscreteFeature f : featureSet){
					increaseMapValue(theMap, f, 1.0);
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
		System.out.println();
		System.out.println("Number of queries : " + allQueries.size());
		System.out.println("Number of docs : " + countDoc);
	}

	protected void increaseMapValue(Map<DiscreteFeature, Double> theMap,
			DiscreteFeature f, Double inc) {
		Double t = theMap.get(f);
		if (t == null) t = 0.0;
		theMap.put(f, t + inc);
	}
	
	public void print() throws Exception {
		System.out.println("Ranked features");
		for (JSONObject obj : rankedFeatures){
			String feature = obj.getString("feature");
			double score = obj.getDouble("score");
			double posCount = obj.getDouble("posCount");
			double negCount = obj.getDouble("negCount");
			System.out.println(feature + "\t" + score + "\t" + posCount + "\t" + negCount);
		}
	}

	public void saveToFile(String fileName) throws Exception {
		PrintWriter writer = new PrintWriter(fileName);
		for (JSONObject obj : rankedFeatures){
			String feature = obj.getString("feature");
			double score = obj.getDouble("score");
			double posCount = obj.getDouble("posCount");
			double negCount = obj.getDouble("negCount");
			writer.println(feature + "\t" + score + "\t" + posCount + "\t" + negCount);
		}
		writer.close();
	}

	public void saveJSONToFile(String fileName) throws Exception {
		PrintWriter writer = new PrintWriter(fileName);
		JSONObject result = new JSONObject();
		JSONArray items = new JSONArray();
		for (JSONObject item : rankedFeatures) {
			items.put(item);
		}
		result.put("items", items);
		writer.println(result.toString(2));
		writer.close();
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
		ID3Ranker ranker = new ID3Ranker(sessionId, dataId);
		ranker.run();
		ranker.saveToFile(featureFile);
		ranker.saveJSONToFile(jsonFile);
//		ranker.print();
	}

}
