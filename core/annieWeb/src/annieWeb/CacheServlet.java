package annieWeb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.lucene.index.IndexReader;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ose.database.IndexInfo;
import ose.database.IndexInfoManager;
import ose.index.IndexFieldConstant;
import ose.utils.JsonIO;

/**
 * Servlet implementation class for Servlet: CacheServlet
 *
 */
 public class CacheServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	public CacheServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.setContentType("text/html");
		System.out.println("Query string : " + JsonIO.toJSONString((Map<String, String[]>)request.getParameterMap()) );
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		int indexId = -1;
		int docId = -1;
		if (request.getParameter("docId") != null){
			docId = Integer.parseInt(request.getParameter("docId"));
		}
		else{
			out.write("bad request"); out.close();
			return;
		}
		
		if (request.getParameter("indexId") != null){
			indexId = Integer.parseInt(request.getParameter("indexId"));
		}
		else{
			out.write("bad request"); out.close();
			return;
		}
		
		boolean filterJS = true;
		if (request.getParameter("nofilter") != null){
			filterJS = false;
		}
		
		out.write(getCache(indexId, docId, filterJS));
		out.close();
		
	}
	private String getCache(int indexId, int docId, boolean filterJS) {
		try {
			IndexInfo iinfo = new IndexInfoManager().getIndexForId(indexId);
			IndexReader reader = IndexReader.open(iinfo.getCachePath());
			String html = reader.document(docId).getField(IndexFieldConstant.FIELD_DOCUMENT_CONTENT).stringValue();
			if (filterJS)
				return filterJavascript(html);
			else
				return html;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}
	
	private Node filterJavascript(Node node) {
		String nodeName = node.getNodeName();
//		System.out.println("  --- " + nodeName);
		if ("script".equalsIgnoreCase(nodeName) ) {
//			if (node.getTextContent().toLowerCase().contains("location"))
				System.out.println("--- haha " + node.getTextContent());
			return null;
		} 
		if ("meta".equalsIgnoreCase(nodeName) ) {
//			if (node.getTextContent().toLowerCase().contains("location"))
				System.out.println("--- hoho " + node.getTextContent());
			return null;
		} 
		else if ("body".equalsIgnoreCase(nodeName)){
			Node onclickAttrNode = node.getOwnerDocument().createAttribute("onclick");
//			onclickAttrNode.setNodeValue("alert(parent.location)");
			onclickAttrNode.setNodeValue("parent.showSelection(event)");
			for (int i = node.getAttributes().getLength() - 1; i >= 0 ; i--)
				node.getAttributes().removeNamedItem(node.getAttributes().item(i).getNodeName());
			node.getAttributes().setNamedItem(onclickAttrNode);
		}
		Node child = node.getFirstChild();
		while (child != null){
			Node newChild = filterJavascript(child);
			if (newChild == null){
				newChild = child.getNextSibling();
				node.removeChild(child);
				child = newChild;
			}
			else{
				child = child.getNextSibling();
			}
		}
		return node;
	}
	
	private String filterJavascript(String html) {
		DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
        boolean badParse = true;
    	try {
			InputSource input = new InputSource(new StringReader(html));
			parser.parse(input, fragment);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(html.length());
			OutputFormat outputFormat = new OutputFormat(document, "utf-8",true);		
			HTMLSerializer serializer = new HTMLSerializer(outputStream, outputFormat);
			serializer.serialize((DocumentFragment) filterJavascript(fragment));
			return outputStream.toString("utf-8");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		CacheServlet servlet = new CacheServlet();
		servlet.getCache(8, 817,true);
		System.out.println(servlet.getCache(8, 817,true));
	}
}