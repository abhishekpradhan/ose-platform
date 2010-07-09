package ose.parser;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import ose.utils.CommonUtils;

public class ParentNode extends ParsingNode {
	protected List<ParsingNode> children;
	
	public ParentNode(String name) {
		super(name);
		children = new ArrayList<ParsingNode>();
		
	}
	
	public void addChildNode(ParsingNode feature){
		children.add(feature);
	}

	public String toString(int level) {
		StringBuffer writer = new StringBuffer();
		writer.append(CommonUtils.tabString(level) + "[" + nodeName + "]\n");
		for (ParsingNode child : children) {
			writer.append(child.toString(level + 1) + "\n");
		}
		return writer.toString();
	}
	
	public List<ParsingNode> getChildren() {
		return children;
	}
	
	public ParsingNode getChild(int i) {
		if (i >= 0 && i < children.size())
			return children.get(i);
		else
			return null;
	}

	public ParsingNode getFirstChild(){
		if (children.size() > 0 )
			return children.get(0);
		else
			return null;
	}
	
	public boolean isFunctionNode(){
		return nodeName.length() > 0 && nodeName.startsWith("_");
	}
	
	public boolean isGeneratorNode(){
		return nodeName.length() > 0 && nodeName.startsWith("%");
	}
	
	public String reconstructString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(nodeName);
		buffer.append("(");
		
		boolean first = true;
		for (ParsingNode child : children){
			if (!first ) buffer.append(",");
			first = false;
			buffer.append(child.reconstructString());
		}
		
		buffer.append(")");
		return buffer.toString();
	}
}
