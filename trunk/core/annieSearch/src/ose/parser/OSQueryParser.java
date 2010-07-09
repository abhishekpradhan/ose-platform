/**
 * 
 */
package ose.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ose.processor.cascader.BooleanFeaturePredicateHandler;
import ose.processor.cascader.BooleanQuery;
import ose.processor.cascader.FeatureGeneratorHandler;
import ose.processor.cascader.FeatureQuery;
import ose.processor.cascader.FeatureSetPredicateHandler;
import ose.processor.cascader.QueryPredicate;
import ose.processor.cascader.TFFeatureGeneratorHandler;
import ose.processor.cascader.WeightedFeatureQuery;

/**
 * @author Pham Kim Cuong
 * 
 */
public class OSQueryParser{
	
	public static final String PRED_NUMBER_BODY = "Number_body";
	public static final String PRED_NUMBER_TITLE = "Number_title";
	
	static public Map<String, Class<? extends QueryPredicateHandler>> 
		predicateHandlerMap ;
	
	static {
		predicateHandlerMap = new HashMap<String, Class<? extends QueryPredicateHandler>>();
		predicateHandlerMap.put("Token", TextPredicateHandler.class);
		predicateHandlerMap.put("HTMLTitle", TextPredicateHandler.class);
		predicateHandlerMap.put(PRED_NUMBER_BODY, NumberPredicateHandler.class);
		predicateHandlerMap.put(PRED_NUMBER_TITLE, NumberPredicateHandler.class);
		predicateHandlerMap.put("Top", TopPredicateHandler.class);
		predicateHandlerMap.put("Phrase", PhrasePredicateHandler.class);
		predicateHandlerMap.put("And", PhrasePredicateHandler.class);
		predicateHandlerMap.put("Or", PhrasePredicateHandler.class);
		predicateHandlerMap.put("Proximity", ProximityPredicateHandler.class);
		predicateHandlerMap.put("%TFFeature", TFFeatureGeneratorHandler.class);
		predicateHandlerMap.put("%BooleanFeature", BooleanFeaturePredicateHandler.class);
		predicateHandlerMap.put("%CountNumber", FeatureGeneratorHandler.class);
		predicateHandlerMap.put("%FeatureSet", FeatureSetPredicateHandler.class);
		predicateHandlerMap.put("%DivideBy", BinaryFeaturePredicateHandler.class);
		predicateHandlerMap.put("%Log", UnaryFeaturePredicateHandler.class);
		predicateHandlerMap.put("%Null", EmptyFeaturePredicateHandler.class);		
	}
	
	public BooleanQuery parseBooleanQuery(String queryString) throws IllegalAccessException, InstantiationException{
		QueryTreeParser parser = new QueryTreeParser(queryString);
		return parseBooleanQuery(parser.parseQueryNode() );
	}
	
	public BooleanQuery parseBooleanQuery(ParsingNode parseTree) throws InstantiationException, IllegalAccessException{
//		System.out.println("Parsing tree : " + parseTree);
		if (parseTree instanceof ParentNode) {
			List<QueryPredicate> predicates = new ArrayList<QueryPredicate>();
			ParentNode parentNode = (ParentNode) parseTree;
			for (ParsingNode child : parentNode.getChildren()) {
				String predName = child.getNodeName() ;
				QueryPredicateHandler handler = getPredicateHandler(predName);
				if (handler != null) {
					predicates.add( handler.parseNode(child) );
				}
				else{
					System.err.println("cannot handler this node [" + predName + "] " + child);
				}
			}
			return new BooleanQuery(predicates);
		}
		else return null;
	}

	/**
	 * @param predName
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static public QueryPredicateHandler getPredicateHandler(String predName) throws InstantiationException, IllegalAccessException {
		Class<? extends QueryPredicateHandler> handlerClass = predicateHandlerMap.get( predName );
		if (handlerClass != null){
			return handlerClass.newInstance();					
		}
		return null;
	}
	

	public FeatureQuery parseFeatureQuery(String queryString) throws IllegalAccessException, InstantiationException{
		QueryTreeParser parser = new QueryTreeParser(queryString);
		return parseFeatureQuery(parser.parseQueryNode() );
	}
	
	public WeightedFeatureQuery parseWeightedFeatureQuery(String queryString) throws IllegalAccessException, InstantiationException{
		QueryTreeParser parser = new QueryTreeParser(queryString);
		return parseWeightedFeatureQuery( parser.parseQueryNode() );
	}
	
	private FeatureQuery parseFeatureQuery(ParsingNode parseTree) throws InstantiationException, IllegalAccessException{
//		System.out.println("Parsing tree : " + parseTree);
		if (parseTree instanceof ParentNode) {
			List<QueryPredicate> predicates = new ArrayList<QueryPredicate>();
			ParentNode parentNode = (ParentNode) parseTree;
			for (ParsingNode child : parentNode.getChildren()) {
				String predName = child.getNodeName() ;
				QueryPredicateHandler handler = getPredicateHandler(predName);
				if (handler != null) {
					predicates.add( (QueryPredicate) handler.parseNode(child) );
				}
				else{
					System.err.println("cannot handler this node " + child);
				}
			}
			return new FeatureQuery(predicates);
		}
		else return null;
	}
	
	private WeightedFeatureQuery parseWeightedFeatureQuery(ParsingNode parseTree) throws InstantiationException, IllegalAccessException{
//		System.out.println("Parsing tree : " + parseTree);
		if (parseTree instanceof ParentNode) {
			List<QueryPredicate> predicates = new ArrayList<QueryPredicate>();
			List<Double> weights = new ArrayList<Double>();
			
			ParentNode parentNode = (ParentNode) parseTree;
			for (ParsingNode child : parentNode.getChildren()) {
				String predName = child.getNodeName() ;
				QueryPredicateHandler handler = getPredicateHandler(predName);
				if (handler != null) {
					predicates.add( (QueryPredicate) handler.parseNode(child) );
					if (weights.size() < predicates.size())
						weights.add(1.0);
				}
				else if (child instanceof LiteralNode) {
					LiteralNode w = (LiteralNode) child;
					weights.add(w.getNumber());
				}
				else {
					System.err.println("cannot handler this node " + child);
				}
			}
			return new WeightedFeatureQuery(predicates, weights);
		}
		else return null;
	}
}
