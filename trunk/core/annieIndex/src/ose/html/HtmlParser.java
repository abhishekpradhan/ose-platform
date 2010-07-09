/**
 * 
 */
package ose.html;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.html.dom.HTMLDocumentImpl;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ose.index.DOMContentUtils;

import common.profiling.Profile;

/**
 * @author KimCuong
 *
 */
public class HtmlParser {
	private boolean useTagSoup = true; //to clean up html
	
	public HtmlParser(){
	}
	
	public void setUseTagSoup(boolean useTagSoup) {
		this.useTagSoup = useTagSoup;
	}
	
	public ParsedHtmlDocument parse(String html){
		Profile.getProfile("parsing").start();
		ParsedHtmlDocument parsed = new ParsedHtmlDocument();
		DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocument document = new HTMLDocumentImpl();
        DocumentFragment fragment = document.createDocumentFragment();
        try {
        	InputSource inpSource = getInputSource(html);
        	if (inpSource == null){
        		throw new RuntimeException("Bad html");
        	}
			parser.parse(inpSource, fragment);
			DOMContentUtils utils = new DOMContentUtils();
			StringBuffer sb = new StringBuffer();			
			utils.getTextInBody(sb, fragment);
			parsed.setPlainBody(sb.toString());
			
			sb = new StringBuffer();
			utils.getTitle(sb, fragment);
			parsed.setTitle( sb.toString() );
		} catch (Exception e) {
			e.printStackTrace();
			parsed = null;
		} 
		Profile.getProfile("parsing").end();
		return parsed;
	}
	
	private InputSource getInputSource(String html) {
		if (useTagSoup){
			Parser tagSoupParser = new Parser();
	    	StringWriter stringWriter = new StringWriter();
	    	XMLWriter writer = new XMLWriter(stringWriter);
	    	InputSource input = new InputSource(new StringReader(html));
	    	tagSoupParser.setContentHandler(writer);        	
	    	try {
				tagSoupParser.parse(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return new InputSource(new StringReader(stringWriter.toString()));
		}
		else {
			return new InputSource(new StringReader(html));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
}
