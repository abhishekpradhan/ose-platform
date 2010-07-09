package ose.index;


import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.miscellaneous.SingleTokenTokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Payload;

public class OSEDocumentFactory {

	public Document createIndexedDocument(TrecDocument trec) {
		Document doc = new Document(); 

        Field idField = new Field(IndexFieldConstant.FIELD_DOCUMENT_ID,trec.getUrl(),
                                  Field.Store.YES,
                                  Field.Index.NO_NORMS);
        doc.add(idField);
        DocAnnotator annotator = new DocAnnotator();
        annotator.annotate(trec);
        if (annotator.parseError)
        	return null;
        for (Annotation annot : annotator.getSortedAnnotations()) {
			if (annot instanceof Number) {
				Number number = (Number) annot;
				Token tk = new Token(IndexFieldConstant.TERM_NUMBER,number.getStart(), number.getEnd());
				tk.setPayload(new Payload(Utils.doubleToByteArray(number.getNumber())));
				SingleTokenTokenStream ts = new SingleTokenTokenStream(tk);
				Field numberField = new Field(number.getIndexField(), ts);
				//add to Field._number
				doc.add(numberField);
				
//				Field tokenField = new Field(number.getIndexField(), number.getSpan().toLowerCase(), Store.NO, Index.UN_TOKENIZED);
//				//add to Field
//				doc.add(tokenField);
			}
			else if (annot instanceof Title) {
				Title base = (Title) annot;
				Field tokenField = new Field(IndexFieldConstant.FIELD_DOCUMENT_TITLE, base.getSpan(), Store.YES, Index.NO);
				doc.add(tokenField);
			}
			else if (annot instanceof AnnotationBase) { //other text tokens
				AnnotationBase base = (AnnotationBase) annot;
				Field tokenField = new Field(base.getIndexField(), base.getSpan().toLowerCase(), Store.NO, Index.UN_TOKENIZED);
				doc.add(tokenField);
			}
		}
        return doc;
	}
	
	public Document createCacheDocument(TrecDocument trec) {
		Document doc = new Document(); 

        Field idField = new Field(IndexFieldConstant.FIELD_DOCUMENT_ID,trec.getUrl(),
                                  Field.Store.YES,
                                  Field.Index.NO_NORMS);
        doc.add(idField);
        Field contentField = new Field(IndexFieldConstant.FIELD_DOCUMENT_CONTENT,trec.getContent(),
                Field.Store.YES,
                Field.Index.NO);
        doc.add(contentField);
        Field plainTextField = new Field(IndexFieldConstant.FIELD_PLAIN_BODY,trec.getPlainBody(),
                Field.Store.YES,
                Field.Index.NO);
        doc.add(plainTextField);
        Field titleField = new Field(IndexFieldConstant.FIELD_DOCUMENT_TITLE,trec.getTitle(),
                Field.Store.YES,
                Field.Index.NO);
        doc.add(titleField);
        return doc;
	}
}
