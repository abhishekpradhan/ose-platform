// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005705BCA4401301CF59EB80310D08B74F17395958561F18E552EC456D691291A3D38A8CEFBBD17648111F2948AEAA4555B024F744D160D7A880A3A0A437D8401E6F620B0D0383AB831AF4ABACDEDE809F8905B3E49E29A952AEC8CE7F0D53B41D6CD9FF59CAEF39CD3ADC9ABAA9D2715AE836C5FC96C42F0ACD71F51480292705CFC4B94873AEF9F5DF02932A49C2EF777177C358D9EA59934CC24DC641214DE5CA660B4F3FC3F8A6A165FF21ACE0FEFDD7C9DA1EEC6F5335926DA3B00C55E7A99C509A815A6BBD475357468DF9476316F81AF1159770A3C7100AE6F64048100000

package lbj.professor.pairwise;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class dept_features extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public dept_features() { super("lbj.professor.pairwise.dept_features"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "real%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof GenericPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'dept_features(GenericPair)' defined on line 11 of pairwise_professor_s2.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == dept_features.exampleCache.get()) return (FeatureVector) dept_features.cache.get();

    GenericPair pp = (GenericPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;

    DocQueryPair pair1 = (DocQueryPair) pp.getFirst();
    DocQueryPair pair2 = (DocQueryPair) pp.getSecond();
    List diffFeatures = Utils.differentiateFeatures(new lbj.professor.dept_features(), pair1, pair2);
    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
    {
      RealFeature f = (RealFeature) it.next();
      __id = this.name + (f.getIdentifier());
      __result.addFeature(new RealFeature(this.containingPackage, __id, f.getValue()));
    }

    dept_features.exampleCache.set(__example);
    dept_features.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'dept_features(GenericPair)' defined on line 11 of pairwise_professor_s2.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "dept_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof dept_features; }
}

