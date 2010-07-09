/**
 * 
 */
package ose.index;

import java.util.Comparator;

/**
 * @author Pham Kim Cuong
 *
 */
public class IdValuePairComparator implements Comparator<IdValuePairComparator> {
	static final long serialVersionUID = 987928713408102934L;

	private int listID;

	private int value;
	
	// Constructors
	public IdValuePairComparator() {
		listID = -1;
		value = -1;
	}
	
	public IdValuePairComparator(IdValuePairComparator p0) {
		listID = p0.listID;
		value = p0.value;
	}
	
	public IdValuePairComparator(int lid, int did) {
		listID = lid;
		value = did;
	}

	public int compare(IdValuePairComparator o1, IdValuePairComparator o2) {
		if (o1.value < o2.value)
			return -1;
		else if (o1.value > o2.value)
			return 1;
		else
			return o1.listID - o2.listID;
	}

	// Methods
	public int hashCode() {
		return value ^ listID;
	}

	public String toString() {
		return "<" + listID + ", " + value + ">";
	}

	public boolean equals(Object p0) {
		if (!p0.getClass().equals(this.getClass()))
			return false;
		return equals((IdValuePairComparator) p0);
	}//equals

	public boolean equals(IdValuePairComparator p0) {
		if (p0.listID == listID && p0.value == value)
			return true;
		return false;
	} //equals

	public synchronized Object clone() {
		return new IdValuePairComparator(this);
	}
	
	public int getListID() {
		return listID;
	}
	
	public int getValue() {
		return value;
	}
}
