/**
 * 
 */
package ose.testing;


import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
/**
 * @author Pham Kim Cuong
 *
 */
public class TestWeka {

	TestWeka(){
		
	}
	
	void doIt() throws Exception{
		System.out.println("Path = " + System.getProperty("user.dir") );
		DataSource source = new DataSource("cpu.arff");
		Instances data = source.getDataSet();
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
		
		System.out.println("Number of instances : " + data.numInstances());
		
		String[] options = Utils.splitOptions("-S 3 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.0010 -P 0.1");
//		LinearRegression learner = new LinearRegression();
		LibSVM learner = new LibSVM();
		learner.setOptions(options);     // set the options
		learner.buildClassifier(data);   // build classifier
		
		for (int i = 0 ; i < data.numInstances() ; i++) {
			Instance inst = data.instance(i);
			double t = learner.classifyInstance(inst);
//			System.out.println("Output t = " + t + " ... " + inst.classValue());
			System.out.println("\t" + t + "\t" + inst.classValue());
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestWeka test = new TestWeka();
		try {
			test.doIt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
