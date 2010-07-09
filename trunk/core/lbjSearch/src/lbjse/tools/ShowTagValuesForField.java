package lbjse.tools;

import common.CommandLineOption;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.LBJTrecFileParser;

public class ShowTagValuesForField {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"trec","field"});
		String trecFile = options.getString("trec");
		String fieldName = options.getString("field");
		LBJTrecFileParser trecParser = new LBJTrecFileParser(trecFile);
		
		for (DocumentFromTrec doc : trecParser.getDocs()) {
//			System.out.println(doc.getTagForField(fieldName));
			for (String val : doc.getTagForField(fieldName)){
				System.out.println("\t" + val);
			}
		}
	}

}
