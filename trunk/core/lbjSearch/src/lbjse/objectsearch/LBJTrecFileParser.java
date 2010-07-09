package lbjse.objectsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lbjse.data.DocumentFromTrec;
import ose.index.TrecDocument;
import ose.index.TrecFileReader;
import LBJ2.parse.Parser;

public class LBJTrecFileParser implements Parser {
	private List<DocumentFromTrec> docs;
	private int currentDoc;
	
	public LBJTrecFileParser(String trecFile) throws IOException{
		readDocs(trecFile);
		reset();
	}
	
	public Object next() {
		if (currentDoc >= docs.size())
			return null;

		DocumentFromTrec doc = docs.get(currentDoc);
		currentDoc += 1;
		return doc;
	}

	public void reset() {
		currentDoc = 0;
	}
	
	public List<DocumentFromTrec> getDocs() {
		return docs;
	}
	
	private void readDocs(String trecFile) throws IOException{
		TrecFileReader reader = new TrecFileReader(trecFile);
		docs = new ArrayList<DocumentFromTrec>();
		while (true){
			TrecDocument htmlTrec = reader.next();
			if (htmlTrec == null) break;
			DocumentFromTrec doc = new DocumentFromTrec();
			doc.setDocId(htmlTrec.getDocId());
			doc.setUrl(htmlTrec.getUrl());
			doc.setTitle(htmlTrec.getTitle());
			doc.setText(htmlTrec.getPlainBody());
			doc.setTags(htmlTrec.getTags());
			docs.add(doc);
		}
		reader.close();
	}
	
}
