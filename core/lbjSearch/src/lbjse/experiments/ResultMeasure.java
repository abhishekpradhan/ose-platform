package lbjse.experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import lbjse.rank.ResultItem;

import common.CommandLineOption;

public class ResultMeasure {

	private int posCount;
	private double avgPres;
	
	/**
	 * @param resultFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public double showMAP(String resultFile) throws FileNotFoundException,
			IOException {
		ResultMeasure cache = new ResultMeasure();
		cache.calculateMAP(resultFile);
		
		System.out.println("\t Positive Count : " + cache.posCount );
		System.out.println("\t Average Precision : " + cache.avgPres );
		return cache.avgPres;
	}

	public Double calculateMAP(String resultFile)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		int count = 0;
		posCount = 0;
		double sum = 0;
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			item = new ResultItem(line);
			if (item.label != null) {
				count += 1;
				if (item.label){
					posCount += 1;
					sum += posCount * 1.0 / count;
//					System.out.println("---- " + (posCount * 1.0 / count));
				}
			}
		}
		if (posCount == 0)
			avgPres = 0;
		else 
			avgPres = sum / posCount;
		return avgPres;
	}
	
	public void showPositiveRanks(String cacheFile)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
		int count = 0;
		int posCount = 0;
		double sum = 0;
		
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			item = new ResultItem(line);
			if (item.label == null) continue;
			count += 1;
			if (item.label){
				System.out.println("\t" + count + "\t,doc=" + item.docId);
				posCount += 1;
				sum += posCount * 1.0 / count;
			}
		}
		System.out.println("Total : " + posCount + "\t, Average Precision : " + (sum / posCount) );
	}
	
	public double getAvgPres() {
		return avgPres;
	}
	
	public int getPosCount() {
		return posCount;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"opt","rresult"});
		ResultMeasure test = new ResultMeasure();
		String rresultFile = options.getString("rresult");
		
		if ("map".equals(options.getString("opt"))){
			test.showMAP(rresultFile);
		}
		else if ("pos".equals(options.getString("opt"))){
			test.showPositiveRanks(rresultFile);
		}
	}
}
