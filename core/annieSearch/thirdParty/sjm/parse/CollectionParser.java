package sjm.parse;

import java.util.*;

/*
 * Copyright (c) 1999 Steven J. Metsker. All Rights Reserved.
 * 
 * Steve Metsker makes no representations or warranties about
 * the fitness of this software for any particular purpose, 
 * including the implied warranty of merchantability.
 */

/**
 * This class abstracts the behavior common to parsers
 * that consist of a series of other parsers.
 * 
 * @author Steven J. Metsker
 * 
 * @version 1.0 
 */
public abstract class CollectionParser extends Parser {
	/**
	 * the parsers this parser is a collection of
	 */
	protected Vector subparsers = new Vector();
/**
 * Supports subclass constructors with no arguments.
 */
public CollectionParser() {
}
/**
 * Supports subclass constructors with a name argument
 *
 * @param   string   the name of this parser
 */
public CollectionParser(String name) {
	super(name);
}
/**
 * A convenient way to construct a CollectionParser with the
 * given parser.
 */
public CollectionParser(Parser p) {
	subparsers.add(p);;
}
/**
 * A convenient way to construct a CollectionParser with the
 * given parsers.
 */
public CollectionParser(Parser p1, Parser p2) {
	subparsers.add(p1);
	subparsers.add(p2);
}
/**
 * A convenient way to construct a CollectionParser with the
 * given parsers.
 */
public CollectionParser(Parser p1, Parser p2, Parser p3) {
	subparsers.add(p1);
	subparsers.add(p2);
	subparsers.add(p3);
}
/**
 * A convenient way to construct a CollectionParser with the
 * given parsers.
 */
public CollectionParser(
	Parser p1, 
	Parser p2, 
	Parser p3, 
	Parser p4) {
	//
	subparsers.add(p1);
	subparsers.add(p2);
	subparsers.add(p3);
	subparsers.add(p4);
}
/**
 * Adds a parser to the collection.
 *
 * @param   Parser   the parser to add
 *
 * @return   this
 */
public CollectionParser add (Parser e) {
	subparsers.addElement(e);
	return this;
}
/**
 * Return this parser's subparsers.
 *
 * @return   Vector   this parser's subparsers
 */
public Vector getSubparsers() {
	return subparsers;
}
/**
 * Helps to textually describe this CollectionParser.
 *
 * @returns   the string to place between parsers in 
 *            the collection
 */
protected abstract String toStringSeparator();
/*
 * Returns a textual description of this parser.
 */
protected String unvisitedString(Vector visited) {
	StringBuffer buf = new StringBuffer("<");
	boolean needSeparator = false;
	Enumeration e = subparsers.elements();
	while (e.hasMoreElements()) {
		if (needSeparator) {
			buf.append(toStringSeparator());
		}
		Parser next = (Parser) e.nextElement();
		buf.append(next.toString(visited));
		needSeparator = true;
	}
	buf.append(">");
	return buf.toString();
}
}