package lbjse.experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lbjse.rank.ResultItem;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;
import ose.index.TrecDocument;
import ose.index.tool.TrecFilter;
import common.CommandLineOption;

public class GoogleResultToRankedResult {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"fromQuery","toQuery","prefix", "index","outputPrefix"});
		String output = options.getString("outputPrefix");
		String resultFile = options.getString("prefix");
		String indexPath = options.getString("index");
		try {
			int indexId = Integer.parseInt(indexPath);
			indexPath = new IndexInfoManager().getIndexForId(indexId).getCachePath();
		} catch (Exception e) {
			//do nothing. The indexPath is perhaps a location
		}
		IndexReader reader = IndexReader.open(indexPath);
		int topK = Integer.MAX_VALUE;
		if (options.hasArg("topK")){
			topK = options.getInt("topK");
		}
		
		Map<String, Integer> urlDocIdMap = new HashMap<String, Integer>();
		List<String> titles = new ArrayList<String>();
		for (int i = 0; i < reader.numDocs(); i++) {
			Document doc = reader.document(i);
			String url = doc.get(IndexFieldConstant.FIELD_DOCUMENT_ID);
			urlDocIdMap.put(url, i);
			titles.add(doc.get(IndexFieldConstant.FIELD_DOCUMENT_TITLE));
		}
		reader.close();
		for (int i = options.getInt("fromQuery"); i <= options.getInt("toQuery"); i++) {
			convertGoogleResultToRankedResult(output + "." + i, resultFile + "." + i, urlDocIdMap,
					titles, topK );	
		}
	}

	/**
	 * @param output
	 * @param resultFile
	 * @param urlDocIdMap
	 * @param titles
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void convertGoogleResultToRankedResult(String output,
			String resultFile, Map<String, Integer> urlDocIdMap,
			List<String> titles, int topK) throws FileNotFoundException, IOException {
		System.out.println("Converting " + resultFile + " to " + output);
		BufferedReader fileReader = new BufferedReader(new FileReader(resultFile));
		PrintWriter writer = new PrintWriter(output);
		int count = 0;
		while (true){
			String line = fileReader.readLine();
			if (line == null)
				break;
			line = line.trim();
			count += 1;
			if (count > topK)
				break;
			String url = TrecFilter.urlNormalize(line);
			if (urlDocIdMap.containsKey(url )){
				int i = urlDocIdMap.get(url );
				ResultItem item = new ResultItem(i,url , titles.get(i), count , null);
				writer.println(item.toString());
			}
			else {
				System.out.println("Url not found at " + count + " : " + url );
			}
		}
		writer.close();
		fileReader.close();
	}

}
