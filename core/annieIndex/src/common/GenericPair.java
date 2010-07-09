package common;

public class GenericPair<FirstClass,SecondClass> {
	private FirstClass first;
	private SecondClass second;
	
	public GenericPair(FirstClass f, SecondClass s){
		this.first = f;
		this.second = s;
	}
	
	public void setFirst(FirstClass first) {
		this.first = first;
	}
	
	public void setSecond(SecondClass second) {
		this.second = second;
	}
	
	public FirstClass getFirst() {
		return first;
	}
	
	public SecondClass getSecond() {
		return second;
	}
	
}
