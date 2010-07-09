package ose.index;

import org.w3c.dom.Node;

public class DOMContentUtils {
	/**
	   * This method takes a {@link StringBuffer} and a DOM {@link Node},
	   * and will append all the content text found beneath the DOM node to 
	   * the <code>StringBuffer</code>.
	   *
	   * <p>
	   *
	   * If <code>abortOnNestedAnchors</code> is true, DOM traversal will
	   * be aborted and the <code>StringBuffer</code> will not contain
	   * any text encountered after a nested anchor is found.
	   * 
	   * <p>
	   *
	   * @return true if nested anchors were found
	   */
	  public boolean getText(StringBuffer sb, Node node, 
	                                      boolean abortOnNestedAnchors) {
	    if (getTextHelper(sb, node, abortOnNestedAnchors, 0)) {
	      return true;
	    } 
	    return false;
	  }


	  /**
	   * This is a convinience method, equivalent to {@link
	   * #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
	   * 
	   */
	  public void getText(StringBuffer sb, Node node) {
	    getText(sb, node, false);
	  }

	  // returns true if abortOnNestedAnchors is true and we find nested 
	  // anchors
	  private boolean getTextHelper(StringBuffer sb, Node node, 
	                                             boolean abortOnNestedAnchors,
	                                             int anchorDepth) {
	    boolean abort = false;
	    NodeWalker walker = new NodeWalker(node);
	    
	    while (walker.hasNext()) {
	    
	      Node currentNode = walker.nextNode();
	      String nodeName = currentNode.getNodeName();
	      short nodeType = currentNode.getNodeType();
	      
	      if ("script".equalsIgnoreCase(nodeName)) {
	        walker.skipChildren();
	      }
	      if ("style".equalsIgnoreCase(nodeName)) {
	        walker.skipChildren();
	      }
	      if (abortOnNestedAnchors && "a".equalsIgnoreCase(nodeName)) {
	        anchorDepth++;
	        if (anchorDepth > 1) {
	          abort = true;
	          break;
	        }        
	      }
	      if (nodeType == Node.COMMENT_NODE) {
	        walker.skipChildren();
	      }
	      if (nodeType == Node.TEXT_NODE) {
	        // cleanup and trim the value
	        String text = currentNode.getNodeValue();
	        text = text.replaceAll("\\s+", " ");
	        text = text.trim();
	        if (text.length() > 0) {
	          if (sb.length() > 0) sb.append(' ');
	        	sb.append(text);
	        }
	      }
	    }
	    
	    return abort;
	  }
	  
	// returns true if abortOnNestedAnchors is true and we find nested 
	  // anchors
	  private boolean getTextHelperInBody(StringBuffer sb, Node node) {
	    boolean abort = false;
	    NodeWalker walker = new NodeWalker(node);
	    boolean insideBody = false;
	    while (walker.hasNext()) {
	    
	      Node currentNode = walker.nextNode();
	      String nodeName = currentNode.getNodeName();
	      short nodeType = currentNode.getNodeType();
	      
	      if ("script".equalsIgnoreCase(nodeName)) {
	        walker.skipChildren();
	      }
	      if ("style".equalsIgnoreCase(nodeName)) {
	        walker.skipChildren();
	      }
	      if ("body".equalsIgnoreCase(nodeName)) {
	    	  insideBody = true;
		  }
	      if (nodeType == Node.COMMENT_NODE) {
	        walker.skipChildren();
	      }
	      if (nodeType == Node.TEXT_NODE && insideBody) {
	        // cleanup and trim the value
	        String text = currentNode.getNodeValue();
	        text = text.replaceAll("\\s+", " ");
	        text = text.trim();
	        if (text.length() > 0) {
	          if (sb.length() > 0) sb.append(' ');
	        	sb.append(text);
	        }
	      }
	    }
	    
	    return abort;
	  }

	  /**
	   * This method takes a {@link StringBuffer} and a DOM {@link Node},
	   * and will append the content text found beneath the first
	   * <code>title</code> node to the <code>StringBuffer</code>.
	   *
	   * @return true if a title node was found, false otherwise
	   */
	  public boolean getTitle(StringBuffer sb, Node node) {
	    
	    NodeWalker walker = new NodeWalker(node);
	    
	    while (walker.hasNext()) {
	  
	      Node currentNode = walker.nextNode();
	      String nodeName = currentNode.getNodeName();
	      short nodeType = currentNode.getNodeType();
	      
//	      if ("body".equalsIgnoreCase(nodeName)) { // stop after HEAD
//	        return false;
//	      }
	  
	      if (nodeType == Node.ELEMENT_NODE) {
	        if ("title".equalsIgnoreCase(nodeName)) {
	          getText(sb, currentNode);
	          return true;
	        }
	      }
	    }      
	    
	    return false;
	  }
	  
	  /**
	   * This is a convinience method, equivalent to {@link
	   * #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
	   * 
	   */
	  public void getTextInBody(StringBuffer sb, Node node) {
	    getTextHelperInBody(sb, node);
	  }
}
