package lbjse.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

import ose.database.DomainInfo;
import ose.database.DomainInfoManager;

import lbjse.datacleaning.PageViewerResultManager;
import lbjse.rank.ResultItem;
import common.CommandLineOption;

public class RankedResultToHtml {

	private String domainName = "alearner";
	private int domainId = 2;
	private int indexId = 30000;
	private int numResult = 1000000;
	private String hostname = "http://localhost:8080";
	private boolean showOracleOnly = false;
	
	public RankedResultToHtml() {
	}
	
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public void setNumResult(int numResult) {
		this.numResult = numResult;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
	
	public void setShowOracleOnly(boolean showOracleOnly) {
		this.showOracleOnly = showOracleOnly;
	}
	
	public int getDomainId() {
		return domainId;
	}
	
	public int getIndexId() {
		return indexId;
	}
	
	public RankedResultToHtml(int domain, int index, int nresult, String host) throws SQLException {
		domainId = domain;
		indexId = index;
		numResult = nresult;
		hostname = host;
		domainName = new DomainInfoManager().getDomainInfoForId(domain).getName();
	}
	
	public void convertToPageViewerOutput(String resultFile, String htmlFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		PageViewerResultManager resultMan = new PageViewerResultManager(htmlFile);
		
		int count = 0;
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			try {
				item = new ResultItem(line);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}			
			if (item.label == null && showOracleOnly)
				continue;
			resultMan.addResult(count, domainId, indexId, item.docId, item.title, "" + item.label);
			count += 1;
			if (count >= numResult)
				break;
		}
		resultMan.finish();
		System.out.println("Total documents : " + count + "");
		System.out.println("Output : " + htmlFile);
		
	}
	
	public void convert(String resultFile, String htmlFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(resultFile));
		PrintStream output = new PrintStream(htmlFile, "utf8");
		output.println("<html>");
		output.println("<head><title>Object Search Ranked Result</title></head>");
		output.println("<body>");
		output.println("<ol>");
		
		double averPres = 0;
		int countPos = 0;
		
		int count = 0;
		while (true){
			String line = reader.readLine();
			if (line == null)
				break;
			ResultItem item;
			try {
				item = new ResultItem(line);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}			
			if (item.label == null && showOracleOnly)
				continue;
			String color = "black";
			if (item.label != null){
				color = (item.label)?"green":"red";
			}			
			output.println("<li><span style='color:" + color + "'>" +
					"[" + item.score + "]" +
					"<a target=\"_blank\" href=\"" + item.url + "\">" + item.title + "</a> " +
					"docId = " + item.docId + "</span>" +
					"<a target=\"_blank\" href=\"" + hostname + "/annieWeb/CacheServlet?indexId=30000&docId=" + item.docId + "\">Cache File</a>" +
					"----" +
					"<a target=\"_blank\" href=\"" + hostname + "/annieWeb/" + domainName + "/pageviewer.html?indexId=" + indexId + "&docId=" + item.docId + "\">Edit Tags</a>" +
					"</li>");
			
			if (item.label != null){
				count += 1;
				if (item.label){
					countPos += 1;
					averPres += (countPos * 1.0 / count);
				}
				if (count >= numResult)
					break;
			}
		}
		averPres /= countPos;
		System.out.println("Total documents : " + count + "");
		output.println("</ol>");
		output.println("<table>");
		output.println("<tr>");
		output.println("<td>Number of docs : </td>");
		output.println("<td>" + count +"</td>");
		output.println("</tr>");
		output.println("<tr>");
		output.println("<td>Number of Positives : </td>");
		output.println("<td>" + countPos +"</td>");
		output.println("</tr>");
		output.println("<tr>");
		output.println("<td>Average Precision : </td>");
		output.println("<td>" + averPres +"</td>");
		output.println("</tr>");
		
		output.println("</table>");
		output.println("</body>");
		output.println("</html>");
		
		output.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException , SQLException{
		CommandLineOption options = new CommandLineOption(args);
		options.require(new String[]{"mode","rresult","html"});
		String resultFile = options.getString("rresult");
		String htmlFile = options.getString("html");
		RankedResultToHtml converter = new RankedResultToHtml();
		if (options.hasArg("indexId")){
			converter.indexId = options.getInt("indexId");
		}
		if (options.hasArg("domain")){
			String domain = options.getString("domain");
			converter.domainName = domain;
			List<DomainInfo> domains = new DomainInfoManager().query("select * from DomainInfo where Name='" + domain + "'");
			if (domains.size() != 1){
				System.out.println("Invalid domain " + domain);
				return;
			}
			System.out.println("Got domain " + domains.get(0));
			converter.domainId = domains.get(0).getDomainId();
		}		
		if (options.hasArg("filter")){
			converter.showOracleOnly = true;
		}
		if (options.hasArg("top")){
			converter.numResult = options.getInt("top");
		}
		
		if ("normal".equals(options.getString("mode"))){
			converter.convert(resultFile, htmlFile);
		}
		else if ("pageviewer".equals(options.getString("mode"))){
			converter.convertToPageViewerOutput(resultFile, htmlFile);
		}
		else {
			System.err.println("Unknown mode : " + options.getString("mode"));
		}
		System.out.println("Done. ");
	}

}
