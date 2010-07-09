package ose.index;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilenameFilter implements FilenameFilter {

	private long fileSizeLimit;
	String extension;
	
	/* 
	 * if sizeLimit = -1 --> no limit
	 */
	public ExtensionFilenameFilter(String ext, long sizeLimit) {
		extension = "." + ext;
		fileSizeLimit = sizeLimit;
	}

	public boolean accept(File dir, String name) {
		if (fileSizeLimit != -1 && new File(dir,name).length() > fileSizeLimit){
			return false;
		}
		else
			return name.endsWith(extension);
	}

}
