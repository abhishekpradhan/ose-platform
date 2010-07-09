package lbjse.trainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ose.query.OQuery;

import common.CommandLineOption;

public class QueryMixer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{		
		CommandLineOption options = new CommandLineOption(args);
		options.setUsage("");
		String [] fields = options.getStrings("fields");
		Map<String, List<String>> fieldValues = new HashMap<String, List<String>>();
		for (String fieldName : fields) {
			String valueFile = options.getString(fieldName);
			List<String> values = readValuesFromFile(valueFile);
			fieldValues.put(fieldName, values);
		}
		int nQuery = options.getInt("n");
		
		Random random = new Random();
		for (int i = 0; i < nQuery; i++) {
			OQuery query = new OQuery();
			for (String fieldName : fields) {
				List<String> values = fieldValues.get(fieldName);
				if (values == null) throw new RuntimeException("invalid field " + fieldName);
				int n = values.size();
				query.setFieldValue(fieldName, values.get(random.nextInt(n)));
			}
			System.out.println(query.getJsonString());
		}
	}
	
	public static List<String> readValuesFromFile(String fileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		List<String> lines = new ArrayList<String>();
		for (String line = reader.readLine(); line != null; line = reader.readLine() ) {
			lines.add(line);
		}
		return lines;
	}

}
