package testing;

import java.util.Iterator;
import java.util.List;

import lbj.professor.dept_features;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import lbjse.objectsearch.Utils;
import LBJ2.classify.RealFeature;

public class TestPairFeatures {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		dept_features featureGen = new dept_features();
		LBJTrecFileParser parser = new LBJTrecFileParser("c:\\working\\lbjSearch\\professor_30000.tagtrec");
		Query query = new Query(2);
		query.setFieldValue("dept", "computer");
		DocumentFromTrec docNo = (DocumentFromTrec ) parser.next();
		DocumentFromTrec docYes = (DocumentFromTrec ) parser.next();
		DocQueryPairFromFile pairYes = new DocQueryPairFromFile(docYes, query);
		DocQueryPairFromFile pairNo = new DocQueryPairFromFile(docNo, query);
		System.out.println("------------- Example 1 ---------------");
		
		List diffFeatures = Utils.differentiateFeatures(featureGen,
				pairYes, pairNo);
		
		System.out.println("------------- Diff Features ---------------");
		for (Iterator it = diffFeatures.iterator(); it.hasNext(); ){
			RealFeature f = (RealFeature) it.next();
			System.out.println("\t" + f.getValue() + "\t" + f.getIdentifier() );
		}
		
	}
}
