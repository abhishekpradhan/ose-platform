package lbjse.objectsearch;

import java.io.*;
import java.util.*;


public class ListMembership
{
  private static final String fileName = "pruned_lists.txt";
  private static final HashMap<String, LinkedList<String>> listNames =
    new HashMap<String, LinkedList<String>>();

  static
  {
    String inputFile =
      ListMembership.class.getResource(fileName).getFile();
    BufferedReader in = openReader(inputFile);
    String name = "";

    int countTop = 0;
    for (String line = readLine(in, inputFile); line != null;
         line = readLine(in, inputFile))
    {
      if (line.startsWith("-----")) {
    	  name = line;
    	  countTop = 0;
      }
      else
      {
        LinkedList<String> value = listNames.get(line);

        if (value == null)
        {
          value = new LinkedList<String>();
          listNames.put(line, value);
        }
        if (countTop < 10)
        	value.add(name);
        countTop += 1;
      }
    }
    
    System.out.println("Read " + listNames.size() + " words");
    int total = 0;
    for (LinkedList<String> list : listNames.values()) {
		total += list.size();
    }
    System.out.println("Average number of sem lists " + (total * 1.0 / listNames.size()));

    closeReader(in, inputFile);
  }


  public static LinkedList<String> containedIn(String word)
  {
    LinkedList<String> result = listNames.get(word);
    if (result == null) return new LinkedList<String>();
    return result;
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

