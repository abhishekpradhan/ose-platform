/**
 * 
 */
package ose.learning;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
public class TrainLogisticModels {

	AnnieLogistic learner;
	Instances data ;
	private boolean instanceBalancing;
	private String arffDir;
	private String modelDir;
	
	TrainLogisticModels(String arffPath , String modelPath){
		arffDir = arffPath;
		modelDir = modelPath;
		File model = new File(modelPath);
		if ( !model.exists()){
			model.mkdirs();
		}
	}
	
	public void setInstanceBalancing(boolean instanceBalancing) {
		this.instanceBalancing = instanceBalancing;
	}
	
	void doIt(int fieldId, String arffFileName,String modelFileName) throws Exception{
		System.out.println("Training on " + arffFileName );
		System.out.println("Path = " + System.getProperty("user.dir") );

		DataSource source= null;
		try {
			source = new DataSource(arffDir + "/" + arffFileName);
			data = source.getDataSet();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("No arff for " + fieldId + " " + arffFileName);
			return;
		}
		
		
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		System.out.println("Number of instances : " + data.numInstances());
		
		Remove removeFilter = new Remove();
		removeFilter.setAttributeIndices("1");
		
		int[] exCount = new int[]{0,0};
		
		for (int i = 0 ; i < data.numInstances() ; i++) {
			exCount[(int)data.instance(i).classValue()] += 1;
		}
		System.out.println("Counts : " + exCount[0] + "," + exCount[1]);
		if (instanceBalancing){
			/* Changing weights */
			for (int i = 0 ; i < data.numInstances() ; i++) {
				Instance inst = data.instance(i);
				if (inst.classValue() == 1){
					System.out.println("Changing weight from " + inst.weight() + " to " + ((double)exCount[0])/exCount[1]);
					inst.setWeight(((double)exCount[0])/exCount[1]);
				}
			}
		}
		
		String[] options = Utils.splitOptions("-R 0.01 -M -1");
		learner = new AnnieLogistic();
		learner.setOptions(options);     // set the options
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(removeFilter);
		fc.setClassifier(learner);
		
		fc.buildClassifier(data);   // build classifier		
		System.out.println("++++ " + fc.getClassifier().toString());	
		Evaluation eTest = new Evaluation(data);
		eTest.evaluateModel(fc, data);
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		System.out.println(eTest.toMatrixString());
		System.out.println("Updating model weight : " + eTest.errorRate() );
		String modelFilePath = modelDir + "/" + modelFileName;
		ModelInfoManager dbMan = new ModelInfoManager();
		ModelInfo modelInfo = dbMan.getModelInfoForFieldId(fieldId);
		if (modelInfo == null){
			modelInfo = new ModelInfo(-1,fieldId, modelFilePath, 1 - eTest.errorRate());
			dbMan.insert(modelInfo);
			System.out.println("New weight is inserted for field " + fieldId);
		}
		else{
			modelInfo.setWeight(1 - eTest.errorRate());
			dbMan.update(modelInfo);
			System.out.println("New weight is updated for field " + fieldId);
		}
		
		Instance instance = data.instance(0);
//		instance = learner.getPreprocessedInstance(instance);
		
		double [][] model = learner.getModel();
		System.out.println("Model size : " + model.length + " " + model[0].length);
		System.out.println("Processed : " + instance.toString());
//		System.out.println("Classification " + learner.classifyInstance(instance));
		for (int i = 0 ; i < instance.numAttributes() - 2 ; i++){
			String featureName = instance.attribute(i).name();
			int featureNum = Integer.parseInt(featureName.substring(1));
			try {
				System.out.println(instance.attribute(i).name() + " x " + model[i + 1][0] + "\t" + featureNum );
			}
			catch (java.lang.ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				System.out.println("Check attribute removal");
			}
		}
		double pred = fc.classifyInstance(instance);
		System.out.println("ID : " + instance.value(0));
		System.out.print(", actual: " + data.classAttribute().value((int) instance.classValue()));
		System.out.println(", predicted: " + data.classAttribute().value((int) pred));

		System.out.print("Writing model to " + modelFilePath + "...");
		SerializationHelper.write(modelFilePath, learner);
		System.out.println("done.");
		
//		learner.pruneModel(1.0);
//		System.out.println("++++ " + learner.toString());	
//		eTest = new Evaluation(data);
//		eTest.evaluateModel(learner, data);
//		strSummary = eTest.toSummaryString();
//		System.out.println(strSummary);
//		System.out.println(eTest.toMatrixString());

		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int domainId = 0;
		boolean instanceBalance = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-balance")){
				instanceBalance = true;
			}
			
		}
		if (domainId == 0){
			System.out.println("Usage : TrainLogisticModels.java -domain [domainId]");
			System.exit(1);
		}
		
		DomainInfo domainInfo = new DomainInfoManager().getDomainInfoForId(domainId);
		if (domainInfo == null){
			System.err.println("No domain found !");
			return;
		}
		System.out.println("Domain : " + domainInfo.getName() );
		System.out.println("Description : " + domainInfo.getDescription() );
		TrainLogisticModels test = new TrainLogisticModels("arff/" + domainInfo.getName(),"resources/model/" + domainInfo.getName());
		test.setInstanceBalancing(instanceBalance);
				
		try {
			List<FieldInfo> fields = new FieldInfoManager().query("select * from FieldInfo where domainId = " + domainId);
			for (FieldInfo fieldInfo : fields) {
				test.doIt(fieldInfo.getFieldId(), fieldInfo.getName() + ".arff", fieldInfo.getName() + ".logistic");
			}
//			test.doIt(43, "area" + ".arff", "area" + ".logistic");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
