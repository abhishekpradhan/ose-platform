package lbjse.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import common.CommandLineOption;

import lbjse.objectsearch.DocQueryPairFromFile;
import lbjse.objectsearch.LBJTrecFileParser;
import lbjse.objectsearch.ObjectInfo;
import lbjse.objectsearch.Query;

public class SplitTrecFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		if ("byQuery".equals(options.getString("opt"))){
			splitByQuery(options);
		}
		else if ("random".equals(options.getString("opt"))){
			int npart = options.getInt("npart");
			splitRandomly(options);
		}
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void splitByQuery(CommandLineOption options) throws FileNotFoundException,
			IOException, UnsupportedEncodingException {
		String documentFile = options.getString("trec");
		String docFile1 = options.getString("outputYes");
		String docFile2 = options.getString("outputNo");
		String queryJSON = options.getString("queryJSON");
		Query theQuery = new Query(null, queryJSON);		
		LBJTrecFileParser parser = new LBJTrecFileParser(documentFile);
		DocumentFromTrec trec;
		PrintWriter file1 = new PrintWriter(docFile1,"utf-8");
		PrintWriter file2 = new PrintWriter(docFile2,"utf-8");
		int countAll = 0;
		int countYes = 0;
		while ( (trec = (DocumentFromTrec)parser.next()) != null){
			countAll += 1;
			if (new DocQueryPairFromFile(trec, theQuery).oracle()){
				trec.serialize(file1);
				countYes += 1;
			}
			else {
				trec.serialize(file2);
			}
		}
		file1.close();
		file2.close();
		System.out.println("Done spliting " + countAll + " into " + countYes + " and " + (countAll - countYes) );
	}

	private static void splitRandomly(CommandLineOption options) throws FileNotFoundException,
		IOException, UnsupportedEncodingException {
		
		String inputDoc = options.getString("trec") ;
		String outputPrefix = options.getString("outputPrefix");
		LBJTrecFileParser parser = new LBJTrecFileParser(inputDoc);
		DocumentFromTrec trec;
		int numParts = options.getInt("npart");
		PrintWriter [] outputFiles = new PrintWriter[numParts];
		int [] counts = new int[numParts];
		for (int i = 0; i < numParts; i++) {
			outputFiles[i] = new PrintWriter(outputPrefix + "_" + i + ".trec");
			counts[i] = 0;
		}
		
		Random rand = new Random(System.currentTimeMillis());
		while ( (trec = (DocumentFromTrec)parser.next()) != null){
			int part = rand.nextInt(numParts);
			trec.serialize(outputFiles[part]);
			counts[part] += 1;
		}
		System.out.println("Done spliting " + inputDoc + " into " + numParts + " parts ");
		for (int i = 0; i < numParts; i++) {
			outputFiles[i].close();
			System.out.println("\tNumber of trec docs in part " + i + " : " + counts[i]);
		}
	}
}
