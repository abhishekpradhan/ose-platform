package lbjse.tools;

import lbjse.search.LbjseFeatureQuery;
import lbjse.search.LbjseObjectQuery;
import lbjse.trainer.TrainingSession;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;

import LBJ2.classify.Classifier;
import LBJ2.classify.FeatureVectorReturner;
import LBJ2.learn.Learner;

import ose.database.DomainInfo;
import ose.database.DomainInfoManager;
import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.database.QueryInfo;
import ose.processor.cascader.OSHits;

import common.CommandLineOption;

public class CommandLineSearch {

	static public void searchByLuceneQuery(String query, int indexId, String outputFile) throws Exception{
		IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
		// search the index using standard Lucene API
		Query q = new QueryParser("Token", new StandardAnalyzer()).parse(query);
		IndexSearcher searcher = new IndexSearcher(iinfo.getIndexPath());
		TopDocCollector results = new TopDocCollector(100);
		searcher.search(q, results);
		OSHits hits = new OSHits(searcher.getIndexReader());
		for (ScoreDoc doc : results.topDocs().scoreDocs){
			hits.addNewDocument(doc.score, doc.doc, null);
		}
		// this will write to a text file. 
		LbjseFeatureQuery.writeResultToFile(searcher.getIndexReader(), hits, outputFile + ".txt");
		searcher.close();
		// this will convert the ranked result in text file to a html file
		RankedResultToHtml converter = new RankedResultToHtml();
		converter.convertToPageViewerOutput(outputFile + ".txt" , outputFile );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("CommandLineSearch --mode lucene --index NNN --query '???' --output ???.html \n" +
				"");
		options.require(new String[]{"query", "output","mode","index"});
		String query = options.getString("query");
		int indexId = options.getInt("index");
		String output = options.getString("output");
		String mode = options.getString("mode");
		if (mode.equals("lucene")){ //search by a lucene query
			searchByLuceneQuery(query, indexId, output);
		}
		else if (mode.equals("object")){ //search by object query (json format)
			options.require(new String[]{"domainId"});
			int domainId = options.getInt("domainId");
			QueryInfo qinfo = new QueryInfo(-1, query, "commandlinesearch", domainId);
			LbjseObjectQuery.rankQueryOnIndexOutputToFile(qinfo, indexId, output + ".txt");
			
			RankedResultToHtml converter = new RankedResultToHtml();
			converter.setIndexId(indexId);
			converter.setDomainId(domainId);
			DomainInfo domInfo = new DomainInfoManager().getDomainInfoForId(domainId);
			converter.setDomainName( domInfo.getName() );
			if (options.hasArg("nresult"))
				converter.setNumResult(options.getInt("nresult")); 
			converter.convertToPageViewerOutput(output+ ".txt" , output);
			
		}
		else if (mode.equals("session")){
			options.require(new String[]{"sessionId"});
			int sessionId = options.getInt("sessionId");
			TrainingSession trainingSession = new TrainingSession(sessionId);
			Learner classifier = trainingSession.getLearner();
			classifier.setExtractor(new FeatureVectorReturner());
			Classifier featureGen = trainingSession.getFeatureGenerator();
			System.out.println("Not implemented yet");
		}
		System.out.println("Done.");
	}
}
