package lbjse.datacleaning;

import java.io.IOException;
import java.io.PrintWriter;

public class PageViewerResultManager{
	PrintWriter writer;
	
	public PageViewerResultManager(String outputFile) throws IOException {
		writer = new PrintWriter(outputFile);
		writer.println("<html>\n" +
				"<head>\n" +
				"<script type=\"text/javascript\">\n" +
				"/*This is for 'Previous' and 'Next' links in PageViewer to work*/\n" +
				"function openResult(t){\n" +
				"var elements = document.getElementsByTagName('li');\n" +
				"if (t < 0 || t >= elements.length){\n" +
				"alert(\"Out of range \" + t);\n" +
				"return;\n" +
				"}\n" +
				"var link = elements[t].getElementsByTagName('a');\n" +
				"eval(link[0].href);\n" +
				"var liTag = document.getElementsByTagName('li')[t];\n" +
				"parent.setResultListItemCopy(liTag.innerHTML);\n" +
				"}" +
				"</script>\n" +
				"</head>\n" +
				"<body>"
				);
		writer.println("<ol>");
	}
	
	public void finish() {
		writer.println("</ol>");
		writer.println("</body>");
		writer.println("</html>");
		writer.close();
	}
	
	public void addResult(int docCount, int domainId, int indexId, int docId, String title, String redtag ) {
		String label = "";
		if ("true".equals(redtag)){
			label = "<li><span style='color:green'>" + redtag + "</span>";
		}
		else if ("null".equals(redtag)){
			label = "<li><span style='color:black'>" + redtag + "</span>";
		}
		else {
			label = "<li><span style='color:red'>" + redtag + "</span>";
		}
		writer.println(label + "<a href='javascript:parent.loadPageViewer(" + docId + "," + indexId + "," + domainId + "," + docCount + ")'>" + title + "</a></li>");
	}
}