package ose.index;

public interface GateCorpusReaderInterface {
	
	/**
	 * Reset the iterator, setting number of docs for each batch.
	 * @param numberOfDocsForEachRun
	 * @return
	 */
	public boolean resetIterator(int numberOfDocsForEachRun);
	
	/**
	 * Get total bytes processed sofar. 
	 * @return
	 */
	public long getTotalSize();
}
