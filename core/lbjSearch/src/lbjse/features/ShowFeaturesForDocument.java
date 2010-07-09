package lbjse.features;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.trainer.TrainingSession;
import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;
import ose.database.lbjse.LbjseQueryValue;
import ose.database.lbjse.LbjseQueryValueManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class ShowFeaturesForDocument {

	protected int sessionId;
	protected int dataId;
	protected LbjseTrainingSession session;
	protected TrainingSession trainingSession;
	protected String trecFile ;
	protected List<Query> allQueries;
	
	protected int positiveCount = 0;
	protected int negativeCount = 0;
	
		
	public ShowFeaturesForDocument(int sessionId, int dataId) throws SQLException, IOException{
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
	private void show(String queryJSON, int docId) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		
		DocumentFromTrec theDoc = null;
		for (DocumentFromTrec doc = (DocumentFromTrec) trecParser.next(); doc != null; doc = (DocumentFromTrec) trecParser.next()){
			if (doc.getDocId() == docId){
				theDoc = doc;
				break;
			}
		}
		Query query = new Query(null, queryJSON);
		DocQueryPairFromFile pair = new DocQueryPairFromFile(theDoc, query);
		FeatureVector fvector = trainingSession.getFeatureVector(pair);
		
		Set<DiscreteFeature> featureSet = new HashSet<DiscreteFeature>();
		boolean activated = false;
		for (Object feature : fvector.features){
			System.out.println(feature);
		}
		System.out.println(theDoc);
		System.out.println("\t" + theDoc.getUrl());
		System.out.println("\t" + theDoc.getTitle());
		System.out.println("\t" + theDoc.getBody());
		System.out.println("Done");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"sessionId", "dataId", "query", "docId"});
		int sessionId = options.getInt("sessionId");
		int dataId = options.getInt("dataId");
		int docId = options.getInt("docId");
		String query = options.getString("query");
		
		ShowFeaturesForDocument ranker = new ShowFeaturesForDocument(sessionId, dataId);
		ranker.show(query, docId);
	}}
