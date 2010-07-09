package lbjse.objectsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.GenericPair;

import ose.index.OSToken;
import ose.index.OSTokenizer;
import ose.parser.RangeFunctionHandler;
import ose.processor.cascader.RangeConstraint;
import LBJ2.classify.Classifier;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.RealFeature;
import LBJ2.nlp.Word;
import LBJ2.parse.LinkedVector;

//TODO : use tokenized version instead of Strings --> more efficient

public class Utils {


	/*
	 * compute "oracle" for pair of (Doc,Query) pair
	 * p0 "ranked" higher than p1 and "true" is "ranked higher" than "false"
	 */
	public static boolean oracle(GenericPair<DocQueryPair, DocQueryPair> pairs){
		return pairs.getFirst().oracle() && !pairs.getSecond().oracle(); 
	}
	
	/*
	 * return if a contains b, b is one word;
	 */
	static public boolean contains(String a, String b){
		if (b == null)
			return false;
		b = b.toLowerCase();
		OSTokenizer tokenizer = new OSTokenizer(a.toLowerCase());
		while (true){
			OSToken tok = tokenizer.nextToken();
			if (tok == null) break;
			if (tok.getString().equals(b))
				return true;
		}
		return false; 
	}

	static public boolean containsSome(String container, String [] containees){
		if (containees == null)
			return false;
		Set<String> termSet = new HashSet<String>();
		for (int i = 0; i < containees.length; i++) {
			termSet.add(containees[i].toLowerCase());
		}
		OSTokenizer tokenizer = new OSTokenizer(container.toLowerCase());
		while (true){
			OSToken tok = tokenizer.nextToken();
			if (tok == null) break;
			if (termSet.contains(tok.getString()))
				return true;
		}
		return false; 
	}
	
	static public int count(String a, String b){
		int count = 0;
		if (b == null)
			return count;
		b = b.toLowerCase();
		OSTokenizer tokenizer = new OSTokenizer(a.toLowerCase());
		
		while (true){
			OSToken tok = tokenizer.nextToken();
			if (tok == null) break;
			if (tok.getString().equals(b))
				count += 1;
		}
		return count; 
	}

	/* this version matches constraint as a phrase */
	public static boolean satisfyConstraint(Word token, String constraint){
		if (constraint.startsWith("_range")){ //number constraint
			RangeConstraint rc = RangeFunctionHandler.parse(constraint);
			return rc.satisfy(token.form);
		}
		else{ //string matching
			for (String w : constraint.split("\\s+")){
				if (token == null)
					return false;
				if ( !token.form.equalsIgnoreCase(w))
					return false;
				token = (Word) token.next;
			}
			return true;
		}
	}
	
	/* this version matches if at least one word in the constraint matches*/
	public static boolean satisfyLooseConstraint(Word token, String constraint){
		if (constraint.startsWith("_range")){ //number constraint
			RangeConstraint rc = RangeFunctionHandler.parse(constraint);
			return rc.satisfy(token.form);
		}
		else{ //string matching
			if (token == null)
				return false;
			for (String w : constraint.split("\\s+")){
				if ( token.form.equalsIgnoreCase(w))
					return true;
			}
			return false;
		}
	}
	
	static public LinkedVector getSurroundingFeatures(String text, String pivot,
			int low, int high) {		
		LinkedVector vector = getTokenizedWords(text);
		return getSurroundingFeatures(vector, pivot, low, high);
	}
	
	static public LinkedVector getSurroundingFeatures(LinkedVector vector, 
			String pivot, int low, int high) {
	    return getSurroundingFeatures(vector, pivot,
	          new HashSet<String>(Collections.singletonList(pivot.toLowerCase())),
	          null, low, high);
	}

  static public LinkedVector
    getSurroundingFeatures(LinkedVector vector, String pivot,
                           Set<String> pivots, int low, int high) {
    return getSurroundingFeatures(vector, pivot, pivots, null, low, high);
  }

  static public LinkedVector
    getSurroundingFeatures(LinkedVector vector, String pivot,
                           RangeConstraint rc, int low, int high) {
    return getSurroundingFeatures(vector, pivot, null, rc, low, high);
  }

  static public LinkedVector
    getSurroundingFeatures(LinkedVector vector, String pivot,
                           Set<String> pivots, RangeConstraint rc, int low,
                           int high) {
		
		Map<Integer, Collection<String>> featureMap = new HashMap<Integer, Collection<String>>();

		for (Word word = (Word) vector.get(0); word != null ; word = (Word)word.next) {
			if (rc == null && satisfyLooseConstraint(word, pivot)
          || rc != null && rc.satisfy(word.form)){
				int i;
				Word w = word;
				for (i = 0; i > low && w.previous != null; --i) w = (Word) w.previous;
				while (i <= high && w != null){
          String form = w.form.toLowerCase();
          if (pivots != null && pivots.contains(form)
              || rc != null && rc.satisfy(w.form))
            form = "\\*/";
          //insert w into collection[i]
          if (!featureMap.containsKey(i))
            featureMap.put(i, new HashSet<String>());
          Collection<String> c = featureMap.get(i);
          c.add(form);
          i += 1;
          w = (Word) w.next;
        }
			}
		}
		LinkedVector res = new LinkedVector();
		for (int pos : featureMap.keySet()){
			if (pos == 0) continue; //skip the position of the word
			Collection<String> col = featureMap.get(pos);
			for (String s : col){
				res.add(new PositionWordPair(pos,s));
			}
		}
		return res;
	}

	/**
	 * @param text
	 * @return
	 */
	private static LinkedVector getTokenizedWords(String text) {
		LinkedVector vector = new LinkedVector();
		OSTokenizer tokenizer = new OSTokenizer(text.toLowerCase());
		OSToken tok = null;
		while ( (tok = tokenizer.nextToken()) != null){
			if (tok.getLabel() != OSToken.TOK_SPACE)
				vector.add(new Word(tok.getString()));
		}
		return vector;
	}
	
