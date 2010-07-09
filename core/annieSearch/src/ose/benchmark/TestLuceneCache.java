package ose.benchmark;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;

/*
 * This is to test if Lucene does some caching behind TermPositions.
 * THe result is almost NO. Thus accessing multiple termPositions in parallel is almost the same as sequential. 
 */
public class TestLuceneCache {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String indexPath = "C:\\working\\annieIndex\\combine_training_index";

		IndexReader reader = IndexReader.open(indexPath);
		
		int N = 10;
		TermPositions [] tp = new TermPositions[N];
		for (int i = 0 ; i < N; i ++)
			tp[i] = reader.termPositions(new Term("Token","_number"));
		
		long count = 0;
		long start= System.currentTimeMillis();
		for (int i = 0 ; i < N; i ++){
			while (tp[i].next()){
				for (int j = 0 ; j < tp[i].freq();j++){
					count += tp[i].nextPosition();
				}
			}
		}
		System.out.println("Result : " + count);
		System.out.println("Time : " + (System.currentTimeMillis() - start) );

		for (int i = 0 ; i < N; i ++)
			tp[i] = reader.termPositions(new Term("Token","_number"));
		count = 0;
		start= System.currentTimeMillis();
		while (true){
			boolean stop = true;
			for (int i = 0 ; i < N; i ++)
				if (tp[i].next()){
					stop = false;
					for (int j = 0 ; j < tp[i].freq();j++){
						count += tp[i].nextPosition();
				}
			}
			if (stop) break;
		}
		
		System.out.println("Result : " + count);
		System.out.println("Time : " + (System.currentTimeMillis() - start) );
	}

}
