/**
 * 
 */
package ose.weka;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ose.learning.DoubleFeatureValue;
import ose.learning.Instance;
import ose.learning.InstanceSet;
import ose.learning.IntegerFeatureValue;
import ose.learning.MissingFeatureValue;
import ose.query.FeatureValue;

/**
 * @author Pham Kim Cuong
 *
 */
public class ArffWriter {
	
	public ArffWriter(){
		
	}
	
	public void writeToFile(String fileName, InstanceSet instances) throws IOException{
		writeToFile(fileName, instances, null);
	}
	
	public void writeToFile(String fileName, InstanceSet instances, String comments) throws IOException{
		
		if (instances.getNumberOfInstances() == 0){
			System.err.println("Empty instace set");
			return;
		}
		
		PrintWriter writer = new PrintWriter(new FileWriter(fileName));
		
		Instance firstInstance = instances.getInstance(0);
		
		//write the header
		
		if (comments != null){
			String [] commentLines = comments.split("\n");
			int i = 1;
			for (String line : commentLines) {
				if (line.trim().length() == 0) continue;
				writer.println("% " + i + " " + line);
				i ++;
			}
			
		}
		writer.println("@RELATION test");
		for (int i = 0; i < firstInstance.getNumberOfFeatures(); i++){
			writer.println("@ATTRIBUTE x" + i + "\tNUMERIC");
		}
		writer.println("@ATTRIBUTE class\t{0,1}");
		writer.println("@DATA");
		for (int i=0 ; i<instances.getNumberOfInstances() ; i++){
			for (int j = 0; j < instances.getNumberOfFeatures(); j++) {
				FeatureValue v = instances.getInstance(i).getFeature(j);
				String t = "?";
				if (v == null || v instanceof MissingFeatureValue)
					t = "?";
				if (v instanceof DoubleFeatureValue){
					t = String.format("%.4f", v.toNumber());
				}
				else if (v != null) {
					t = v.toString();
				}
				writer.print(t + ",");
			}
			writer.println(instances.getInstance(i).getClassification());
		}
		writer.flush();
		writer.close();
	}
}
