// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D709F4B43C04015CFBAC81016712D0E5A71B6E222501A845A5BE15246725696A33DEEF11BA4FBBB3B9848411A79569D73FE7FEDE81BEB67810F61E52EE1D9DA751D93E846C2D6FD9D91FFC4F8C6E4F1B762BAE98BE7D88EE4BCACA30374D0F0318F18585AD1A9D06D18D140ED129DF6439C5052CB50BDAF2A66AF44716DC3E9556E85C61380459E4795F8DAA4BE91CA2839A10D85C6DC6AA632A00B735753A7BDC7055569A56626CB8A4D06512A34F34B113D8E793FF395D545FC749A93C46A93C458BD80E4D29DBD06992DA7ABD4C12D15EF714F01D9225DD900AB1DB921826BDECEA81392501E750D2B06F84E112BC0EEBF77185A9452CB33C9F71BF1502E42C100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumericSurroundingWordsInBody_moni extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInBody_moni() { super("lbj.laptop.NumericSurroundingWordsInBody_moni"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInBody_moni(DocQueryPair)' defined on line 25 of laptop_s32.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInBody_moni.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInBody_moni.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("moni");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInBody_moni.exampleCache.set(__example);
    NumericSurroundingWordsInBody_moni.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInBody_moni(DocQueryPair)' defined on line 25 of laptop_s32.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInBody_moni".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInBody_moni; }
}

