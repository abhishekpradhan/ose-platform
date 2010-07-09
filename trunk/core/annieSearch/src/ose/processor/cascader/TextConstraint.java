/**
 * 
 */
package ose.processor.cascader;




/**
 * 
 * This text constraint is part of a structured query predicate.
 * This is NOT used in general query. 
 * @author Pham Kim Cuong
 *
 */
public class TextConstraint implements Constraint {

	private String text;
	
	public TextConstraint(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return "\"" + text + "\"";
	}
	
	/* (non-Javadoc)
	 * @see ose.index.Constraint#satisfy(java.lang.Object)
	 */
	public boolean satisfy(Object fieldValue) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean equals(Object obj){
		return false;
	}
}
