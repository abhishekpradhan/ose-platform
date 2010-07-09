// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B058406BB879E5CAB711928022596DA74956766B68E631B3944CA2DFFEE467569D224F2124EDCBFEDBC0A1F53350A90C35CD31B9A75199D54B86CE6F5D13AF70B77E0F8F6FE88AEED5DFC198F8BCAC030E143CDE41E70E1D8DD11E68AE0E8128BD1953FD489C4052CB403DAF2A676F3983CADD87651E1A8D25018A2D9E2BE1B5596D37855069610D81A617355B19408DB9ABA1DBD6138AA23929998FECA15345588C4E78232E91DF171F72BA3B6E9F8243789EC2789907B11C9A5A3F6281763D27AB54C74A3ACFF28E12A354AAB610473A75528D8D6B3FB06C4A4169EB2869DFA72BE902BC0E6AF77185A9452CB31C9E710D04298B10C100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumericSurroundingWordsInBody_hdd extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInBody_hdd() { super("lbj.laptop.NumericSurroundingWordsInBody_hdd"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInBody_hdd(DocQueryPair)' defined on line 50 of laptop_s36.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInBody_hdd.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInBody_hdd.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("hdd");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInBody_hdd.exampleCache.set(__example);
    NumericSurroundingWordsInBody_hdd.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInBody_hdd(DocQueryPair)' defined on line 50 of laptop_s36.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInBody_hdd".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInBody_hdd; }
}

