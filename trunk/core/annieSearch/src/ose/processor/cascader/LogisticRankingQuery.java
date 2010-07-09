package ose.processor.cascader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.ModelInfo;
import ose.database.ModelInfoManager;
import ose.learning.DoubleNamedFeatureValue;
import ose.learning.VectorInstance;
import ose.parser.OSQueryParser;
import ose.query.FeatureValue;
import ose.retrieval.ResultPresenter;
import ose.weka.AnnieLogistic;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class LogisticRankingQuery extends FeatureQuery
	implements ResultAggregator {
	
	private String [] logisticModelPaths;
	
	private String [] listOfModelNames;
	private Double [] listOfModelWeights;
	
//	private FilteredClassifier [] listOfClassifiers;
	private AnnieLogistic [] listOfLogistic;
	private int [] listOfDims;
//	private Remove removeFilter;
	private int numberOfDims ;
	private VectorInstance [] fieldInstances;
	private Instances [] datasets;
	
	//for ResultAggregator
	private OSHits result;
	
	public LogisticRankingQuery(List<QueryPredicate> queryPredicates, String [] modelPaths, String [] modelNames, Double [] modelWeights) {
		super(queryPredicates);
		this.logisticModelPaths = modelPaths;
		this.listOfModelNames = modelNames;
		listOfModelWeights = modelWeights;
		initializeModels();
		processNullPredicates();
	}
	
	private void initializeModels() {
		try {
//			removeFilter = new Remove();
//			removeFilter.setAttributeIndices("1");
			
			
			listOfLogistic = new AnnieLogistic[listOfModelNames.length];
//			listOfClassifiers = new FilteredClassifier[listOfModels.length];
			listOfDims = new int[listOfModelNames.length];
			fieldInstances = new VectorInstance[listOfModelNames.length];
			datasets = new Instances[listOfModelNames.length];
			
			numberOfDims = 0;	
			for (int i = 0; i < listOfModelNames.length; i++) {
				if (logisticModelPaths[i] == null){
					listOfLogistic[i] = null;
					listOfDims[i] = 0;
					System.out.println("Warning : " + logisticModelPaths[i] + " not found");
				}
				else {
					InputStream is = getInputStreamFromResource(logisticModelPaths[i]);
					listOfLogistic[i] = (AnnieLogistic) SerializationHelper.read(is);
					is.close();
				
					listOfDims[i] = listOfLogistic[i].getModel().length - 1; //-1 because the last weight is a constant
					numberOfDims += listOfDims[i] ;
					fieldInstances[i] = new VectorInstance(new FeatureValue[listOfDims[i]]);
					datasets[i] = VectorInstance.getWekaDatasetInfo(listOfDims[i]);
					System.out.println("Dim " + i + "  " + listOfDims[i] );
				}

			}
			System.out.println("Total number of dims " + numberOfDims);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void processNullPredicates() {
		int i = 0;
		int dim = 0;
		for (int k = 0 ; k < predicates.size() ; k++) {
			if (dim >= listOfDims.length)
				throw new RuntimeException("too many null positions, or OQuery Predicates don't match Logistic Models ");
			if (listOfDims[dim] >0 && i >= listOfDims[dim]){
				i = 0;
				dim ++;
			}
			if (predicates.get(k) instanceof NullPredicate) {
				if (i != 0)
					throw new RuntimeException("Bad null position, or OQuery Predicates don't match Logistic Models ");
				predicates.set(k, null);
				listOfDims[dim] = 0;
				dim ++;
			}
			else
				i += 1;
		}
		
		if (dim  != listOfDims.length - 1){
			throw new RuntimeException("Somehow it doesn't match number of query predicates (" + getPredicates().size() + ")");
		}
	}
	
	@Override
	public List<QueryPredicate> getPredicates() {
		return super.getPredicates();
	}
	
	protected InputStream getInputStreamFromResource(String resourceURL){
		ClassLoader loader = getClass().getClassLoader();		
		return loader.getResourceAsStream(resourceURL);
	}
	
	@Override
	public OSHits search(IndexReader reader) throws IOException {		
		aggregateResult(reader);
		return getHits();
	}
	
	public void aggregateResult(IndexReader reader) throws IOException {
		aggregateResult(new OSHits(reader), reader);
	}
	
	
	
	private static final int  CHECK_POINT = 100;
	
	public void aggregateResult(OSHits result, IndexReader reader)
			throws IOException {
		this.result = result;
		List<DocFeatureIterator> featureGenIterList = new ArrayList<DocFeatureIterator>();
		for (QueryPredicate pred : featurePredicates) {
			if (pred != null)
				featureGenIterList.add( (DocFeatureIterator) pred.getInvertedListIterator(reader) );
		}
		DocFeatureIterator iterator;
		if (featureGenIterList.size() == 1){
			iterator = featureGenIterList.get(0);
		}
		else{
			iterator = new CompositeFeatureIterator(featureGenIterList);
		}
//		iterator.skipTo(1004595); //for debugging
		int lastCheckPoint = 0;
		while (iterator.next() ){
			int docId = iterator.getDocID();
			
			List<FeatureValue> featureVector = iterator.getFeatures();
//			if (docId == 2526){
//				System.out.println("Hehe !");
//			}
			double score = getRegressedScore(featureVector);
			
//			List<FeatureValue> onlyOneFeature = Arrays.asList(new FeatureValue[]{new DoubleFeatureValue(score)});
			result.addNewDocument(score, docId, featureVector );
			if (docId - lastCheckPoint > CHECK_POINT){
				System.out.print("..check point : " + docId);
				lastCheckPoint = docId;
			}
		}
		System.out.println();
		
//		result.sortByScore();
	}
	
	private double getRegressedScore(List<FeatureValue> featureValues) {
		
		double score = 1;
		int i = 0;
		int dim = 0;
		List<FeatureValue> newFeatures = new ArrayList<FeatureValue>();
		for (FeatureValue featureValue : featureValues) {
			while (dim  < listOfDims.length && listOfDims[dim] == 0)
				dim ++;
			if (dim >= listOfDims.length)
				break;
			if (i >= listOfDims[dim]){
				//regress feature group dim with a model
				double prob = probabilityScore(listOfLogistic[dim], fieldInstances[dim].toWekaInstance(datasets[dim]));
				score *= prob * listOfModelWeights[dim] + 0.5 * (1 - listOfModelWeights[dim]); //multiply with smoothed prob
				newFeatures.add(new DoubleNamedFeatureValue(listOfModelNames[dim], prob));
				i = 0;
				dim += 1;
				while (dim  < listOfDims.length && listOfDims[dim] == 0)
					dim ++;
				if (dim >= listOfDims.length)
					break;
			}
			fieldInstances[dim].setFeature(i, featureValue);
			i++;
		}
		if (i == listOfDims[dim]){
			//regress feature group dim with a model
			double prob = probabilityScore(listOfLogistic[dim], fieldInstances[dim].toWekaInstance(datasets[dim]));
			score *= prob;
			newFeatures.add(new DoubleNamedFeatureValue(listOfModelNames[dim], prob));
			i = 0;
			dim += 1;
		}
		else{
			throw new RuntimeException("More feature than expected !!! i= " + i + " dim = " + dim + " ndim = " + listOfDims[dim]);
		}
		featureValues.addAll(newFeatures);
		return score;
	}
	
	private double probabilityScore(AnnieLogistic logistic, weka.core.Instance inst) {
		try {
			double [] fDist = logistic.distributionForInstance(inst);
			return fDist[1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1.0;
		}		
	}
	
	public OSHits getHits() {
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		String queryString = "%Null() %Null() %Null() %Null() %Null() %TFFeature(Token(checkout))  %TFFeature(Token(shop shopping))  %TFFeature(Token(shipping shipped ships))  %CountNumber(Phrase(\"add to cart\")) %TFFeature(Token(availability)) ";
		OSQueryParser parser = new OSQueryParser();
		FeatureQuery osQuery = parser.parseFeatureQuery(queryString);
		int domainId = 1;
		List<ModelInfo> models = new ModelInfoManager().getModelsForDomainId(domainId);
		List<String> paths = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for (ModelInfo modelInfo : models){
			paths.add(modelInfo.getPath());
			weights.add(modelInfo.getWeight());
		}
		List<String> fieldNames = new FieldInfoManager().getFieldNamesForDomain(domainId);
		LogisticRankingQuery query = new LogisticRankingQuery(osQuery.getPredicates(), paths.toArray(new String[]{}), 
				fieldNames.toArray(new String[]{}),
				weights.toArray(new Double[]{}));
		IndexInfoManager iiMan = new IndexInfoManager();
		IndexInfo iinfo = iiMan.getIndexForId(8);
		IndexReader reader = IndexReader.open(iinfo.getIndexPath());
		
		OSHits result = query.search(reader);
		result.sortByScore();
		ResultPresenter presenter = new ResultPresenter();
		presenter.setHowManyToReturn(20);
		for (Document doc : result) {
			System.out.println("Doc : " + result.getDocID() + " , Score : " + result.score() + "\t" + result.docFeatures());
			presenter.addDocScoreFeatures(result.getDocID(), result.score(), result.docFeatures());
		}
		System.out.println(presenter.showResult(reader));
		System.out.println("Done");
	}
}
