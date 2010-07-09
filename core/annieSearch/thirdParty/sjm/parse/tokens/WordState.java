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
 * A wordState returns a word from a reader. Like other 
 * states, a tokenizer transfers the job of reading to this 
 * state, depending on an initial character. Thus, the 
 * tokenizer decides which characters may begin a word, and 
 * this state determines which characters may appear as a 
 * second or later character in a word. These are typically 
 * different sets of characters; in particular, it is typical 
 * for digits to appear as parts of a word, but not as the 
 * initial character of a word. 
 *
 * <p>
 * By default, the following characters may appear in a word.
 * The method <code>setWordChars()</code> allows customizing
 * this.
 * 
 * <blockquote><pre>
 *     From    To
 *      'a', 'z'
 *      'A', 'Z'
 *      '0', '9'
 *
 *     as well as: minus sign, underscore, and apostrophe.
 * 
 * </pre></blockquote>
 *
 * @author Steven J. Metsker
 *
 * @version 1.0 
 */
public class WordState extends TokenizerState {
	protected char charbuf[] = new char[16];
	protected boolean wordChar[] = new boolean[9000]; // Khanh: Change size of array
/**
 * Constructs a word state with a default idea of what 
 * characters are admissible inside a word (as described in 
 * the class comment). 
 *
 * @return   a state for recognizing a word
 */
	/*
	 * Khanh: Add Vietnamese 
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
		
public WordState() {
	setWordChars('a', 'z', true);
	setWordChars('A', 'Z', true);
	setWordChars('0', '9', true);
	setWordChars('-', '-', true);
	setWordChars('_', '_', true);
	setWordChars('\'', '\'', true);
	setWordChars(0xc0, 0xff, true);
	setVNWordChars(true);
}
/*
 * Fatten up charbuf as necessary.
 */
protected void checkBufLength(int i) {
	if (i >= charbuf.length) {
		char nb[] = new char[charbuf.length * 2];
		System.arraycopy(charbuf, 0, nb, 0, charbuf.length);
		charbuf = nb;
	}
}
/**
 * Return a word token from a reader.
 *
 * @return a word token from a reader
 */
public Token nextToken(PushbackReader r, int c, Tokenizer t) 
	throws IOException {
		
	int i = 0;
	do {
		checkBufLength(i);
		charbuf[i++] = (char) c;
		c = r.read();
	} while (wordChar(c));
	
	if (c >= 0) {
		r.unread(c);
	}
	String sval = String.copyValueOf(charbuf, 0, i);
	return new Token(Token.TT_WORD, sval, 0);
}
/**
 * Establish characters in the given range as valid 
 * characters for part of a word after the first character. 
 * Note that the tokenizer must determine which characters
 * are valid as the beginning character of a word.
 *
 * @param   from   char
 *
 * @param   to   char
 *
 * @param   boolean   true, if this state should allow
 *                    characters in the given range as part
 *                    of a word
 */
public void setWordChars(int from, int to, boolean b) {
	for (int i = from; i <= to; i++) {
		if (i >= 0 && i < wordChar.length) {
			wordChar[i] = b;
		}
	}
}
/*
 * Khanh: Add Vietnamese character
 */
public void setVNWordChars(boolean b) {
	for (int i = 0; i < vnVal.length; i++)
		wordChar[vnVal[i]] = b;
}

/*
 * Just a test of the wordChar array.
 */
protected boolean wordChar(int c) {
	if (c >= 0 && c < wordChar.length) {
		return wordChar[c];
	}
	return false;
}
}