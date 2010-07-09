package sjm.parse.tokens;

import java.io.*;

/*
 * Copyright (c) 1999 Steven J. Metsker. All Rights Reserved.
 * 
 * Steve Metsker makes no representations or warranties about
 * the fitness of this software for any particular purpose, 
 * including the implied warranty of merchantability.
 */

/**
 * A tokenizer divides a string into tokens. This class is highly customizable
 * with regard to exactly how this division occurs, but it also has defaults
 * that are suitable for many languages. This class assumes that the character
 * values read from the string lie in the range 0-255. For example, the Unicode
 * value of a capital A is 65, so <code> System.out.println((char)65); </code>
 * prints out a capital A.
 * 
 * <p>
 * The behavior of a tokenizer depends on its character state table. This table
 * is an array of 256 <code>TokenizerState
 * </code> states. The state table
 * decides which state to enter upon reading a character from the input string.
 * 
 * <p>
 * For example, by default, upon reading an 'A', a tokenizer will enter a "word"
 * state. This means the tokenizer will ask a <code>WordState</code> object to
 * consume the 'A', along with the characters after the 'A' that form a word.
 * The state's responsibility is to consume characters and return a complete
 * token.
 * 
 * <p>
 * The default table sets a SymbolState for every character from 0 to 255, and
 * then overrides this with:
 * 
 * <blockquote>
 * 
 * <pre>
 *      From    To     State
 *        0     ' '    whitespaceState
 *       'a'    'z'    wordState
 *       'A'    'Z'    wordState
 *      160     255    wordState
 *       '0'    '9'    numberState
 *       '-'    '-'    numberState
 *       '.'    '.'    numberState
 *       '&quot;'    '&quot;'    quoteState
 *      '\''   '\''    quoteState
 *       '/'    '/'    slashState
 * </pre>
 * 
 * </blockquote>
 * 
 * In addition to allowing modification of the state table, this class makes
 * each of the states above available. Some of these states are customizable.
 * For example, wordState allows customization of what characters can be part of
 * a word, after the first character.
 * 
 * @author Steven J. Metsker
 * 
 * @version 1.0
 */
public class Tokenizer {
	/*
	 * The reader to read characters from
	 */
	protected PushbackReader reader;

	/*
	 * The number of characters that might be in a symbol;
	 */
	protected static final int DEFAULT_SYMBOL_MAX = 4;

	/*
	 * The state lookup table
	 */
	protected TokenizerState[] characterState = new TokenizerState[9000]; // Khanh: change size of array

	/*
	 * The default states that actually consume text and produce a token
	 */
	protected NumberState numberState = new NumberState();

	protected QuoteState quoteState = new QuoteState();

	protected SlashState slashState = new SlashState();

	protected SymbolState symbolState = new SymbolState();

	protected WhitespaceState whitespaceState = new WhitespaceState();

	protected WordState wordState = new WordState();

	/**
	 * Constructs a tokenizer with a default state table (as described in the
	 * class comment).
	 * 
	 * @return a tokenizer
	 */
	/*
	 * Khanh: change code for using unicode
	 */
	private static final int[] vnVal= {
		'\u0110','\u00D4','\u00EA','\u00F4','\u00F5','\u00E3','\u00C2',
		'\u00E2','\u0103','\u00C1','\u00E1','\u0102','\u0103','\u1EA4','\u1EA5','\u1EA6','\u1EA7','\u1EAE','\u1EAF','\u1EB0',
		'\u1EB1','\u1EAA','\u1EAB','\u1EB4','\u1EB5','\u1EA2','\u1EA3','\u1EA8','\u1EA9','\u1EB2','\u1EB3','\u1EA0','\u1EA1','\u1EAC',
		'\u1EAD','\u1EB6','\u1EB7','\u00EA','\u00C0','\u00E0','\u0111','\u00D0','\u00C9','\u00E9','\u1EBC','\u1EBD','\u1EBE','\u1EBF',
		'\u1EC0','\u1EC1','\u1EC4','\u1EC5','\u1EBA','\u1EBB','\u1EC2','\u1EC3','\u1EB8','\u1EB9','\u1EC6','\u1EC7','\u00C8','\u00E8','\u00CD',
		'\u00ED','\u0128','\u0129','\u1EC8','\u1EC9','\u1ECA','\u1ECB','\u00CC','\u00EC','\u00D3','\u00F3','\u1ED0','\u1ED1','\u1ED2','\u1ED3',
		'\u1ED6','\u1ED7','\u1ECE','\u1ECF','\u01A0','\u01A1','\u1ED4','\u1ED5','\u1ECC','\u1ECD','\u1EDA','\u1EDB','\u1EDC','\u1EDD','\u1EE0',
		'\u1EE1','\u1ED8','\u1ED9','\u1EDE','\u1EDF','\u1EE2','\u1EE3','\u00D2','\u00F2','\u00DA','\u00FA','\u0168','\u0169','\u1EE6','\u1EE7',
		'\u01AF','\u01B0','\u1EE4','\u1EE5','\u1EE8','\u1EE9','\u1EEA','\u1EEB','\u1EEE','\u1EEF','\u1EEC','\u1EED','\u1EF0','\u1EF1','\u00D9',
		'\u00F9','\u1EF9','\u00DD','\u00FD','\u1EF2','\u1EF3','\u1EF8','\u1EF6','\u1EF7','\u1EF4','\u1EF5' };
	
