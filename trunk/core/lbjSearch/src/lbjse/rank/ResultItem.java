package lbjse.rank;

public class ResultItem {

	private static final String REPLACER = "____";
	public int docId;
	public String url;
	public String title;
	public double score;
	public Boolean label;
	public String residue;
	
	public ResultItem(int docId, String url, String title, double score,
			Boolean label) {
		this.docId = docId;
		this.url = url;
		this.title = title;
		this.score = score;
		this.label = label;
		residue = null;
	}
	
	public ResultItem(String repr) {
		String [] fields = repr.split("\t");
		if (fields.length < 4){
			throw new RuntimeException("bad line : " + repr);
		}
		else {
			if ("null".equals(fields[0]))
				label = null;
			else
				label = Boolean.parseBoolean(fields[0]);
			score = Double.parseDouble(fields[1]);
			docId = Integer.parseInt(fields[2]);
			url = fields[3];
			if (fields.length > 4)
				title = fields[4];
			else
				title = "";
			residue = "";
			for (int i = 5; i < fields.length; i++) {
				residue += "\t" + fields[i];
			}
		}
	}
	
	/**
	 * @param pair
	 * @param score
	 * @param label
	 * @return
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(label + "\t" + score + "\t" + 
				docId + "\t" + 
				url.trim() + "\t" + title.replaceAll("[\\n\\t]", REPLACER));
		if (residue != null)
			buffer.append(residue);
		return buffer.toString();
	}

}
