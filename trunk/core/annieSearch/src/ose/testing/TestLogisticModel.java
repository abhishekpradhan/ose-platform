package ose.testing;

import java.io.InputStream;

import ose.learning.DoubleFeatureValue;
import ose.learning.VectorInstance;
import ose.query.FeatureValue;
import ose.weka.AnnieLogistic;
import weka.core.Instance;
import weka.core.SerializationHelper;

public class TestLogisticModel {
	public TestLogisticModel() {
	}
	
	private void doIt() throws Exception {
		InputStream is = getInputStreamFromResource( "model/camera/other.logistic");
		AnnieLogistic logistic = (AnnieLogistic) SerializationHelper.read(is);
		FeatureValue [] features = new FeatureValue[]{
				new DoubleFeatureValue(0),
				new DoubleFeatureValue(1),
				new DoubleFeatureValue(0),
				new DoubleFeatureValue(1),
				new DoubleFeatureValue(0),				
				};
		
		VectorInstance inst = new VectorInstance(features);
		Instance winst = inst.toWekaInstance(null);
		double [] fdist = logistic.distributionForInstance(winst);
		System.out.println("Distribution : " + fdist.length);
		for (int i = 0; i < fdist.length; i++) {
			System.out.println("\t" + fdist[i]);
		}
		System.out.println(logistic.getModel().length);
		System.out.println(logistic.getModel()[0].length);
	}
	
	protected InputStream getInputStreamFromResource(String resourceURL){
		ClassLoader loader = getClass().getClassLoader();	
		System.out.println("--- " + loader.getResource(resourceURL));
		return loader.getResourceAsStream(resourceURL);
	}
	
	public static void main(String[] args)throws Exception  {
		TestLogisticModel test = new TestLogisticModel();
		test.doIt();
	}
	
}
