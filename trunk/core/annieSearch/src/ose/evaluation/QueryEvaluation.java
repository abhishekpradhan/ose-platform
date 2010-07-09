package ose.evaluation;

import org.json.JSONArray;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryInfo;
import ose.database.QueryInfoManager;
import ose.retrieval.ObjectRankingEvaluation;
import ose.retrieval.ObjectSearcher;
import ose.retrieval.RankingEvaluation;
import ose.retrieval.ResultPresenter;

public class QueryEvaluation {

	public QueryEvaluation() {
		// TODO Auto-generated constructor stub
	}
	
	private void evaluate(int domainId, int indexId, int queryId) throws Exception{
		QueryInfo queryInfo = new QueryInfoManager().queryByKey(queryId);
		String objectQuery = queryInfo.getQueryString();
		
		ObjectSearcher servlet = new ObjectSearcher();
		ResultPresenter resultPrinter = servlet.getResultForQuery(indexId, domainId, objectQuery);
		
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		RankingEvaluation evaluation = new ObjectRankingEvaluation(queryId, indexId);
		JSONArray jsonItems = new JSONArray();
		Integer [] rankedDocIds = resultPrinter.getRankedList(iinfo.getIndexPath(), indexId, jsonItems);
		evaluation.evaluate(rankedDocIds);
		evaluation.prettyPrint();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		int indexId = 1201;
		int domainId = 2;
		int queryId = 224;
		
		QueryEvaluation eval = new QueryEvaluation();
		eval.evaluate(domainId, indexId, queryId);
	}

	
}
