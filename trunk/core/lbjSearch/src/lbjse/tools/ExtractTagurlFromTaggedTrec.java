package lbjse.tools;

import java.io.IOException;
import java.io.PrintWriter;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.LBJTrecFileParser;

import org.json.JSONException;
import org.json.JSONObject;

import common.CommandLineOption;

public class ExtractTagurlFromTaggedTrec {

	public void convert(String taggedTrecFile, String outputFile) {
		int count = 0;
		try {
			LBJTrecFileParser reader = new LBJTrecFileParser(taggedTrecFile);
			PrintWriter writer = new PrintWriter(outputFile);
			
			while (true){
				DocumentFromTrec doc = (DocumentFromTrec) reader.next();
				if (doc == null)
					break;
				count += 1;
				JSONObject jsonTags = new JSONObject();
				for (String valuePair : doc.getTags()){
					String key = valuePair.substring(0,valuePair.indexOf(":"));
					String value = valuePair.substring(valuePair.indexOf(":") + 1);
					if (jsonTags.has(key)){
						jsonTags.put(key, jsonTags.get(key) + " " + value);
					}
					else
						jsonTags.put(key, value);
				}
				writer.println(jsonTags.toString());
				writer.println(doc.getUrl());
			}
			writer.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done converting " + count + " docs");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineOption options = new CommandLineOption(args);
		
		String taggedTrecFile = options.getString("tagtrec");
		String outputFile = options.getString("tagurl");
		ExtractTagurlFromTaggedTrec converter = new ExtractTagurlFromTaggedTrec();
		converter.convert(taggedTrecFile, outputFile);
		
	}

}
