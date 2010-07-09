package ose.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.utils.JsonIO;

public class Span {
	public int docId;
	public int startPos, endPos;
	protected Map<String, Object> attributes;
	
	public Span(int docId, int startPos, int endPos) {
		this.docId = docId;
		this.startPos = startPos;
		this.endPos = endPos;
		attributes = null;
	}
	
	public Span(int docId, int startPos, int endPos, Map<String, Object> attributes) {
		this.docId = docId;
		this.startPos = startPos;
		this.endPos = endPos;
		if (attributes != null)
			this.attributes = new HashMap<String, Object>(attributes);
//			this.attributes = attributes; just try to see if it reduces overhead
		else
			this.attributes = attributes;
	}
	
	public Span clone(){
		return new Span(docId, startPos, endPos, attributes);
	}
	
	public Object getAttribute(String key) {
		if (attributes == null)
			return null;
		else 
			return attributes.get(key);
	}
	
	public void setAttribute(String key, Object value ) {
		if (attributes == null)
			attributes = new HashMap<String, Object>();
		attributes.put(key, value);
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		if (attributes == null)
			return "Doc " + docId + "[" + startPos + "," + endPos + "]";
		else
			return "Doc " + docId + "[" + startPos + "," + endPos + "]" + JsonIO.toJSONString(attributes);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Span) {
			Span other = (Span) obj;
			return other.docId == docId && other.startPos == startPos && other.endPos == endPos && equalMap(other.attributes, attributes);
		}
		else 
			return false;
	}
	
	private boolean equalMap(Map<String, Object> a, Map<String, Object> b ) {
		if (a == null && b != null)
			return false;
		if (a != null && b == null)
			return false;
		if (a == null && b == null)
			return true;
		if (a.size() != b.size())
			return false;
		for (String key : a.keySet()){
			if (! a.get(key).equals(b.get(key)))
				return false;
		}
		return true;
	}
}
