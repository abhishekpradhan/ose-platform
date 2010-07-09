/**
 * 
 */
package ose.parser;

import java.io.StringWriter;

/**
 * @author Pham Kim Cuong
 *
 */
public class ParsingNode {
	protected String nodeName;
	
	public ParsingNode() {
		nodeName = null;
	}
	
	public ParsingNode(String name) {
		nodeName = name;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	@Override
	public String toString() {
		return toString(0);
	};

	public String toString(int level){
		return null;
	}
	
	public String reconstructString(){
		return null;
	}
}
