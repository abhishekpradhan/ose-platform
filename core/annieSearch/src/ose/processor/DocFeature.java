package ose.processor;

import java.util.Map;

import ose.utils.JsonIO;

public class DocFeature extends Span{
	
	public DocFeature(int docId) {
		super(docId, 0, Integer.MAX_VALUE);
	}
	
	public DocFeature(int docId, Map<String, Object> attributes) {
		super(docId, 0,Integer.MAX_VALUE, attributes);
	}
	
	public DocFeature clone(){
		return new DocFeature(docId, attributes);
	}
	
	public void setFeatureValue(Object feature) {
		setAttribute("fvalue", feature);
	}

	public void setFeatureSpan(Object span) {
		setAttribute("span", span);
	}

	@Override
	public String toString() {
		if (attributes == null)
			return "Feature(" + docId + ")" ;
		else
			return "Doc " + docId + JsonIO.toJSONString(attributes);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
