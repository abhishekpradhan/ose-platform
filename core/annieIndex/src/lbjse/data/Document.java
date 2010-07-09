package lbjse.data;

import java.util.List;

public interface Document {
	public int getDocId();
	public String toString();
	public String getTitle();
	public String getBody() ;
	public String getUrl() ;
	public List<String >getTokenizedTitle();
	public List<String> getTokenizedBody();
}
