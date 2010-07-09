package ose.index.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ose.database.DocTag;
import ose.database.DocTagManager;

import common.CommandLineOption;

public class DocTagTranslation {
	
	private Map<Integer, Integer> docIdMap;
	
	public DocTagTranslation(String translationFile) throws Exception{
		docIdMap = new HashMap<Integer, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(translationFile));
		String line = null;
		while ( (line = reader.readLine()) != null){
			String [] values = line.split("\t");
			if (values.length < 2)
				throw new RuntimeException("bad line : " + line);
			int from = Integer.parseInt(values[0]);
			int to = Integer.parseInt(values[1]);
			docIdMap.put(from, to);
		}
		System.out.println("Done reading : " + docIdMap.size() + " translations. ");
	}
	
	public void translate(int oldIndexId, int newIndexId) throws SQLException{
		DocTagManager man = new DocTagManager();
		List<DocTag> tags = man.getAllTagForIndex(oldIndexId);
		int count = 0;
		for (DocTag tag : tags) {
			if (docIdMap.containsKey(tag.getDocId())){
				DocTag newTag = new DocTag(newIndexId, docIdMap.get(tag.getDocId()), tag.getFieldId(), tag.getValue());
				man.insert(newTag);
				count += 1;
			}
		}
		System.out.println("Translated " + count + " tags");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"transInfo","oldIndex", "newIndex"});
		int oldIndexId = options.getInt("oldIndex");
		int newIndexId = options.getInt("newIndex");
		String translationFile = options.getString("transInfo");
		DocTagTranslation runner = new DocTagTranslation(translationFile);
		runner.translate(oldIndexId, newIndexId);

	}

}
