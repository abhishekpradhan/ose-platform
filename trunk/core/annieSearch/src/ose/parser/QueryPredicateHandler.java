package ose.parser;

import ose.processor.cascader.QueryPredicate;

public interface QueryPredicateHandler {
	public QueryPredicate parseNode(ParsingNode node) throws IllegalAccessException, InstantiationException;
}
