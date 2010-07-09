package lbjse.experiments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONException;

import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.Query;
import common.CommandLineOption;

public class QueryGenerator {

	String trecFile ;
	
	public QueryGenerator(String trecFile) {
		this.trecFile = trecFile;
	}
	
	private void generateRandomQueryByField(int count, String [] fieldNames) throws IOException, JSONException{
		LBJTrecFileParser parser = new LBJTrecFileParser(trecFile);
		Random random = new Random(System.currentTimeMillis());
		Set<String> querySet = new HashSet<String>();
		while (true){
			DocumentFromTrec doc= (DocumentFromTrec) parser.next();
			if (doc == null) break;
			Query query = new Query();
			for (int i = 0; i < fieldNames.length; i++) {
				List<String> tags = doc.getTagForField(fieldNames[i]);
				if (tags.size() == 0){
					query = null;
					break;
				}
				query.setFieldValue(fieldNames[i], tags.get(random.nextInt(tags.size())));
			}
			if (query != null){
				querySet.add(query.toJSONString());
			}
		}
		List<String> queries = new ArrayList<String>(querySet);
		for (int i = 0; i < count; i++) {
			int k = random.nextInt(queries.size());
			System.out.println(queries.get(k));
			queries.remove(k);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"mode","trec"});
		QueryGenerator generator = new QueryGenerator(options.getString("trec"));
		if ("byfield".equals(options.getString("mode"))){
			options.require(new String[]{"fields"});
			generator.generateRandomQueryByField(options.getInt("n"),options.getStrings("fields"));
		}
	}

}
