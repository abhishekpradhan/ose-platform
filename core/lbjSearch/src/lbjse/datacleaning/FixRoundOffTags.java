package lbjse.datacleaning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LazyLBJTrecFileParser;
import lbj.common.WordsInBody;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.index.Number;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class FixRoundOffTags {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec", "domainId"});
		String trecFile = options.getString("trec");
		int domainId = options.getInt("domainId");
		FieldInfoManager man = new FieldInfoManager();
		List<String> fieldNames = new ArrayList<String>();
		for (FieldInfo fieldInfo : man.getFieldInfoForDomain(domainId)){
			if ("other".equals( fieldInfo.getType() ) )
				continue;
			if ("text".equals( fieldInfo.getType() ) )
				continue;
			fieldNames.add(fieldInfo.getName());
			System.out.println("Checking field : " + fieldInfo);
		}
		
		LazyLBJTrecFileParser parser = new LazyLBJTrecFileParser(trecFile);
		WordsInBody tokenGenerator = new WordsInBody();
		int docCount = 0;
		int totalDoc = 0;
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			totalDoc += 1;
			Set<String> badTags = new HashSet<String>();
			Map<String, String> veryCloseTags = new HashMap<String, String>();
			for (String fieldName : fieldNames){
				for (String tag : doc.getTagForField(fieldName)){
					badTags.add(tag);
				}
			}
			
			FeatureVector fvector = tokenGenerator.classify(new DocQueryPairFromFile(doc, null));
			for (Object df : fvector.features){
				DiscreteFeature dfeature = (DiscreteFeature) df;
				for (String tag : badTags.toArray(new String[]{})){
					if (matchTagWithToken(dfeature.getValue(), tag)){
						badTags.remove(tag);
					}
					if (tagVeryCloseToToken(dfeature.getValue(), tag)){
						veryCloseTags.put(tag, dfeature.getValue());
					}
				}
			}

			if (badTags.size() > 0){
				System.out.println("Document " + doc.getDocId() + " missing " + badTags.size() + " tags ");
				System.out.println("\t" + doc.getUrl());
				System.out.println("\t" + doc.getTitle());
				for (String tag : badTags){
					System.out.println("\t" + tag);
					if (veryCloseTags.containsKey(tag)){
						String newTag = veryCloseTags.get(tag);
						System.out.println("\t\tchange to this " + newTag);
						System.out.println("SQL : Update DocTag Set Value = '" + newTag + "' where IndexId = 301 and DocId = " + doc.getDocId() + " and Value = '" + tag + "'" );
					}
				}				
				docCount += 1;
			}
		}
		System.out.println("Total docs : " + totalDoc);
		System.out.println("Total docs to be fixed : " + docCount);
	}

	static private boolean matchTagWithToken(String token, String tag) {
		if (token.equals(tag))
			return true;
		else {
			double num = Number.toNumber(token);
			if (num != Double.NaN && num == Number.toNumber(tag))
				return true;
		}
		return false;
	}
	
	static private boolean tagVeryCloseToToken(String token, String tag) {
		if (!token.equals(tag) && token.startsWith(tag))
			return true;
		return false;
	}
}
