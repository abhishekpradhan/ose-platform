package ose.index.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;

import ose.index.Annotation;
import ose.index.AnnotationBase;

public class DocumentReconstruction {

	
	public DocumentReconstruction() {
	}
	
	public String reconstruct(String indexPath, int docId) throws IOException{
		
		IndexReader reader = IndexReader.open(indexPath);
		List<Annotation> tokens = getSortedListOfTokens(reader, docId);
		reader.close();
		StringBuffer buffer = new StringBuffer();
		for (Annotation annotation : tokens) {
//			buffer.append(annotation.getStart() + " : " + annotation.getSpan() + "\n");
			System.out.println(annotation.getStart() + " : " + annotation.getSpan());
		}
		return buffer.toString();
	}
	
	private List<Annotation> getSortedListOfTokens(IndexReader reader, int docId) throws IOException{
		List<Annotation> result = new ArrayList<Annotation>();
		TermEnum termEnum = reader.terms();
		while (termEnum.next()){
			Term term = termEnum.term();
			System.out.println("processing " + term);
			result.addAll( getAllAnnotationForTerm(reader, docId, term));
		}
		Collections.sort(result);
		return result;
	}
	
	private List<Annotation> getAllAnnotationForTerm(IndexReader reader, int docId, Term term ) throws IOException{
		List<Annotation> result = new ArrayList<Annotation>();
		TermPositions termPos = reader.termPositions(term);
		if (termPos.skipTo(docId) && termPos.doc() == docId){
			for (int i = 0; i < termPos.freq(); i++) {
				int pos = termPos.nextPosition();
				AnnotationBase annot = new AnnotationBase(term.text(), pos,pos,term.field() );
				result.add(annot);
			}
		}
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String indexPath = "C:\\working\\annieIndex\\combine_testing_index";
		DocumentReconstruction res = new DocumentReconstruction();
		System.out.println( res.reconstruct(indexPath, 829));
	}

	
}
