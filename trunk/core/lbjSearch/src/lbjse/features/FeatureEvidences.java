package lbjse.features;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.CommandLineOption;

import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbj.professor.other_features;
import lbjse.trainer.TrainingSession;
import lbjse.trainer.Utils;
import lbjse.utils.LbjUtils;
import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;
import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;

public class FeatureEvidences {
	
	protected int sessionId;
	protected int dataId;
	protected LbjseTrainingSession session;
	protected TrainingSession trainingSession;
	protected String trecFile ;
	protected List<Query> allQueries;
	
	protected int positiveCount = 0;
	protected int negativeCount = 0;
	
		
	public FeatureEvidences(int sessionId, int dataId) throws SQLException, IOException{
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
	
	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void show(String featureString, String outputFile) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		PrintWriter writer = new PrintWriter(outputFile);
		int count = 0 ;
		int countDoc = 0;
		DiscreteFeature theFeature = LbjUtils.parseDiscreteFeature(featureString);
		for (Query query : allQueries) {
			countDoc = 0;
			trecParser.reset();
			for (DocumentFromTrec doc = (DocumentFromTrec) trecParser.next(); doc != null; doc = (DocumentFromTrec) trecParser.next()){
				countDoc += 1;
				DocQueryPairFromFile pair = new DocQueryPairFromFile(doc, query);
				
				FeatureVector fvector = trainingSession.getFeatureVector(pair);
				
				Set<DiscreteFeature> featureSet = new HashSet<DiscreteFeature>();
				boolean activated = false;
				for (Object feature : fvector.features){
					if (theFeature.equals(feature)){
						activated = true;
						break;
					}
				}
				if (!activated) 
					continue;
				if (pair.oracle())
					positiveCount += 1;
				else
					negativeCount += 1;
				count += 1;
				writer.println(pair.oracle() + "\t" + pair);
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
		System.out.println("#positive : " + positiveCount);
		System.out.println("#negative : " + negativeCount);
		System.out.println("#total : " + (positiveCount + negativeCount) );
		writer.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"sessionId", "dataId", "output", "feature"});
		int sessionId = options.getInt("sessionId");
		int dataId = options.getInt("dataId");
		String featureString = options.getString("feature");
		String output = options.getString("output");
//		String jsonFile = options.getString("jsonFile");
		FeatureEvidences ranker = new FeatureEvidences(sessionId, dataId);
		ranker.show(featureString, output);
	}

}
