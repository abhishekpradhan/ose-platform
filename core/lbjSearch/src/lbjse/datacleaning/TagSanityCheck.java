package lbjse.datacleaning;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lbj.common.WordsInBody;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LazyLBJTrecFileParser;
import ose.database.FieldInfo;
import ose.database.FieldInfoManager;
import ose.index.Number;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class TagSanityCheck {
	
	int indexId;
	static public boolean showDeleteSQL = true;
	
	public TagSanityCheck(int index) {
		indexId = index;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec", "domainId","output","indexId"});
		String trecFile = options.getString("trec");
		int domainId = options.getInt("domainId");
		int indexId = options.getInt("indexId");
		String outputResult = options.getString("output");
		TagSanityCheck checker = new TagSanityCheck(indexId);
		checker.check(trecFile, domainId, outputResult);
	}

	/**
	 * @param trecFile
	 * @param domainId
	 * @throws SQLException
	 * @throws IOException
	 */
	public void check(String trecFile, int domainId, String outputResult)
			throws SQLException, IOException {
		
		
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
		PageViewerResultManager resultMan = new PageViewerResultManager (outputResult); 
		
		
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			totalDoc += 1;
			Set<String> badTags = new HashSet<String>();
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
				}
			}

			if (badTags.size() > 0){
				String title = doc.getTitle();
				if (title == null || title.length() == 0)
					title = "[untitled]";
				String badTagList = "";
				for (String tag : badTags){
					System.out.println("\t" + tag);
					badTagList += "["+tag+"]";
					if (showDeleteSQL)
						System.out.println("DeleteSQL: " + "Delete From DocTag where IndexId = " + indexId + " AND DocId = " + doc.getDocId() + " AND Value='" + tag +"'");
				}
				resultMan.addResult(docCount , domainId , indexId, doc.getDocId(), title , badTagList);
				
				System.out.println("Document " + doc.getDocId() + " missing " + badTags.size() + " tags ");
				System.out.println("\t" + doc.getUrl());
				System.out.println("\t" + title);
				
				
				docCount += 1;
			}
		}
		resultMan.finish();
		System.out.println("Total docs : " + totalDoc);
		System.out.println("Total docs to be fixed : " + docCount);
		
	}

	static public boolean matchTagWithToken(String token, String tag) {
		if (token.equals(tag))
			return true;
		else {
			double num = Number.toNumber(token);
			if (num != Double.NaN && num == Number.toNumber(tag))
				return true;
		}
		return false;
	}
}

