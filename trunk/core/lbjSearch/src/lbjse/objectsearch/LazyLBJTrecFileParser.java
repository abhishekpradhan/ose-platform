package lbjse.objectsearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import lbjse.data.DocumentFromTrec;
import ose.index.TrecDocument;
import ose.index.TrecFileReader;
import LBJ2.parse.Parser;

public class LazyLBJTrecFileParser implements Parser {
	private DocumentFromTrec doc;
	private int currentDoc;
	private TrecFileReader reader;
	private String trecFile;
	
	public LazyLBJTrecFileParser(String trecFile) throws IOException{
		this.trecFile = trecFile;
		reset();
	}
	
	public Object next() {
		currentDoc += 1;
		TrecDocument htmlTrec = reader.next();
		if (htmlTrec == null) return null;
		doc = new DocumentFromTrec();
		doc.setDocId(htmlTrec.getDocId());
		doc.setUrl(htmlTrec.getUrl());
		doc.setTitle(htmlTrec.getTitle());
		doc.setText(htmlTrec.getPlainBody());
		doc.setTags(htmlTrec.getTags());
		return doc;
	}

	public void reset() {
		currentDoc = 0;
		try {
			reader = new TrecFileReader(trecFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			reader = null;
		}
	}
	
	public List<DocumentFromTrec> getDocs() {
		return null;
	}
	
	public void close() throws IOException {
		reader.close();
	}
}
