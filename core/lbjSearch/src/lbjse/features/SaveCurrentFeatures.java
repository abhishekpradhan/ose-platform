package lbjse.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.CommandLineOption;

import ose.database.lbjse.LbjseSearchFeature;
import ose.database.lbjse.LbjseSearchFeatureManager;
import ose.database.lbjse.LbjseTrainingSession;
import ose.database.lbjse.LbjseTrainingSessionManager;

public class SaveCurrentFeatures {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"session", "featureFile"});
		int sessionId = options.getInt("session");
		String featureFileName = options.getString("featureFile");
		BufferedReader reader = new BufferedReader(new FileReader(featureFileName));
		LbjseSearchFeatureManager man = new LbjseSearchFeatureManager();
		Set<String> existedFeatures = new HashSet<String>();
		for (LbjseSearchFeature feature : man.getFeaturesForSession(sessionId)){
			existedFeatures.add(feature.getValue());
		}
	
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			if (line.trim().length() == 0)
				continue;
			LbjseSearchFeature feature = new LbjseSearchFeature(-1, sessionId, line.trim());
			if (existedFeatures.contains(feature.getValue())){
				System.out.println("skipped " + feature);
			}
			else {
				man.insert(feature);
				System.out.println("Inserted :" + feature);
			}
		}
	}

}
