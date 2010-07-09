package lbjse.rank;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LBJ2.classify.Classifier;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.FeatureVectorReturner;
import LBJ2.classify.TestDiscrete;
import LBJ2.learn.Learner;

import lbjse.data.Document;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.LazyLBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.objectsearch.Utils;
import lbjse.tools.RankedResultToHtml;
import lbjse.trainer.TrainingSession;
import lbjse.utils.OrderPair;
import lbjse.utils.ProgressBar;
import lbjse.utils.Sorter;
import ose.database.DomainInfo;
import ose.database.DomainInfoManager;
import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;

import common.CommandLineOption;

public class PairwiseRank {
	private TrainingSession session;
	private String lbjTrecFile;
	
	public PairwiseRank(int sessionId, int dataId) throws SQLException, IllegalAccessException, 
		InstantiationException, ClassNotFoundException{
		session = new TrainingSession(sessionId);
		LbjseData data = new LbjseDataManager().getDataForId(dataId);
		lbjTrecFile = data.getPath();
	}
	
	public PairwiseRank(int sessionId, String trecFile) throws SQLException, IllegalAccessException, 
		InstantiationException, ClassNotFoundException{
		session = new TrainingSession(sessionId);
		lbjTrecFile = trecFile;
	}
	
	public void rank(String queryValue, String rankedResultFile) throws IOException {
		Learner classifier = session.getLearner();
		classifier.setExtractor(new FeatureVectorReturner());
		Classifier featureGen = session.getFeatureGenerator();
		LazyLBJTrecFileParser trecParser = new LazyLBJTrecFileParser(lbjTrecFile);
		Query query = new Query(session.getSession().getDomainId());
		query.setFieldValue(session.getSession().getFieldId(), queryValue);
		ArrayList<ResultItem> allItems = new ArrayList<ResultItem>();
		Sorter sorter = new Sorter();
		int i = 0;
		while (true){
			DocumentFromTrec theDoc = (DocumentFromTrec) trecParser.next();
			if (theDoc == null)
				break;
			if (! "other".equals( query.getFieldNameFromId(session.getSession().getFieldId() ) ) )
				if (theDoc.getTagForField("other").size() > 0) continue; //just evaluate for object pages.
			
			DocQueryPair pair = new DocQueryPairFromFile(theDoc,query);
			
			FeatureVector features = Utils.pointwiseFeatureVector(featureGen,pair, classifier);
			double score = classifier.scores(features).get("true");
			
			ResultItem item = new ResultItem(theDoc.getDocId(), theDoc.getUrl(), theDoc.getTitle(), 
					score, pair.oracle() );
			allItems.add(item);
			sorter.addPair(new OrderPair(score, i));
			i += 1;
			ProgressBar.printDot(i, 10, 200);
		}
		
		List<OrderPair> sorted = sorter.getOrderedList();
		Collections.reverse(sorted);
		PrintStream stream = new PrintStream(rankedResultFile, "utf-8");
		for (OrderPair orderPair : sorted) {
			int id = orderPair.getId();
			stream.println(allItems.get(id));
		}
		
		stream.close();
	}
	
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"sessionId", "data", "query", "output", "index"});
		int sessionId = options.getInt("sessionId");
		
		PairwiseRank ranker = null;
		int dataId;
		try {
			dataId = options.getInt("data");
			ranker = new PairwiseRank(sessionId, dataId);
		} catch (NumberFormatException e) {
			ranker = new PairwiseRank(sessionId, options.getString("data"));
		}
		
		String query = options.getString("query");
		String output = options.getString("output");
		ranker.rank(query, output + ".txt");
		RankedResultToHtml converter = new RankedResultToHtml();
		converter.setIndexId(options.getInt("index"));
		converter.setDomainId(ranker.session.getSession().getDomainId());
		DomainInfo domInfo = new DomainInfoManager().getDomainInfoForId(ranker.session.getSession().getDomainId());
		converter.setDomainName( domInfo.getName() );
		if (options.hasArg("nresult"))
			converter.setNumResult(options.getInt("nresult")); 
		converter.convertToPageViewerOutput(output+ ".txt" , output);
		
		System.out.println("Done output to " + output);
		
	}
}
