package ose.index;

public class OSTokenizer {
	

	private StringBuffer buffer;
	private int start;
	
	private static final String vnchar=
		"\u0110\u00D4\u00EA\u00F4\u00F5\u00E3\u00C2" +
		"\u00E2\u0103\u00C1\u00E1\u0102\u0103\u1EA4\u1EA5\u1EA6\u1EA7\u1EAE\u1EAF\u1EB0" +
		"\u1EB1\u1EAA\u1EAB\u1EB4\u1EB5\u1EA2\u1EA3\u1EA8\u1EA9\u1EB2\u1EB3\u1EA0\u1EA1\u1EAC" +
		"\u1EAD\u1EB6\u1EB7\u00EA\u00C0\u00E0\u0111\u00D0\u00C9\u00E9\u1EBC\u1EBD\u1EBE\u1EBF" +
		"\u1EC0\u1EC1\u1EC4\u1EC5\u1EBA\u1EBB\u1EC2\u1EC3\u1EB8\u1EB9\u1EC6\u1EC7\u00C8\u00E8\u00CD" +
		"\u00ED\u0128\u0129\u1EC8\u1EC9\u1ECA\u1ECB\u00CC\u00EC\u00D3\u00F3\u1ED0\u1ED1\u1ED2\u1ED3" +
		"\u1ED6\u1ED7\u1ECE\u1ECF\u01A0\u01A1\u1ED4\u1ED5\u1ECC\u1ECD\u1EDA\u1EDB\u1EDC\u1EDD\u1EE0" +
		"\u1EE1\u1ED8\u1ED9\u1EDE\u1EDF\u1EE2\u1EE3\u00D2\u00F2\u00DA\u00FA\u0168\u0169\u1EE6\u1EE7" +
		"\u01AF\u01B0\u1EE4\u1EE5\u1EE8\u1EE9\u1EEA\u1EEB\u1EEE\u1EEF\u1EEC\u1EED\u1EF0\u1EF1\u00D9" +
		"\u00F9\u1EF9\u00DD\u00FD\u1EF2\u1EF3\u1EF8\u1EF6\u1EF7\u1EF4\u1EF5";		

	private TokenPattern [] tokenizers = new TokenPattern[]{
			new TokenPattern("^\\d+(\\.\\d+)*(,\\d+)?",OSToken.TOK_NUMBER),
			new TokenPattern("^\\d+(,\\d+)*(\\.\\d+)?",OSToken.TOK_NUMBER),
			new TokenPattern("^[\u00D0"+vnchar+"a-zA-Z_]+[a-zA-Z0-9"+vnchar+"_]*",OSToken.TOK_ALPHANUM),
			new TokenPattern("^[\\$\\.\\?,\\\"',-,:/]",OSToken.TOK_SYMBOL), 
			new TokenPattern("^\\s",OSToken.TOK_SPACE),
			new TokenPattern("^[^\\w"+vnchar+"]",OSToken.TOK_REST),//[^"+vnchar+"]
		};	
	
	public OSTokenizer(String text){
		buffer = new StringBuffer(text);
		start = 0;
	}
	
	public OSToken nextToken(){
		OSToken tok = null;
		while (buffer.length() > 0 && (tok == null || tok.getLabel() == OSToken.TOK_REST) ) {
			tok = null;
			//Khanh: get different number
			int pos1 = tokenizers[0].match(buffer);
			int pos2 = tokenizers[1].match(buffer);		
			if (pos1 != -1 && pos2 != -1) {
				int pos = pos1>pos2 ? pos1:pos2;
				int i = pos1>pos2 ? 0:1;
				tok = new OSToken(tokenizers[i].getLabel(), buffer.substring(0, pos), start, start + pos  );
				buffer.delete(0, pos);
				start += pos;
				if (tok.getLabel() != OSToken.TOK_REST)
					return tok;									
			}
			// end
			for (int i = 2; i < tokenizers.length; i++) {
				int pos = tokenizers[i].match(buffer);
				if (pos != -1) {
					tok = new OSToken(tokenizers[i].getLabel(), buffer.substring(0, pos), start, start + pos  );
					buffer.delete(0, pos);
					start += pos;
					if (tok.getLabel() != OSToken.TOK_REST)
						return tok;					
				}				
			}			
			if (tok == null){
				System.err.println("Can not match token (even TOK_REST) with : " + buffer );
				System.err.println("delete character " + buffer.charAt(0) + "(" + (int)buffer.charAt(0));
				buffer.delete(0, 1);
				start += 1;
			}
		}		
		return null;
	}
	
	public static void main(String[] args) {
//		String input = "diện tích 83m2 123.456,12";
		String input = "CDW Product Overview: HP EliteBook Mobile Workstation 8730w - Core 2 Duo T9400 2.53 GHz - 17\" TFT";		
		OSTokenizer tokenizer = new OSTokenizer(input);
		
		OSToken token;
		while ((token = tokenizer.nextToken()) != null) {
			System.out.println(token.getLabel() + " " + token.getString() + "\t" + Number.toNumber(token.getString()));
		}
	}	
}
