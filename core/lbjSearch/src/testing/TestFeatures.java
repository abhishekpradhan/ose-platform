package testing;

import java.util.Collection;
import java.util.Iterator;

import LBJ2.classify.FeatureVector;
import LBJ2.nlp.Word;
import LBJ2.nlp.seg.Token;

import lbj.laptop.NumericSurroundingWordsInBody_moni;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryPair;
import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.Query;
import lbjse.objectsearch.Utils;

public class TestFeatures {
	
	public boolean InTitle_moni(DocQueryPair dq){
		String fieldValue = dq.getQuery().getFieldValue("moni");
		Collection c = dq.getDoc().getTokenizedTitle();
		boolean inTitle = false;
		for (Iterator it = c.iterator(); it.hasNext(); ){
			String w = ((String) it.next()).toLowerCase();
			if (Utils.satisfyConstraint(new Word(w), fieldValue)){ 
				inTitle = true;
				break;
			}
		}
		return inTitle;
	}
	
	public boolean InTitle_brand(DocQueryPair dq) 	{
		String fieldValue = dq.getQuery().getFieldValue("brand").toLowerCase();
		Collection c = dq.getDoc().getTokenizedTitle();
		boolean inTitle = false;
		String [] values = fieldValue.split("\\s+");
		int i = 0;
		for (Iterator it = c.iterator(); it.hasNext(); ){
			String w = ((String) it.next()).toLowerCase();
			if (w.equals(values[i])) {
				i++;
				if (i == values.length){
					inTitle = true;
					break;
				}
			}
			else {
				i = 0;
			}
		} 
		return inTitle;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DocumentFromTrec doc = new DocumentFromTrec(1,"<url>",
				"PANASONIC CF-U1AQBCZAM CFU1AQBCZAM TB U1A Z520 1.33G 1GB 16GB SSHD 5.6-WSVGA WL BT XPP 2MP CAM",
				"PANASONIC CF-W7BWAZZJM TB W7 ULV U7500 1.20G 1GB 80GB DVDRW 12.1-XGA WL BT TPM1.2 VISTA \n" +
				"Product Manufaturer: PANASONIC");
		Query query = new Query();
		query.setFieldValue("moni", "_range(1,2)");
		query.setFieldValue("brand", "PANASONIC CF");
		TestFeatures test = new TestFeatures();
		DocQueryPair dq = new DocQueryPairFromFile(doc,query);
		System.out.println("InTitle_moni : " + test.InTitle_moni(dq));
		System.out.println("InTitle_brand : " + test.InTitle_brand(dq));
		
		NumericSurroundingWordsInBody_moni moni_features = new NumericSurroundingWordsInBody_moni();
		FeatureVector allFeatures = moni_features.classify(dq);
		for (Object f : allFeatures.features){
			System.out.println("\t" + f);
		}
	}

}
