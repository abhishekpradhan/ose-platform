package ose.index.tool;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "ftp://ftp.software.ibm.com/eserver/zseries/misc/bookoffer/download/360revolution_040704.dpdf";
		System.out.println(s.matches(".*\\.(pdf|doc|ps|jpg)$"));
	}

}
