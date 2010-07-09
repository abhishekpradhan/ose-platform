/**
 * 
 */
package ose.learning;

import ose.query.FeatureValue;


/**
 * @author Pham Kim Cuong
 *
 */
public class DocumentInstance extends VectorInstance {
	
	int docID;
	
	public DocumentInstance(int docID, FeatureValue[] features) {
		super(features);
		this.docID = docID;
	}
	
	@Override
	public String toString() {
		return "Doc " + docID + " " + super.toString();
	}
}
