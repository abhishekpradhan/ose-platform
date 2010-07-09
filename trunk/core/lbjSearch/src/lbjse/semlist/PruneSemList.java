package lbjse.semlist;

import java.io.*;
import java.util.*;

import lbjse.objectsearch.ListMembership;

public class PruneSemList {

	private static final int TOPK_THRESHOLD = 10;
	private HashMap<String, LinkedList<String>> listNames ;

	public PruneSemList(String fileName) {
		listNames = new HashMap<String, LinkedList<String>>();
		
		String inputFile = ListMembership.class.getResource(fileName).getFile();
		BufferedReader in = Utils.openReader(inputFile);
		String name = "";

		int countTop = 0;
		for (String line = Utils.readLine(in, inputFile); line != null; line = Utils.readLine(
				in, inputFile)) {
			if (line.startsWith("-----")) {
				name = line.substring(5);
				countTop = 0;
			} else {
				LinkedList<String> value = listNames.get(name);

				if (value == null) {
					value = new LinkedList<String>();
					listNames.put(name, value);
				}
				if (countTop < TOPK_THRESHOLD)
					value.add(line);
				countTop += 1;
			}
		}

		
		int total = 0;
		for (LinkedList<String> list : listNames.values()) {
			total += list.size();
		}
		System.out.println("Read " + listNames.size() + " lists");
		System.out.println("\t" + total + " words");
		System.out.println("Average number of sem lists "
				+ (total * 1.0 / listNames.size()));

		Utils.closeReader(in, inputFile);
	}

	private void pruneAndWriteToFile(String sourceFeature, String outputFile) throws IOException{
		PrintWriter out = new PrintWriter(outputFile);
		Set<String> featureToUse = readWordSetFromFile(sourceFeature);
		for (String listName : listNames.keySet()) {
			List<String> words = listNames.get(listName);
			if (containOnce(featureToUse, words) ){
				writeListToFile(listName, words, out);
			}
		}
		out.close();
		System.out.println("Done pruning " + outputFile);
	}
	
	private void keepFeatureOnBothDomains(String sourceFeature, String targetFeature, String outputFile) throws IOException{
		PrintWriter out = new PrintWriter(outputFile);
		Set<String> computerWords = readWordSetFromFile(sourceFeature);
		Set<String> mathWords = readWordSetFromFile(targetFeature);
		for (String listName : listNames.keySet()) {
			List<String> words = listNames.get(listName);
			if (containOnce(computerWords, words) && containOnce(mathWords, words)){
				writeListToFile(listName, words, out);
			}
		}
		out.close();
		System.out.println("Done pruning " + outputFile);
	}
	
	private void writeListToFile(String listName, List<String> words, PrintWriter out){
		out.println("-----" + listName);
		for (String w : words) {
			out.println(w);
		}
	}
	
	private boolean containOnce(Set<String> container, List<String> containee){
		for (String string : containee) {
			if (container.contains(string)){
				return true;
			}
		}
		return false;
	}
	
	public Set<String> readWordSetFromFile(String fileName){
		String inputFile = PruneSemList.class.getResource(fileName).getFile();
		BufferedReader in = Utils.openReader(inputFile);
		
		Set<String> wordSet = new HashSet<String>();
		
		for (String line = Utils.readLine(in, inputFile); line != null; line = Utils.readLine(
				in, inputFile)) {
			wordSet.add(line.trim());
		}
		return wordSet;
	}
	
	public static void main(String[] args) throws Exception {
		PruneSemList pruner = new PruneSemList("lists.txt");
		pruner.pruneAndWriteToFile("words.txt", "pruned_lists.txt");
//		pruner.pruneAndWriteToFile("computer_words.txt", "math_words.txt", "pruned_list.txt");
	}
}
