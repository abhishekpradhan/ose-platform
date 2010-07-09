package ose.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ose.utils.CommonUtils;

public class LiteralNode extends ParsingNode {
	public static final String NODE_LITERAL = "Literal";
	private String value;
	Set<String> specialStrings = new HashSet<String>(Arrays.asList(new String[] {"$","\"","."} ));
	
	public LiteralNode(String value) {
		this.nodeName = NODE_LITERAL;
		this.value = value; 
	}

	public String getValue() {
		return value;
	}

	public Double getNumber() throws NumberFormatException{
		return Double.parseDouble(value);
	}
	
	@Override
	public String toString(int level) {
		return CommonUtils.tabString(level) + "[Literal]" + value;
	}
	
	public String reconstructString(){
		if (specialStrings.contains(value) 
				|| value.indexOf(" ") != -1 )
			return "'" + escapeQuotes(value) + "'";
		else
			return escapeQuotes(value);
	}
	
	public String escapeQuotes(String s) {
		return s.replace("\"", "\\\"");
	}
}