	public static List<String> getTokenizedString(String str){
		return ose.index.Utils.getTokenizedString(str);
	}
	
	static public LinkedVector convertToLinkedVector(List<String> strings){
		Iterator<String> item = strings.iterator();
		Word current = null;
		while (item.hasNext()){
			if (current == null){
				current = new Word(item.next());
			}
			else{
				current.next = new Word(item.next());
				current.next.previous = current;
				current = (Word) current.next;
			}
		}
		if (current == null){
			return new LinkedVector();
		}
		else
			return new LinkedVector(current);
	}

	public static Map<String, Double> filterConcreteFeatures(Map<String, Double> fMap, Set<String> concreteFeatures){
		Map<String, Double> filtered = new HashMap<String, Double>();
		for (Map.Entry<String, Double> entry : fMap.entrySet()){
			if (concreteFeatures.contains(entry.getKey()))
				filtered.put(entry.getKey(), entry.getValue());
		}
		return filtered;
	}
	
	/*
	 * Use List instead of "List<RealFeature>" because lbj can't handle java 1.5
	 */
	public static List differentiateFeatures(
			Classifier featureGen, DocQueryPair pairYes,
			DocQueryPair pairNo) {
		Map<String, Double> fMap1 = getFeatureMap(featureGen, pairYes);
		Map<String, Double> fMap2 = getFeatureMap(featureGen, pairNo);
		return differentiateFeatures(fMap1, fMap2);
	}

	public static List differentiateFeatures(
			Classifier featureGen, DocQueryPair pairYes,
			DocQueryPair pairNo, Set<String> concreteFeatures) {
		Map<String, Double> fMap1 = getFeatureMap(featureGen, pairYes);
		Map<String, Double> fMap2 = getFeatureMap(featureGen, pairNo);
		return differentiateFeatures(filterConcreteFeatures(fMap1, concreteFeatures), filterConcreteFeatures(fMap2, concreteFeatures) );
	}
	
	public static List differentiateFeatures(Map<String, Double> fMap1,
			Map<String, Double> fMap2) {
		List<RealFeature> diffFeatures = new ArrayList<RealFeature>();
		for (Iterator<Map.Entry<String, Double>> it = fMap1.entrySet().iterator(); it.hasNext(); ){
			Map.Entry<String, Double> entry = it.next();
		    double c1 = ((Double) entry.getValue()).doubleValue();
		    double c2 = 0;
		    if (fMap2.containsKey(entry.getKey())){
		        c2 = ((Double) fMap2.get(entry.getKey())).doubleValue();
		    }
		    if (c1 == c2) continue; //ignore "zeroed" feature
	        String[] parts = entry.getKey().split(":");
			String p = parts[0];
	        String i = parts[1];
	        diffFeatures.add(new RealFeature(p, i, c1 - c2));
		}
		
		for (Iterator<Map.Entry<String, Double>> it = fMap2.entrySet().iterator(); it.hasNext(); ){
			Map.Entry<String, Double> entry = it.next();
		    if (!fMap1.containsKey(entry.getKey())){
		        double c2 = entry.getValue();
		        String[] parts = entry.getKey().split(":");
				String p = parts[0];
		        String i = parts[1];
		        diffFeatures.add(new RealFeature(p, i, - c2));
		    }
		}
		return diffFeatures;
	}
	
	
	public static FeatureVector pointwiseFeatureVector(Classifier dqFeatureGen, DocQueryPair ex, Classifier pairFeatureGen) {
		Map<String, Double> fMap = getFeatureMap(dqFeatureGen, ex);
		FeatureVector fv = new FeatureVector();
		for (Iterator<Map.Entry<String, Double>> it = fMap.entrySet().iterator(); it.hasNext(); ){
			Map.Entry<String, Double> entry = it.next();
		    double c1 = ((Double) entry.getValue()).doubleValue();
	        RealFeature realFeature = convertPointwiseFeatureToPairwise(entry.getKey(), c1, dqFeatureGen.name, pairFeatureGen.containingPackage);
			fv.addFeature(realFeature );
		}
		
		return fv;
	}

	public static RealFeature convertPointwiseFeatureToPairwise(String featureString,
			double c1, String featureName, String containingPackage) {
		String[] parts = featureString.split(":");
		String p = parts[0];
		//TODO: convention here : the pairwise feature generator is always the same as pointwise generator
		//this is so that we can swap them here.
        String i = featureName + parts[1]; 
        return new RealFeature(containingPackage, i, c1);
	}
	
	public static RealFeature convertPointwiseFeatureToPairwise(RealFeature feature,
			String featureName, String containingPackage) {
		//TODO: convention here : the pairwise feature generator is always the same as pointwise generator
		//this is so that we can swap them here.
        return new RealFeature(containingPackage, featureName + feature.getIdentifier(), feature.getValue());
	}

	public static Map<String, Double> getFeatureMap(Classifier featureGen,
			DocQueryPair pairYes) {
		HashMap<String, Double> featureMap = new HashMap<String, Double>();
		for (Object o : featureGen.classify(pairYes).features) {
			if (o instanceof DiscreteFeature) {
				DiscreteFeature df = (DiscreteFeature) o;
				featureMap.put(df.toString(), 1.0);
			} else if (o instanceof RealFeature) {
				RealFeature rf = (RealFeature) o;
				featureMap.put(rf.getPackage() + ":" + rf.getIdentifier(), rf.getValue());  //treat duplicated features as 0/1
			} else {
				System.out.println("Unknown feature : " + o);
			}
		}
		return featureMap;
	}
	
}
