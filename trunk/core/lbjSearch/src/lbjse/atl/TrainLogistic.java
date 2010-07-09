/**
 * 
 */
package lbjse.atl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbjse.objectsearch.WordDict;
import lbjse.utils.OrderPair;
import lbjse.utils.Sorter;

import ose.database.DomainInfo;
import ose.database.DomainInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.ModelInfo;
import ose.database.ModelInfoManager;
import ose.weka.AnnieLogistic;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;

/**
 * @author Pham Kim Cuong
 *
 */
public class TrainLogistic {

	AnnieLogistic learner;
	Instances data ;
	Instances testData;
	
	private String arffPath;
	private String modelPath;
	static String [] wordArray = WordDict.getWordSet().toArray(new String[]{});
	Set<String> tabooWords;
	
	TrainLogistic(String arffPath , String modelPath) throws Exception {
		this.arffPath = arffPath;
		this.modelPath = modelPath;
		File model = new File(modelPath);
//		if ( !model.exists()){
//			model.mkdirs();
//		}
		tabooWords = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader("overfit.txt"));
		String line = null;
		while ( (line = reader.readLine()) != null){
			tabooWords.add(line.trim());
			System.out.println("Found taboo word " + line.trim());
		}
	}
	
	void doIt() throws Exception{
		System.out.println("Training on " + arffPath );
		System.out.println("Path = " + System.getProperty("user.dir") );

		DataSource source= null;
		arffPath = arffPath;
		try {
			source = new DataSource(arffPath );
			data = source.getDataSet();
			
			source = new DataSource("lbjTest.arff" );
			testData = source.getDataSet();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("No arff found ");
			return;
		}
		
		
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		if (testData.classIndex() == -1)
			testData.setClassIndex(testData.numAttributes() - 1);
		
		System.out.println("Number of instances : " + data.numInstances());
		
		String[] options = Utils.splitOptions("-R 0.01 -M -1");
		learner = new AnnieLogistic();
		learner.setOptions(options);     // set the options
		setTabooWeights(data);
		learner.buildClassifier(data);   // build classifier		
//		System.out.println("++++ " + learner.toString());
		System.out.println("---- Training performace");
		showEvaluation(data);
		System.out.println("---- Testing performace");
		showEvaluation(testData);

		System.out.print("Writing model to " + modelPath + " ...");
		SerializationHelper.write(modelPath, learner);
		System.out.println("done.");
		
	}

	/**
	 * @throws Exception
	 */
	private void showEvaluation(Instances data) throws Exception {
		Evaluation eTest = new Evaluation(data);
		eTest.evaluateModel(learner, data);
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		System.out.println(eTest.toMatrixString());
	}
	
	private void setTabooWeights(Instances data) {
		for (int i = 0 ; i < data.numInstances(); i ++){
			Instance inst = data.instance(i);
			double weight = 1.0;
			for (int j = 0 ; j < inst.numAttributes() - 1 ;  j++){
				if (inst.value(j) > 0 && tabooWords.contains(wordArray[j])){
					System.out.println("demote instance " + i + " because of word : " + wordArray[j]);
					weight *= 2;
					inst.setValue(j, 0.0);
				}
			}
//			inst.setWeight(weight);
		}
	}
	
	private void showModel() throws Exception{
		learner = (AnnieLogistic) SerializationHelper.read(modelPath);
		System.out.println(learner.getModel()[0].length);
		double [][] rawModel = learner.getModel();
		
		Sorter sort = new Sorter();
		for (int i = 0; i < rawModel.length; i++) {
			if (i > 0)
				sort.addPair(new OrderPair(new Double(rawModel[i][0]), i-1));
		}
		List<OrderPair> sorted = sort.getOrderedList();
		for (OrderPair pair : sorted) {
			System.out.println(pair.getValue() + "\t" + pair.getId() + "\t" + wordArray[pair.getId()]);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TrainLogistic logistic = new TrainLogistic("lbjTrain.arff","lbj.logistic");
		logistic.doIt();
		logistic.showModel();
	}
}