	public Tokenizer() {

		setCharacterState(0, 255, symbolState()); // the default

		setCharacterState(0, ' ', whitespaceState());
		setCharacterState('a', 'z', wordState());
		setCharacterState('A', 'Z', wordState());
		setCharacterState(0xc0, 0xff, wordState());
		setCharacterState('0', '9', numberState());
		setCharacterState('-', '-', numberState());
		setCharacterState('.', '.', numberState());
		setCharacterState('"', '"', quoteState());
		setCharacterState('\'', '\'', quoteState());
		setCharacterState('/', '/', slashState());
				
		setVNCharacterState(wordState());		
	}

	/**
	 * Constructs a tokenizer to read from the supplied string.
	 * 
	 * @param String
	 *            the string to read from
	 */
	public Tokenizer(String s) {
		this();
		setString(s);
	}

	/**
	 * Return the reader this tokenizer will read from.
	 * 
	 * @return the reader this tokenizer will read from
	 */
	public PushbackReader getReader() {
		return reader;
	}

	/**
	 * Return the next token.
	 * 
	 * @return the next token.
	 * 
	 * @exception IOException
	 *                if there is any problem reading
	 */
	public Token nextToken() throws IOException {
		int c = reader.read();

		/*
		 * There was a defect here, that resulted from the fact that unreading a
		 * -1 results in the next read having a value of (int)(char)-1, which is
		 * 65535. This may be a defect in PushbackReader.
		 */

		if (c >= 0 && c < characterState.length) {
			return characterState[c].nextToken(reader, c, this);
		}
		return Token.EOF;
	}

	/**
	 * Return the state this tokenizer uses to build numbers.
	 * 
	 * @return the state this tokenizer uses to build numbers
	 */
	public NumberState numberState() {
		return numberState;
	}

	/**
	 * Return the state this tokenizer uses to build quoted strings.
	 * 
	 * @return the state this tokenizer uses to build quoted strings
	 */
	public QuoteState quoteState() {
		return quoteState;
	}

	/**
	 * Change the state the tokenizer will enter upon reading any character
	 * between "from" and "to".
	 * 
	 * @param from
	 *            the "from" character
	 * 
	 * @param to
	 *            the "to" character
	 * 
	 * @param TokenizerState
	 *            the state to enter upon reading a character between "from" and
	 *            "to"
	 */
	public void setCharacterState(int from, int to, TokenizerState state) {

		for (int i = from; i <= to; i++) {
			if (i >= 0 && i < characterState.length) {
				characterState[i] = state;
			}
		}
	}
	/*
	 * Khanh: Add Vietnamese character
	 */
	public void setVNCharacterState(TokenizerState state) {
		for (int i = 0; i < vnVal.length; i++)
			characterState[vnVal[i]] = state;
	}

	/**
	 * Set the reader to read from.
	 * 
	 * @param PushbackReader
	 *            the reader to read from
	 */
	public void setReader(PushbackReader r) {
		this.reader = r;
	}

	/**
	 * Set the string to read from.
	 * 
	 * @param String
	 *            the string to read from
	 */
	public void setString(String s) {
		setString(s, DEFAULT_SYMBOL_MAX);
	}

	/**
	 * Set the string to read from.
	 * 
	 * @param String
	 *            the string to read from
	 * 
	 * @param int
	 *            the maximum length of a symbol, which establishes the size of
	 *            pushback buffer we need
	 */
	public void setString(String s, int symbolMax) {
		setReader(new PushbackReader(new StringReader(s), symbolMax));
	}

	/**
	 * Return the state this tokenizer uses to recognize (and ignore) comments.
	 * 
	 * @return the state this tokenizer uses to recognize (and ignore) comments
	 * 
	 */
	public SlashState slashState() {
		return slashState;
	}

	/**
	 * Return the state this tokenizer uses to recognize symbols.
	 * 
	 * @return the state this tokenizer uses to recognize symbols
	 */
	public SymbolState symbolState() {
		return symbolState;
	}

	/**
	 * Return the state this tokenizer uses to recognize (and ignore)
	 * whitespace.
	 * 
	 * @return the state this tokenizer uses to recognize whitespace
	 */
	public WhitespaceState whitespaceState() {
		return whitespaceState;
	}

	/**
	 * Return the state this tokenizer uses to build words.
	 * 
	 * @return the state this tokenizer uses to build words
	 */
	public WordState wordState() {
		return wordState;
	}

	public String restOfTheStream() throws IOException {
		char[] theRest = new char[100];
		int t = reader.read(theRest);
		if (t > 0)
			return new String(theRest, 0, t);
		else
			return "EOF";
	}
	
	/* Kim : to read the input stream until a character untilChar is read */
	public String readUntil(char untilChar) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int ch = reader.read();
		while (ch != -1 && (char)ch != untilChar){
			buffer.append((char)ch);
			ch = reader.read();
		}
		return buffer.toString();
	}
	
	/* Kim : to read the input stream whatever inside a bracket */
	public String readInsideBracket() throws IOException {
		StringBuffer buffer = new StringBuffer();
		int topLevel = 1;
		int ch;
		do {
			ch = reader.read();
			if (ch == '(') topLevel += 1;
			else if (ch == ')') topLevel -= 1;
			if (ch == -1 || topLevel == 0) break;
			buffer.append((char)ch);			
		} while (ch != -1 || topLevel == 0);
		if (ch == -1) //unclosed bracket. 
			return null;
		else
			return buffer.toString();
	}
	
}