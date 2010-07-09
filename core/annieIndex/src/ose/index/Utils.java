package ose.index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.TermPositions;

public class Utils {

	static public byte [] doubleToByteArray(double t){
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		DataOutputStream data_out = new DataOutputStream(byte_out);
		try {
			data_out.writeDouble(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byte_out.toByteArray();
	}

	static public double byteArrayToDouble(byte [] b) throws IOException{
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
		return in.readDouble();
	}
	
	static public Double getNumberValue(String numberStr) {
		try {
			return Double.parseDouble(numberStr.replaceAll("[^0-9.]", "") );
		} catch (NumberFormatException e) {
			return null;
		}
	}

	static public double getPayloadNumber(TermPositions termPositions) throws IOException {
		byte[] data = new byte[1];
		data = termPositions.getPayload(data, 0);
		double value = byteArrayToDouble(data);
		return value;
	}

	public static List<String> getTokenizedString(String str){
		OSTokenizer tokenizer = new OSTokenizer(str);
		ArrayList<String> tokens = new ArrayList<String>();
		while (true){
			OSToken tok = tokenizer.nextToken();
			if (tok == null)
				break;
			if (tok.getLabel() != OSToken.TOK_SPACE)
				tokens.add(tok.getString());
		}
		return tokens;
	}

	public static Annotation makeAnnotation(int label, String span, int start, int end, String indexField){
		switch (label) {
			case OSToken.TOK_NUMBER:
				return new Number(span, start,end,indexField);				
			case OSToken.TOK_ALPHANUM:
			case OSToken.TOK_SYMBOL:
				return new AnnotationBase(span, start,end,indexField);
			case OSToken.TOK_TITLE:
				return new Title(span, start,end,indexField);
			case OSToken.TOK_PLAIN_BODY:
				return new PlainBody(span, start,end,indexField);			
		}
		return null;
	}
	
	public static PrintWriter getPrintWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException{
		return new PrintWriter(new File( filePath ),"utf-8");
	}
}
