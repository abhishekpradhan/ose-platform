// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005705BCA4401301CF59EB8090860CD3AF8B9CAC2888F04FA2176A2AD236668E4F0ABC2BFFE67C911644CB421AABA2555D288DD105E8FE87A488A3A0A8BB4468073731958681C3D9D11DE8E2AF6E6748C6724DE836A3727B44DB193CB047DC254D9F3DFB295DF729B743D7E6BA6EA8B825BC92DA7E43629705EEA48A02409593A2E76AE23E38AB7E7B0384F905A4F21E7771F78358D9EA59934AE58CD641215DE5CA660B4FB0C3F8A6A16D0FA1BC53E3FBBF87B53CD9DEB66A25AA576108FACFC339B02713A4D67B96B6AE4C0BF39E462CE136732A2F6F4BFF20C08A8BBD48100000

package lbj.professor.pairwise;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class name_features extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public name_features() { super("lbj.professor.pairwise.name_features"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "real%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof GenericPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'name_features(GenericPair)' defined on line 11 of pairwise_professor_s240.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == name_features.exampleCache.get()) return (FeatureVector) name_features.cache.get();

    GenericPair pp = (GenericPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;

    DocQueryPair pair1 = (DocQueryPair) pp.getFirst();
    DocQueryPair pair2 = (DocQueryPair) pp.getSecond();
    List diffFeatures = Utils.differentiateFeatures(new lbj.professor.name_features(), pair1, pair2);
    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
    {
      RealFeature f = (RealFeature) it.next();
      __id = this.name + (f.getIdentifier());
      __result.addFeature(new RealFeature(this.containingPackage, __id, f.getValue()));
    }

    name_features.exampleCache.set(__example);
    name_features.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'name_features(GenericPair)' defined on line 11 of pairwise_professor_s240.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "name_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof name_features; }
}

