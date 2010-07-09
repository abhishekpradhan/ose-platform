package ose.index;

public interface Annotation extends Comparable<Annotation>{
	public String getSpan();
	
	public int getStart();
	
	public int getEnd();
	
	public int compareTo(Annotation other);
}
