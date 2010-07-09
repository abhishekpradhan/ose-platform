package lbjse.utils;

public class ProgressBar {
	static public void printDot(int c, int perDOT, int perLINE){
		if (c % perDOT == 0){
			System.out.print(".");
			if (c % perLINE == 0)
				System.out.println();
		}
	}
}
