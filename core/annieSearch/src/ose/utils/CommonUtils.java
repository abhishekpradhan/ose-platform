/**
 * 
 */
package ose.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Pham Kim Cuong
 *
 */
public class CommonUtils {
	static public String tabString(int level){
		StringBuffer writer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			writer.append('\t');	
		}
		return writer.toString();
	}

	static public String escapeFileName(String fileName){
		return fileName.replaceAll(" ", "\\ ");
	}

	static public String unquote(String quote){
		return quote.substring(1,quote.length()-1);
	}
	
	static public String [] convertToStringArray(String text){
		ArrayList<String> arr = new ArrayList<String>();
		if (text == null) 
			return arr.toArray(new String[]{});
		StringTokenizer tokenizer = new StringTokenizer(text);
		while (tokenizer.hasMoreTokens()){
			arr.add(tokenizer.nextToken());
		}
		return arr.toArray(new String[]{});
	}
	
	static public List<Integer> convertStringToList(String str){
		List<Integer> result = new ArrayList<Integer>();
		if (str == null){
			return result;
		}
		StringTokenizer tokenizer = new StringTokenizer(str,"_");
		while (tokenizer.hasMoreTokens()){
			result.add(Integer.parseInt(tokenizer.nextToken()));
		}
		return result;
	}
	
	static public List<String> convertCVSStringToListOfString(String str){
		List<String> result = new ArrayList<String>();
		if (str == null){
			return result;
		}
		StringTokenizer tokenizer = new StringTokenizer(str,",");
		while (tokenizer.hasMoreTokens()){
			result.add(tokenizer.nextToken());
		}
		return result;
	}
}
