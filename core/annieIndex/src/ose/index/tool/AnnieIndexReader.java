package ose.index.tool;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;

import ose.index.IndexFieldConstant;
import ose.index.Utils;

public class AnnieIndexReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		String indexPath=null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-index")){
				indexPath = args[++i];
			}
		}
		
		if (indexPath == null){
			System.err.println("Usage : AnnieIndexReader -index index_name");
			System.exit(1);
		}
		
		IndexReader reader = IndexReader.open(indexPath);
		System.out.println("Number of document : " + reader.numDocs());
		
		System.out.println("Top Title terms : " );
		TermEnum te = reader.terms();
		int count = 0;
		while (te.next()){
			if (te.term().field().equals(IndexFieldConstant.FIELD_BODY) ){
				if (count < 10){
					System.out.print("\t" + te.term().text());
				}
				count += 1;
			}
		}
		System.out.println("\n\tTotal terms : " + count);
		
		System.out.println("Top body terms : " );
		te.close();
		te = reader.terms();
		count = 0;
		while (te.next()){
			if (te.term().field().equals(IndexFieldConstant.FIELD_HTMLTITLE) ){
				if ( count < 10){
					System.out.print("\t" + te.term().text());
				}
				count += 1;
			}
		}
		System.out.println("\n\tTotal terms : " + count);
		System.out.println("Top numbers: " );
		TermPositions tp = reader.termPositions(new Term(IndexFieldConstant.FIELD_ANNOTATION,IndexFieldConstant.TERM_NUMBER));
		count = 0;
		while (tp.next()){
			for (int i = 0; i < tp.freq(); i++) {
				if ( count < 10 && tp.nextPosition() > 0 && tp.isPayloadAvailable()){
					byte[] data = new byte[1];
					data = tp.getPayload(data, 0);
					System.out.print("\t" + Utils.byteArrayToDouble(data));
				}
				count += 1;
			}			
		}
		System.out.println("\n\tTotal numbers : " + count);
	}

}
