package lbjse.semlist;

import java.io.BufferedReader;
import java.io.FileReader;

public class Utils {

	static BufferedReader openReader(String inputFile) {
		BufferedReader in = null;
	
		// Open the input file with exception handling
		try {
			in = new BufferedReader(new FileReader(inputFile));
		} catch (Exception e) {
			System.err
					.println("Can't open '" + inputFile + "' for input: " + e);
			System.exit(1);
		}
	
		return in;
	}

	static String readLine(BufferedReader in, String inputFile) {
		String line = null;
	
		// Read a line of input with exception handling
		try {
			line = in.readLine();
		} catch (Exception e) {
			System.err.println("Can't read from '" + inputFile + "': " + e);
			System.exit(1);
		}
	
		return line;
	}

	static void closeReader(BufferedReader in, String inputFile) {
		// Close the input file with exception handling
		try {
			in.close();
		} catch (Exception e) {
			System.err.println("Can't close input file '" + inputFile + "': "
					+ e);
			System.exit(1);
		}
	}

}
