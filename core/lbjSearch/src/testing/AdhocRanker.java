package testing;

import java.io.IOException;
import java.sql.SQLException;

import lbj.professor.SurroundingWordsInBody_area;
import lbjse.data.DocumentFromTrec;
import lbjse.datacleaning.PageViewerResultManager;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LazyLBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.utils.ProgressBar;
import ose.index.Number;
import LBJ2.classify.FeatureVector;

import common.CommandLineOption;

public class AdhocRanker {
	
	int indexId;
	static public boolean showDeleteSQL = true;
	
	public AdhocRanker(int index) {
		indexId = index;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec", "domainId","output","indexId","mode"});
		String trecFile = options.getString("trec");
		int domainId = options.getInt("domainId");
		int indexId = options.getInt("indexId");
		String outputResult = options.getString("output");
		if ("byfeature".equals(options.getString("mode"))){
			AdhocRanker ranker = new AdhocRanker(indexId);
			ranker.rankByFeature(trecFile, domainId, outputResult);	
		}
	}

	/**
	 * @param trecFile
	 * @param domainId
	 * @throws SQLException
	 * @throws IOException
	 */
	public void rankByFeature(String trecFile, int domainId, String outputResult)
			throws IOException {
		
		LazyLBJTrecFileParser parser = new LazyLBJTrecFileParser(trecFile);
		Query query = new Query(domainId);
		query.setFieldValue("area", "wireless networking");
		SurroundingWordsInBody_area feature = new SurroundingWordsInBody_area();
		int docCount = 0;
		int totalDoc = 0;
		PageViewerResultManager resultMan = new PageViewerResultManager (outputResult); 
		
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			totalDoc += 1;
			ProgressBar.printDot(totalDoc, 20, 400);
//			if (doc.getTagForField("area").size() == 0 )
//				continue; //skip those that we didn't annotate
			FeatureVector fvector = feature.classify(new DocQueryPairFromFile(doc, query));
			boolean qualified = false;
			for (Object df : fvector.features){
				qualified = true;
				break;
			}

			if (qualified){
				String title = doc.getTitle();
				if (title == null || title.length() == 0)
					title = "[untitled]";
				resultMan.addResult(docCount , domainId , indexId, doc.getDocId(), title , "");
				docCount += 1;
				System.out.println(doc.getDocId() + "\t" + title);
			}
		}
		System.out.println("Total docs : " + totalDoc);
		System.out.println("Total result: " + docCount);
		resultMan.finish();
	}

}

