package lbjse.atl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import lbj.common.WordsInBody_InDict;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.Query;
import lbjse.objectsearch.WordDict;
import ose.learning.VectorInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import LBJ2.classify.Feature;
import LBJ2.classify.FeatureVector;

public class ExportToWeka {
	
	private WordDict dict = new WordDict();
	private Instances dataset;
	String[] wordArrays;
	
	public ExportToWeka() {
		wordArrays = WordDict.getWordSet().toArray(new String[]{});
		dataset = VectorInstance.getWekaDatasetInfo(wordArrays.length);
		for (int i = 0; i < wordArrays.length; i++) {
			System.out.println(i + "\t" + wordArrays[i]);
		}
	}
	
	public void export() throws IOException{
		DocQueryFileParser data = new DocQueryFileParser("annotated_docs_index_30000_domain_2.trec","query.txt");
		WordsInBody_InDict featureExtractor = new WordsInBody_InDict();
		DocQueryPairFromFile pair = null;
		Instances instSet = new Instances(dataset);
		Instances physicsSet = new Instances(dataset);
		
		while ( (pair = (DocQueryPairFromFile) data.next()) != null){
			FeatureVector features = featureExtractor.classify(pair);
			Instance wekaInstance = convertFeatureVectorToWekaInstance(features);
			if (pair.oracle())
				wekaInstance.setClassValue(1);
			else
				wekaInstance.setClassValue(0);
			Query query = pair.getQuery();
			query.setFieldValue("dept", "physics");
			if (pair.oracle())
				physicsSet.add(wekaInstance);
			else
				instSet.add(wekaInstance);
			System.out.print(".");
		}
		instSet.randomize(new Random(System.currentTimeMillis()));
		System.out.println();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instSet);
		saver.setFile(new File("lbjTrain.arff"));
		saver.writeBatch();		
		System.out.println("Exported : " + instSet.numInstances());
		
		saver.setInstances(physicsSet);
		saver.setFile(new File("lbjTest.arff"));
		saver.writeBatch();
		System.out.println("Exported : " + physicsSet.numInstances());
		System.out.println("Done");
	}
	
	private Instance convertFeatureVectorToWekaInstance(FeatureVector features) {
		Set<String> activeWords = new HashSet<String>();
		Iterator<Object> fiter = features.features.iterator();
		
		while (fiter.hasNext()){
			Feature f = (Feature)fiter.next();
			activeWords.add(f.getStringValue());
		}
		
		Instance inst = new Instance(wordArrays.length + 1);
		inst.setDataset(dataset);
		
		for (int i = 0; i < wordArrays.length; i++) {
			if (activeWords.contains(wordArrays[i])){
				inst.setValue(i, 1.0);
			}
			else {
				inst.setValue(i,0);
			}
		}
		return inst;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ExportToWeka p = new ExportToWeka();
		p.export();
	}
}
