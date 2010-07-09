// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005705BCA4401301CF59EB80310D08B74F17395958561F18E552EC45CD691291A3D38A8CEFBBD17648141F2948AEAA4555B024F744957F097A880A3A0A437D8401E6F620B0D0383AB831AF4ABACDEDE809F8905B3E49E29A952AEC8CEF50AB6692AD8B3FFB295DF729B74B9357553B5E2A4D17C8BE93D894E149BF2EB2801425E0A8F996390F64DF3FBAF1427449295CFFA23EE87A4B3D5B23B88958A9D82428ADB85DC16968E97E15D83CAE7F1ACE0FEFD58C9DE1EEC636335926DE3B00C55E7A99C509A815A6DBD475367468DF9476316F81AF1159770A3C71E8E2772168100000

package lbj.professor.pairwise;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class other_features extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public other_features() { super("lbj.professor.pairwise.other_features"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "real%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof GenericPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'other_features(GenericPair)' defined on line 11 of pairwise_professor_s210.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == other_features.exampleCache.get()) return (FeatureVector) other_features.cache.get();

    GenericPair pp = (GenericPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;

    DocQueryPair pair1 = (DocQueryPair) pp.getFirst();
    DocQueryPair pair2 = (DocQueryPair) pp.getSecond();
    List diffFeatures = Utils.differentiateFeatures(new lbj.professor.other_features(), pair1, pair2);
    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
    {
      RealFeature f = (RealFeature) it.next();
      __id = this.name + (f.getIdentifier());
      __result.addFeature(new RealFeature(this.containingPackage, __id, f.getValue()));
    }

    other_features.exampleCache.set(__example);
    other_features.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'other_features(GenericPair)' defined on line 11 of pairwise_professor_s210.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "other_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof other_features; }
}

