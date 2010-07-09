/**
 * 
 */
package ose.parser;

import java.io.IOException;

import sjm.parse.tokens.Token;
import sjm.parse.tokens.TokenType;
import sjm.parse.tokens.Tokenizer;

/**
 * @author Pham Kim Cuong
 *
 */
public class BaseParser {
	protected Tokenizer tokenizer = null;
	
	public BaseParser() {
		tokenizer = new Tokenizer();
	}
	
	protected void assertToken(Token tok, TokenType shouldBeType) throws IOException {
		assertToken(tok, shouldBeType, "Token of type " + shouldBeType + " is expected.");
	}
	
	protected void assertToken(Token tok, TokenType shouldBeType, String message) throws IOException {
		if (tok == null || !tok.ttype().equals(shouldBeType)){
			throwException(message);
		}
	}
	
	protected void assertToken(Token tok, Token shouldBeToken) throws IOException {
		assertToken(tok, shouldBeToken, shouldBeToken + " is expected, " + tok + " found.");
	}
	
	protected void assertToken(Token tok, Token shouldBeToken, String message) throws IOException {
		if (tok == null || !tok.equals(shouldBeToken)){
			throwException(message);
		}
	}

	protected void throwException(String message) throws IOException {
		throw new RuntimeException(message + " near '" + tokenizer.restOfTheStream()+"'");
	}
	
	private Token tempToken = null;
	protected Token getNextToken() throws IOException{
		if (tempToken != null){
			Token returnToken = tempToken;
			tempToken = null;
			return returnToken;
		}
		else{		
			Token tok = tokenizer.nextToken();
//		System.out.println("Token : " + tok + "  " + tok.ttype() + "," + tok.value() + "," + tok.nval() + "," + tok.sval());
			return tok;
		}
	}
	
	protected Token seeNextToken() throws IOException{
		if (tempToken != null)			
			return tempToken;
		else{
			tempToken = getNextToken();
			return tempToken;
		}
	}
	
	protected void pushBackToken(Token tok) throws IOException{
		if (tempToken != null)
			throw new IOException("Can not push token back after reading more than one token");
		else
			tempToken = tok;
	}
}
