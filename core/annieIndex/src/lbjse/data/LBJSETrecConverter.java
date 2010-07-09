package lbjse.data;

import java.io.IOException;
import java.io.PrintWriter;

import ose.html.HtmlParser;
import ose.html.ParsedHtmlDocument;
import ose.index.TrecDocument;
import ose.index.TrecFileReader;
import common.CommandLineOption;

/* this convert a normal trec file (html content) to 
 * LBJTrecFile (txt content)
 */
public class LBJSETrecConverter {

	public LBJSETrecConverter() {
		// TODO Auto-generated constructor stub
	}
	
	public void convert(String inputTrec, String outputTrec) throws IOException{
		PrintWriter output = new PrintWriter(outputTrec, "utf-8");
		HtmlParser parser = new HtmlParser();
		TrecFileReader reader = new TrecFileReader(inputTrec);
		TrecDocument trec = reader.next();
		while (trec != null){
			System.out.println("Converting " + trec.getUrl());
			String content = trec.getContent();
			ParsedHtmlDocument parsed = parser.parse(content);
			DocumentFromTrec doc = new DocumentFromTrec();
			doc.setDocId(-1);
			doc.setUrl(trec.getUrl());
			doc.setTitle(parsed.getTitle());
			doc.setText(parsed.getPlainBody());
			doc.setTags(trec.getTags());
			doc.serialize(output);
			trec = reader.next();
		}
		output.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"input","output"});
		String inputTrec = options.getString("input");
		String outputTrec = options.getString("output");
		LBJSETrecConverter converter = new LBJSETrecConverter();
		converter.convert(inputTrec, outputTrec);
		System.out.println("Done");
	}

}
