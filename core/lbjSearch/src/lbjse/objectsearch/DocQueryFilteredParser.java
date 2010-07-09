package lbjse.objectsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lbjse.data.DocumentFromTrec;

import LBJ2.parse.Parser;

public class DocQueryFilteredParser implements Parser{

	private Parser docQueryParser;
	private String fieldName ; 
	private String tagToContain;
	
	public DocQueryFilteredParser(Parser docQueryParser, String field, String tag ) {
		this.docQueryParser = docQueryParser;
		this.fieldName = field;
		tagToContain = tag;
	}

	public List<Query> getQueries() {
		if (docQueryParser instanceof DocQueryFileParser) {
			DocQueryFileParser p = (DocQueryFileParser) docQueryParser;
			return p.getQueries();
		}
		return null;
	}
	
	public List<DocumentFromTrec> getDocs() {
		if (docQueryParser instanceof DocQueryFileParser) {
			DocQueryFileParser p = (DocQueryFileParser) docQueryParser;
			return p.getDocs();
		}
		return null;
	}
	
	public Object next() {
		Object p = null;
		while ( (p = docQueryParser.next()) != null){
			DocQueryPair pair = (DocQueryPair) p;
			DocumentFromTrec doc = (DocumentFromTrec) pair.getDoc();
			if (doc.getTagForField(fieldName).size() == 0 ||
					doc.getTagForField(fieldName).contains(tagToContain)){
				return pair;
			}
		}
		return null;
	}
	
	public void reset() {
		docQueryParser.reset();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
//		DocQueryFileParser parser = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query.txt");
		DocQueryFilteredParser parser = new DocQueryFilteredParser(
				new DocQueryFileParser("C:\\working\\annotated_docs_index_30000_domain_2.trec","C:\\working\\query.txt"),
				"dept", "computer");
		
		int count = 0;
		DocQueryPairFromFile pair = null;
		while ( (pair = (DocQueryPairFromFile) parser.next()) != null){ 
			count += 1;
			System.out.println(pair.getDoc() + "\t" + pair.getQuery() + "\t" + pair.oracle());
		}
		System.out.println("Total " + count + " examples. ");
	}

}
