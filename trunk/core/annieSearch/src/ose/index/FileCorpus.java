/**
 * 
 */
package ose.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pham Kim Cuong
 *
 */
public class FileCorpus implements GateCorpusReaderInterface {
	
	private List<String> fileURLList;
	Iterator<String> fileIter;
	int chunkSize;
	long totalSize;
	
	private final long FILE_SIZE_LIMIT = 500000; //500k
	private final int SKIP_TO = -1; //3790; //4000;

	public FileCorpus(String directory, String extension, boolean recursive)throws IOException{
		fileURLList = findAllFiles(new File(directory), new ExtensionFilenameFilter(extension,FILE_SIZE_LIMIT), recursive);
		System.out.println("Finish adding " + fileURLList.size() + " docs.");		
	}
	
	public long getTotalSize() {
		return totalSize;
	}
	
	public boolean resetIterator(int numberOfDocsForEachRun){
		chunkSize = numberOfDocsForEachRun;
		if (chunkSize <= 0) chunkSize = fileURLList.size();
		fileIter = fileURLList.iterator();
		if (SKIP_TO > 0){
			for (int i = 0; i < SKIP_TO	; i++) {
				if (!fileIter.hasNext()){
					System.err.println("Skip too far..." + i);
				}
				else{
					System.out.println("Skipped " + fileIter.next() );
				}
			}
		}
		totalSize = 0;
		return true;
	}
	
	private String readContentFromUrl(URL u) throws IOException{
//		System.out.println("Reading " + u.getPath());
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(u.getPath()), "utf-8"));
		String line;
		while((line=reader.readLine()) != null){
			buffer.append(line + "\n");
		}
		return buffer.toString();
	}
	
	private String fixHTMLCode(String content){
		String lower = content.toLowerCase();
		if (lower.indexOf("&lt;") != -1 && lower.indexOf("&lt;") < lower.indexOf("<title>"))
			System.err.println("Bad content page ");
//		if (lower.indexOf("<html>") == -1 || lower.indexOf("</html>") == -1){
//			System.err.println("Bad content page ");
//		}
		return content;
	}
	
	static public ArrayList<String> findAllFiles(File directory, FilenameFilter fileNameFilter, boolean recursive) {
		ArrayList<String> fileURLList = new ArrayList<String>();
		if (directory != null ){
			File [] fileList = directory.listFiles();
			if (fileList != null){
				for (File  file :  fileList) {
					if (file.isDirectory()){
						if (recursive)
							fileURLList.addAll( findAllFiles(file, fileNameFilter, recursive));
					}
					else{
						if (fileNameFilter.accept(directory, file.getName()))
							fileURLList.add(file.getAbsolutePath());
					}
				}
			}
		}
		return fileURLList;
	}
}

//public FileCorpus(String aSingleFileURL) throws ResourceInstantiationException, MalformedURLException{
//fileURLList = new ArrayList<String>();
//fileURLList.add(aSingleFileURL);
//System.out.println("Finish adding " + fileURLList.size() + " docs.");
//}
