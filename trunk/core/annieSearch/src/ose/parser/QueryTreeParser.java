package ose.parser;

import java.io.IOException;

import ose.processor.cascader.BooleanQuery;
import ose.utils.CommonUtils;
import sjm.parse.tokens.Token;

public class QueryTreeParser extends BaseParser{

	public QueryTreeParser(String inputString) {
		super();
		tokenizer.setString(inputString);
	}
	
	public ParentNode parseQueryNode(){
		ParentNode queryNode = new ParentNode(null);
		
		ParsingNode feature = parseNextNode();
		while (feature != null){
			queryNode.addChildNode(feature);
			feature = parseNextNode();
		}
				
		return queryNode;
	}
	
	private ParsingNode parseNextNode(){
		try {			
			Token tok = getNextToken();
			if (tok.equals(Token.EOF)) return null;
			if (tok.isWord()){
				String nodeName = tok.sval();
				if (isFeatureName(nodeName) ){
					assertToken(getNextToken(), new Token(Token.TT_SYMBOL,"(",0), "Bad query, expected a '(' " );			
					ParentNode pNode = parseQueryNode();
					assertToken(getNextToken(), new Token(Token.TT_SYMBOL,")",0), "Bad query, expected a ')' " );
					pNode.setNodeName(nodeName);
					return pNode;
				}				
				else{
					return new LiteralNode(nodeName);
				}
			}		
			else if ("_".equals(tok.sval())){
				Token functionName = getNextToken();
				assertToken(getNextToken(), new Token(Token.TT_SYMBOL,"(",0), "Bad query, expected a '(' " );			
				ParentNode pNode = parseQueryNode();
				assertToken(getNextToken(), new Token(Token.TT_SYMBOL,")",0), "Bad query, expected a ')' " );
				pNode.setNodeName(tok.sval() + functionName.sval());
				return pNode;
			}
			else if ("%".equals(tok.sval())){
				Token featureGeneratorName = getNextToken();
				assertToken(getNextToken(), new Token(Token.TT_SYMBOL,"(",0), "Bad query, expected a '(' " );			
				ParentNode pNode = parseQueryNode();
				assertToken(getNextToken(), new Token(Token.TT_SYMBOL,")",0), "Bad query, expected a ')' " );
				pNode.setNodeName(tok.sval() + featureGeneratorName.sval());
				return pNode;
			}
			else if (",".equals(tok.sval())){
				return parseNextNode();
			}
			else if (tok.isNumber()){
				return new LiteralNode(tok.toString());
			}
			else if ( tok.isQuotedString() ){
				return new LiteralNode(CommonUtils.unquote( tok.toString() ));
			}
			else{
				pushBackToken(tok);
				return null;
			}
				
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static private boolean isFeatureName(String str){
		return Character.isUpperCase( str.charAt(0) ) && ! isUpperCases(str.substring(1));
	}
	
	static private boolean isMacroName(String str){
		return isUpperCases(str);
	}

	/**
	 * @param str
	 * @return
	 */
	private static boolean isUpperCases(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (! Character.isUpperCase(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	static private boolean isLowerCases(String str){
		for (int i = 0; i < str.length(); i++) {
			if (! Character.isLowerCase(str.charAt(i)))
				return false;
		}
		return true;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		String query = "Token(canon) Token(camera) Phrase( Number(_range(100,200)) Token(megapixel) )";
//		String query = "Token(canon) Token(camera) Phrase( Number(_range(100,200)) Token(megapixel) ) Ranking( 1.0 Number(_range(100,200)) 2.0 Token(haha) )";
		String query = "Token(canon,camera) Number(_range(100,200) ) Phrase( Number(_range(100,200)) Token(megapixel) )";
		QueryTreeParser parser = new QueryTreeParser(query);
		ParentNode tree = parser.parseQueryNode();
		System.out.println(tree);
		OSQueryParser queryParser = new OSQueryParser();
		BooleanQuery booleanQuery = queryParser.parseBooleanQuery(tree);
		System.out.println("Boolean query : " + booleanQuery);
	}

}
