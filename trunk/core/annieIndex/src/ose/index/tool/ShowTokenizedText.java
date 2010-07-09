package ose.index.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import common.CommandLineOption;

import ose.index.Annotation;
import ose.index.AnnotationBase;
import ose.index.DocAnnotator;
import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;
import ose.index.TrecFileReader;

public class ShowTokenizedText {

	private List<Annotation> annots ;
	
	public void initializeDocFromCache(String cachePath, int docid) {
		TrecDocument trecDoc = getTrecDocumentFromCache(cachePath, docid);
		DocAnnotator annotator = new DocAnnotator();
//		annotator.setUseTagSoup(false);
		annotator.annotate(trecDoc);
		annots = annotator.getSortedAnnotations();
	}
	
	public void initializeDocFromTrec(String trecPath, int docid) throws FileNotFoundException {
		TrecDocument trecDoc = getTrecDocumentFromTrec(trecPath, docid);
		DocAnnotator annotator = new DocAnnotator();
//		annotator.setUseTagSoup(false);
		annotator.annotate(trecDoc);
		annots = annotator.getSortedAnnotations();
	}

	/**
	 * @param cachePath
	 * @param docid
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private TrecDocument getTrecDocumentFromCache(String cachePath, int docid)
			 {
		try {
			IndexReader cacher = IndexReader.open(cachePath);
			String url = cacher.document(docid).get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			String content = cacher.document(docid).get(IndexFieldConstant.FIELD_BODY);
			System.out.println("done reading from cache, size " + content.length() + " url " + url);
			if (content.length() > 100000)
				throw new IOException("File too large");
			TrecDocument trecDoc = new TrecDocument();
			trecDoc.setUrl(url);
			trecDoc.setContent(content);
			return trecDoc;
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private TrecDocument getTrecDocumentFromTrec(String trecPath, int docid) throws FileNotFoundException {
		TrecFileReader reader = new TrecFileReader(trecPath);
		TrecDocument trec = null;
		for (int i = 0; i <= docid; i++) {
			trec = reader.next();
			if (trec == null)
				break;
		}
		return trec;
	}	
	
	public List<String> getTokenizedField(String fieldName){
		List<String> result = new ArrayList<String>();
		int i = 0;
		for (Annotation annot : annots) {
			AnnotationBase annotBase = (AnnotationBase) annot;
			if (fieldName == null || annotBase.getIndexField().equals(fieldName))
				result.add(i + " " + annotBase.getSpan());
			i += 1;
		}
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption option = new CommandLineOption(args);
		option.require(new String[]{"doc"});
		int doc = option.getInt("doc");
		String indexPath = option.getString("index");
		String trecPath = option.getString("trec");
		ShowTokenizedText prog = new ShowTokenizedText();
		if (indexPath != null){			
			prog.initializeDocFromCache(indexPath, doc);
		}
		else{
			prog.initializeDocFromTrec(trecPath, doc);
		}
		
		for (String token : prog.getTokenizedField(null)) {
			System.out.println(token);
		}
		
	}

}
