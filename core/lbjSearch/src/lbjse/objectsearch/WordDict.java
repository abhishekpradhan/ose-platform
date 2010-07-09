package lbjse.objectsearch;

import java.io.*;
import java.util.*;


public class WordDict
{
  private static final String fileName = "dict.txt";
//  private static final String fileName = "wordsKim.txt";
//  private static final String fileName = "word2k.txt";
//  private static final String fileName = "laptop_dict.txt";
  
  private static final HashSet<String> wordSet = new HashSet<String>();

  static
  {
    String inputFile =
      WordDict.class.getResource(fileName).getFile();
    BufferedReader in = openReader(inputFile);
    for (String line = readLine(in, inputFile); line != null;
         line = readLine(in, inputFile))
    {
    	wordSet.add(line.trim());
    }
    System.out.println("Read " + wordSet.size() + " words from " + inputFile);
    closeReader(in, inputFile);
  }

  public static HashSet<String> getWordSet() {
	return wordSet;
  }
  
  public static boolean contains(String word)
  {
    return wordSet.contains(word);
  }


  static BufferedReader openReader(String inputFile)
  {
    BufferedReader in = null;

    // Open the input file with exception handling
    try { in = new BufferedReader(new FileReader(inputFile)); }
    catch (Exception e)
    {
      System.err.println("Can't open '" + inputFile + "' for input: " + e);
      System.exit(1);
    }

    return in;
  }


  static String readLine(BufferedReader in, String inputFile)
  {
    String line = null;

    // Read a line of input with exception handling
    try { line = in.readLine(); }
    catch (Exception e)
    {
      System.err.println("Can't read from '" + inputFile + "': " + e);
      System.exit(1);
    }

    return line;
  }


  static void closeReader(BufferedReader in, String inputFile)
  {
    // Close the input file with exception handling
    try { in.close(); }
    catch (Exception e)
    {
      System.err.println("Can't close input file '" + inputFile + "': " + e);
      System.exit(1);
    }
  }
}

