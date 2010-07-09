// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420BD54F0E5CAB711924144A2DA5F82B423B5243D4AD9422659EF7772BB2BC692879012F6EB7FE564B90A8032E53CB4AD12915BC444E393D6CD6EDD39E0377F8E5F1F36FC21A872FAE5312D171D8120D7090F0318F18763E6B8A7D8A2A7288E7B8ECC73AEC814D0F61D8D0592FEE3192EACF87658E345B1C8C6A246EBCA6C8A0927A0BC84CD30A53865FAB1B909D0B78ABA1D363B145154753B062FCA35B8D4C448168A3C4D8EF93BF395C957F2741B52C4EEA48B56FD69DE4C2C7031D8779714DDA26F9F8AF25093447E84173C60DD8E55D0E295B3DF70CCA45E0FB2A4ED07047101A820EEBF7763BC3928DFE407AF586A0DC7A4C100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumericSurroundingWordsInBody_price extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInBody_price() { super("lbj.laptop.NumericSurroundingWordsInBody_price"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInBody_price(DocQueryPair)' defined on line 25 of laptop_s33.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInBody_price.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInBody_price.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("price");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 1);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInBody_price.exampleCache.set(__example);
    NumericSurroundingWordsInBody_price.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInBody_price(DocQueryPair)' defined on line 25 of laptop_s33.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInBody_price".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInBody_price; }
}

