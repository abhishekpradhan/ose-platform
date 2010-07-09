package ose.testing;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.processor.cascader.OSHits;
import ose.retrieval.OSSearcher;
import ose.retrieval.ResultPresenter;

public class TestFeatureQuery {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String indexPath = "C:\\working\\annieIndex\\training_index";
		OSSearcher searcher = new OSSearcher(indexPath);
		String featureQuery = "%BooleanFeature(Phrase(Number_body(_range(0,4)),Or(Token(bed),Token(beds),Token(bedroom),Token(bedrooms),Token(br))))";
		OSHits result = searcher.featureSearch(featureQuery);
		
		ResultPresenter presenter = new ResultPresenter();
		presenter.setHowManyToReturn(20);
		for (Document doc : result) {
//			System.out.println("Doc : " + result.getDocID() + " , Score : " + result.score() + "\t" + result.docFeatures());
			presenter.addDocScoreFeatures(result.getDocID(), result.score(), result.docFeatures());
		}
		
		IndexReader reader = IndexReader.open(indexPath);
		System.out.println(presenter.showResult(reader));
		reader.close();
		System.out.println("Done");
	}

}
