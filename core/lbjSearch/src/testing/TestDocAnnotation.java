package testing;

import lbjse.objectsearch.DocAnnotations;
import lbjse.objectsearch.DocumentFromDatabase;

public class TestDocAnnotation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DocAnnotations annotMan = new DocAnnotations(1,8);
		DocumentFromDatabase doc = new DocumentFromDatabase(1,annotMan);
		System.out.println(doc.getBody());
	}

}
