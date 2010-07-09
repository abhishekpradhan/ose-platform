/**
 * 
 */
package ose.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;

/**
 * @author KimCuong
 *
 */
public class DocAnnotator {


	private List<Annotation> annotations;
	
	private boolean useTagSoup = true; //to clean up html
	public boolean parseError = false;
	
	public DocAnnotator(){
		annotations = new ArrayList<Annotation>();
	}
	
	public void setUseTagSoup(boolean useTagSoup) {
		this.useTagSoup = useTagSoup;
	}
	
	public List<Annotation> getSortedAnnotations() {
		Collections.sort(annotations);
		return annotations;
	}
	
	public void annotate(TrecDocument trec){
		
		if (!trec.isParsed())
			trec.parseHtml(useTagSoup);
		if (trec.isParsed()){
			annotate(IndexFieldConstant.FIELD_BODY,trec.getPlainBody());
			addAnnotation(Utils.makeAnnotation( OSToken.TOK_TITLE, trec.getTitle(), 0, 0, IndexFieldConstant.FIELD_DOCUMENT_TITLE ) );
			annotate(IndexFieldConstant.FIELD_HTMLTITLE,trec.getTitle());
			parseError = false;
		}
		else {
			System.err.println("Can not annotate " + trec.getUrl());
			parseError = true;
		}
		
	}
	
	public void annotate(String tokenField, String text){
//		System.out.println("Annotating --" + text + "--  " + tokenField);
		OSTokenizer tokenizer = new OSTokenizer(text);
		
		while (true){
			OSToken tok = tokenizer.nextToken();
			if (tok != null){
				addAnnotation(Utils.makeAnnotation(tok.getLabel(), tok.getString(), tok.getStart(), tok.getEnd(), tokenField) );
			}
			else
				break;
		}
	}
	
	private void addAnnotation(Annotation annot){
		if (annot != null)
			annotations.add(annot);
//		System.out.println("--- adding Annotation " + annot);
	}
	
	public static void print(Node node, String indent) {
        System.out.println(indent+node.getClass().getName() + " " + node.getNodeName() + " " + node.getNodeType());
        Node child = node.getFirstChild();
        while (child != null) {
            print(child, indent+" ");
            child = child.getNextSibling();
        }
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TrecFileReader reader = new TrecFileReader("test.trec");
		TrecDocument trec = reader.next();
		DocAnnotator annotator = new DocAnnotator();
		annotator.annotate(trec);
		for (Annotation annot : annotator.getSortedAnnotations()) {
			System.out.println("---- " + annot);
		}
	}
	
}
