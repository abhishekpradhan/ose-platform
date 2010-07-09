package ose.learning;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;

import ose.database.FeatureInfo;
import ose.database.FeatureInfoManager;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.TrainingFeedback;
import ose.database.TrainingFeedbackManager;
import ose.database.TrainingQueryInfo;
import ose.database.TrainingQueryInfoManager;
import ose.parser.OSQueryParser;
import ose.processor.cascader.FeatureQuery;
import ose.query.OQuery;
import ose.weka.ArffWriter;


public class GenerateArffForDomain {
	private int domainId;
	private int indexId;
	GenerateArffFromTrainingFeedback generator;
	
	public GenerateArffForDomain(int domainId,int indexId) throws SQLException, IOException{
		this.domainId = domainId;
		this.indexId = indexId;
		generator = new GenerateArffFromTrainingFeedback(domainId, indexId);
		
	}
	
	public void generate(String arffFilePath) throws SQLException, IOException{
		File file = new File(arffFilePath);
		if (!file.exists()){
			System.out.println("Directory not exist, create new dir : " + file.mkdirs());
		}
		List<FieldInfo> fields = new FieldInfoManager().getFieldInfoForDomain(domainId);		
		for (FieldInfo fieldInfo : fields) {
			generator.generateForField(fieldInfo.getFieldId(), arffFilePath + "/" + fieldInfo.getName() + ".arff");
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int domainId = 0;
		int indexId = 0;
		String arffFile = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-domain")){
				domainId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-index")){
				indexId = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-output")){
				arffFile = args[i+1];
			}
		}
		
		if (domainId == 0 || indexId == 0 || arffFile == null){
			System.out.println("Usage : GenerateArffFromTrainingFeedback.java -domain [domainId] -index [indexId] -field [field id] -output [arff output file]");
			System.out.println("	  : generate arff from all examples from queries in this field");
			System.exit(1);
		}
		GenerateArffForDomain generator = new GenerateArffForDomain(domainId,indexId);
		System.out.println("Hello, i'm generating training oracle feedback for domain " + domainId + " index " + indexId );	
		generator.generate(arffFile);
	}
}
