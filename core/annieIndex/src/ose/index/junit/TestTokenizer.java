/**
 * 
 */
package ose.index.junit;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ose.index.OSToken;
import ose.index.OSTokenizer;


/**
 * @author Pham Kim Cuong
 *
 */
public class TestTokenizer extends TestCase {
	
	static private final String[] testTexts = new String[]{
		"abc def \n" +
			"hello.world!\n" +
			"how-are-you?"
			,
		"diện tích 83m2 123.456,12",
	};
	
	static private OSToken[][] answers = new OSToken[][]{
		new OSToken[] {
			OSToken.alphanum("abc",0,3),
			OSToken.space(" ",3,4),
			OSToken.alphanum("def",4,7),
			OSToken.space(" ",7,8),
			OSToken.space("\n",8,9),
			OSToken.alphanum("hello",9,14),
			OSToken.symbol(".",14,15),
			OSToken.alphanum("world",15,20),
			OSToken.space("\n",21,22),
			OSToken.alphanum("how",22,25),
			OSToken.alphanum("are",26,29),
			OSToken.alphanum("you",30,33),
			OSToken.symbol("?",33,34),
		},
		new OSToken[]{
			OSToken.alphanum("diện",0,4),
			OSToken.space(" ",4,5),
			OSToken.alphanum("tích",5,9),
			OSToken.space(" ",9,10),
			OSToken.number("83",10,12),
			OSToken.alphanum("m2",12,14),
			OSToken.space(" ",14,15),
			OSToken.number("123.456,12",15,25),
		}
	};
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 */
	@Test
	public void testNextToken() throws IOException {
		
		for (int testNo = 0; testNo < testTexts.length ; testNo ++) {
			String text = testTexts[testNo];
			OSTokenizer tokenizer = new OSTokenizer(text);
			OSToken tok = null;
			int i = 0;
			while ( (tok = tokenizer.nextToken()) != null){
				if (i >= answers[testNo].length){
					assertTrue("Extra token " + tok, false);
				}
				assertEquals("Wrong token extracted ! ", tok, answers[testNo][i]);
				i += 1;
			}
			assertEquals("Too few tokens ", i, answers[testNo].length);
			testNo += 1;
		}
	}
	
	public static void main(String[] args) throws Exception{
		OSTokenizer tokenizer = new OSTokenizer("diện tích 83m2 123.456,12");
		OSToken tok = null;
		while ( (tok = tokenizer.nextToken()) != null){
			System.out.write(tok.toString().getBytes("utf8"));
			System.out.println();
		}
	}
	
}