package lbjse.objectsearch;

import LBJ2.parse.LinkedChild;

public class PositionWordPair extends LinkedChild {
	private static final long serialVersionUID = 231L;
	
	private int position;
	private String word;
	public PositionWordPair(int pos, String s) {
		position = pos;
		word = s;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getWord() {
		return word;
	}
	
	@Override
	public String toString() {
		return "(" + position + "," + word + ")";
	}
}
