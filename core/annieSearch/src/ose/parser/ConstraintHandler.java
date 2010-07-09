package ose.parser;

import java.util.HashMap;
import java.util.Map;

import ose.processor.cascader.Constraint;

/*
 * This class handle parsing node of constraints 
 * e.g: Range Function constraint
 */
public class ConstraintHandler {
	static private Map<String, Class<? extends ConstraintHandler>> functionHandlerMap;
	
	static {
		functionHandlerMap = new HashMap<String, Class<? extends ConstraintHandler>>();
		functionHandlerMap.put("_range", RangeFunctionHandler.class);
	}
	public ConstraintHandler() {
	}
	
	public Constraint parseNode(ParsingNode node) {
		if (node instanceof ParentNode) {
			ParentNode pNode = (ParentNode) node;
			if (pNode.isFunctionNode()){
				String funcName = pNode.getNodeName();
				Class<? extends ConstraintHandler> handlerClass = functionHandlerMap.get(funcName);
				if (handlerClass != null){
					ConstraintHandler handlerObj;
					try {
						handlerObj = handlerClass.newInstance();
						return handlerObj.parseNode(node);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.err.println("Bad node " + node );
		return null;
	}
	
	
}
