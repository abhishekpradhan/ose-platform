package ose.processor.cascader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ose.learning.DoubleFeatureValue;
import ose.learning.IntegerFeatureValue;
import ose.query.FeatureValue;

public class DivideByFeatureIterator implements DocFeatureIterator {

	protected DocFeatureIterator nominatorIterator, denominatorIterator;
	protected Double nomiValue, denomValue;
	private int nomiDocId, denomDocId;
	public DivideByFeatureIterator(DocFeatureIterator nomIterator, DocFeatureIterator denomIterator) throws IOException{
		if (nomIterator.getNumberOfFeatures() != 1 ||
			denomIterator.getNumberOfFeatures() != 1 )
			throw new IOException("Bad nominator/denominator iterators");
		
		nominatorIterator = nomIterator;
		denominatorIterator = denomIterator;
		nomiValue = null;
		denomValue = null;
	}
	
	public List<FeatureValue> getFeatures() throws IOException {
		List<FeatureValue> result = new ArrayList<FeatureValue>();
		if (nomiValue == null || nomiDocId != denomDocId){
			result.add(new DoubleFeatureValue(0));
		}
		else{
			result.add(new DoubleFeatureValue(nomiValue / denomValue));
		}
		return result;
	}

	public int getNumberOfFeatures() {
		return 1;
	}

	public String getClue() {
		return "=>" + nomiValue + " / " + denomValue;
	}

	public int getDocID() {
		return denominatorIterator.getDocID();
	}

	public boolean next() throws IOException {
		if (denominatorIterator.next()){
			denomValue = denominatorIterator.getFeatures().get(0).toNumber();
			denomDocId = denominatorIterator.getDocID();
			if (nomiValue == null || nomiDocId < denomDocId){
				if (nominatorIterator.skipTo(denomDocId)){
					nomiDocId = nominatorIterator.getDocID();
					nomiValue = nominatorIterator.getFeatures().get(0).toNumber();
				}
				else {
					nomiValue = null; 
				}
			}
			return true;
		}
		else
			return false;
	}

	public boolean skipTo(int docID) throws IOException {
		if (denominatorIterator.skipTo(docID) ){
			nomiDocId = nominatorIterator.getDocID();
			nomiValue = nominatorIterator.getFeatures().get(0).toNumber();
			denomValue = denominatorIterator.getFeatures().get(0).toNumber();
			denomDocId = denominatorIterator.getDocID();
			if (nomiValue == null || nomiDocId < denomDocId){
				if (nominatorIterator.skipTo(denomDocId)){
					nomiDocId = nominatorIterator.getDocID();
					nomiValue = nominatorIterator.getFeatures().get(0).toNumber();
				}
				else {
					nomiValue = null; 
				}
			}
			return true;
		}
		else
			return false;
	}

	public List<FeatureValue> getDefaultValues() {
		// TODO Auto-generated method stub
		List<FeatureValue> values = new ArrayList<FeatureValue>();
		values.add(new DoubleFeatureValue(0));
		return values;
	}
}
