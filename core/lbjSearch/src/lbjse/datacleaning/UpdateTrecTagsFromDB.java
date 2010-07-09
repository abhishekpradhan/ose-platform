package lbjse.datacleaning;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.LazyLBJTrecFileParser;

import ose.tools.IndexExporter;
import common.CommandLineOption;

public class UpdateTrecTagsFromDB {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		String trecFile = options.getString("trec");
		String updateTrecFile = options.getString("output");
		int indexId = options.getInt("indexId");
		int domainId = options.getInt("domainId");
		Map<Integer, ArrayList<String>> tags = IndexExporter.getTagMapForIndexDomain(indexId, domainId);
		LazyLBJTrecFileParser parser = new LazyLBJTrecFileParser(trecFile);
		PrintWriter writer = new PrintWriter(updateTrecFile);
		while (true){
			DocumentFromTrec doc = (DocumentFromTrec) parser.next();
			if (doc == null) break;
			if (tags.containsKey(doc.getDocId())){
				doc.setTags(tags.get(doc.getDocId()));
				doc.serialize(writer);
			}
			else {
				System.out.println("Skip document " + doc.getDocId() + " ( " + doc.getUrl() + " ) ");
			}
		}
		writer.close();
	}
}
