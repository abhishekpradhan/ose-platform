/**
 * 
 */
package ose.processor.cascader;


/**
 * @author Pham Kim Cuong
 *
 */
public interface Constraint {
	public boolean satisfy(Object fieldValue);
	public String toString();
	public boolean equals(Object obj);
}
