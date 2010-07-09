/**
 * 
 */
package ose.index;

/**
 * @author Pham Kim Cuong
 *
 */
public final class IndexFieldConstant {
	/*
	 * ALL-CAPITAL : untokenized, unindexed
	 * Other : tokenized, indexed.
	 */
	public static final String FIELD_DOCUMENT_TITLE = "DOCUMENT_TITLE";
	public static final String FIELD_PLAIN_BODY = "DOCUMENT_PLAIN_BODY";
	public static final String FIELD_DOCUMENT_ID = "DOCUMENT_ID";
	public static final String FIELD_DOCUMENT_CONTENT = "DOCUMENT_CONTENT";
	public static final String FIELD_BODY = "Token";
	public static final String FIELD_HTMLTITLE = "HTMLTitle";
	public static final String FIELD_ANNOTATION = "Annotation";
	public static final String TERM_NUMBER = "_number";
}
