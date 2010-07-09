package ose.index;

public class AnnotationBase implements Annotation {
	
	private String span;
	private int start;
	private int end;
	private String indexField;
	public AnnotationBase(String span, int start, int end, String indexField) {
		this.span = span;
		this.start = start;
		this.end = end;
		this.indexField = indexField;
	}
	
	public String getSpan() {
		return span;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}

	public String getIndexField() {
		return indexField;
	}
	
	public int compareTo(Annotation other) {
		if (start < other.getStart())
			return -1;
		else if (start > other.getStart())
			return 1;
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return "(" + start + "," + end + ")" + span + "[" + indexField + "]";
	}
}
