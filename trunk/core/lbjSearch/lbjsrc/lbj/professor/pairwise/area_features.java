// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005705BCA4401301CF59EB80310D08B74F17395958561F18E552EC456D691291A3D38A8CEFBBD17648111F2948AEAA4555B024F7441401E92228E8282DC532148BDB90C243C0E8E2E48E39EA27B7B324E3624DE835AB4A6698A332BFD347DC254B177EF752BAFF427F8637AEAA66BC549A3E817D37A139C3827F5C7501284AC141F33D621ED8AF7E75F384E88252B8FFDD5CD1F4167AB656E013B053B158405B71BA91C2DFC3FC3AA9685DFB482B3CBF77F176B68B3BD7DC45A85BEC200759F9662714A2649ADE63D5D4D1916F72D9D48D368E7445ED18E0F5F5D1C06948100000

package lbj.professor.pairwise;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class area_features extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public area_features() { super("lbj.professor.pairwise.area_features"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "real%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof GenericPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'area_features(GenericPair)' defined on line 11 of pairwise_professor_s250.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == area_features.exampleCache.get()) return (FeatureVector) area_features.cache.get();

    GenericPair pp = (GenericPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;

    DocQueryPair pair1 = (DocQueryPair) pp.getFirst();
    DocQueryPair pair2 = (DocQueryPair) pp.getSecond();
    List diffFeatures = Utils.differentiateFeatures(new lbj.professor.area_features(), pair1, pair2);
    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
    {
      RealFeature f = (RealFeature) it.next();
      __id = this.name + (f.getIdentifier());
      __result.addFeature(new RealFeature(this.containingPackage, __id, f.getValue()));
    }

    area_features.exampleCache.set(__example);
    area_features.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'area_features(GenericPair)' defined on line 11 of pairwise_professor_s250.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "area_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof area_features; }
}

