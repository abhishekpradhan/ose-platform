package ose.index;

public class OSToken {
	
	
	public static final int TOK_PLAIN_BODY = 101;
	public static final int TOK_TITLE = 100;
	public static final int TOK_NUMBER = 1;
	public static final int TOK_ALPHANUM = 2;
	public static final int TOK_SYMBOL = 3;
	public static final int TOK_REST = 5;
	public static final int TOK_SPACE = 10;
	
	private int label;
	private String text;
	private int start;
	private int end;
	
	public OSToken(int label, String text, int start, int end){
		this.label = label;
		this.text = text;
		this.start = start;
		this.end = end;
	}
	
	public int getEnd() {
		return end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getLabel() {
		return label;
	}
	
	public String getString() {
		return text;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return label + ","+text+","+ start + ","+ end;
		String labelFunc = "";
		switch (label){
			case TOK_ALPHANUM:  labelFunc = "alphanum"; break;
			case TOK_NUMBER:    labelFunc = "number"; break;
			case TOK_SPACE:		labelFunc = "space"; break;
			case TOK_SYMBOL:    labelFunc = "symbol"; break;
			case TOK_REST:		labelFunc = "rest"; break;
			default:			labelFunc = "un???"; break;
		}
		return "OSToken." + labelFunc + "(\""+text+"\","+ start + ","+ end + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OSToken) {
			OSToken other = (OSToken) obj;
			return other.label == label && other.start == start && other.end == end && other.text.equals(text);
		}
		else
			return false;
	}
	
	public static OSToken space(String text, int start, int end){
		return new OSToken(TOK_SPACE, text, start, end);
	}
	
	public static OSToken alphanum(String text, int start, int end){
		return new OSToken(TOK_ALPHANUM, text, start, end);
	}
	public static OSToken symbol(String text, int start, int end){
		return new OSToken(TOK_SYMBOL, text, start, end);
	}
	
	public static OSToken number(String text, int start, int end){
		return new OSToken(TOK_NUMBER, text, start, end);
	}

	public static OSToken rest(String text, int start, int end){
		return new OSToken(TOK_REST, text, start, end);
	}
}
