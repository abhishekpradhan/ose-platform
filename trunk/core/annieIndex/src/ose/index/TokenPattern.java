package ose.index;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenPattern {
	
	private Pattern pattern;
	private int label;
	
	public TokenPattern(String regex, int label){
		this.pattern = Pattern.compile("^" + regex, Pattern.CANON_EQ);
		this.label = label;
	}
	
	public int match(CharSequence s){
		Matcher matcher = pattern.matcher(s);
		if (matcher.find())
			return matcher.end();
		else
			return -1;
	}
	
	public int getLabel() {
		return label;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public static void main(String[] args) {
		TokenPattern p = new TokenPattern("\\d+",1);
		System.out.println(p.match(" 1232abcd"));
	}
}	
