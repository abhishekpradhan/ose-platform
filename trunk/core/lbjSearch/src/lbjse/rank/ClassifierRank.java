package lbjse.rank;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lbjse.data.Document;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.LazyLBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.trainer.TrainingSession;
import lbjse.utils.OrderPair;
import lbjse.utils.ProgressBar;
import lbjse.utils.Sorter;
import ose.database.lbjse.LbjseData;
import ose.database.lbjse.LbjseDataManager;

import common.CommandLineOption;

public class ClassifierRank {
	private TrainingSession session;
	private String lbjTrecFile;
	
	public ClassifierRank(int sessionId, int dataId) throws SQLException, IllegalAccessException, 
		InstantiationException, ClassNotFoundException{
		session = new TrainingSession(sessionId);
		LbjseData data = new LbjseDataManager().getDataForId(dataId);
		lbjTrecFile = data.getPath();
	}
	
	public void rank(String queryValue, String rankedResultFile) throws IOException {
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
			DocQueryPair pair = new DocQueryPairFromFile(theDoc,query);
			
			double score = session.score(pair).get("true");
			
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
		options.require(new String[]{"sessionId", "dataId", "query", "output"});
		int sessionId = options.getInt("sessionId");
		int dataId = options.getInt("dataId");
		ClassifierRank ranker = new ClassifierRank(sessionId, dataId);
		
		String query = options.getString("query");
		String output = options.getString("output");
		ranker.rank(query, output);
		System.out.println("Done output to " + output);
		
	}
}
