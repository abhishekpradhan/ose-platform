// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005705D5B4301301CFB2BF22420A186F1DF8739A41A88F18EBA4CBBD8DD27427CEEE9DA84FFBBB1FE48311F52903B33999956C8DD90C099EDF52164D1815CDD06646A6EE22134FDB78BC338F48BE2DCDF08CF132A67C20EA0CDC15F64E0F68AB226157EF2EFA469FF94E11B929BDAA9D09824B492DA6A43629725AE424501913B2545CF99ABC8B78EE5771A7E29054A078FDD5CF9E8167CB696E09A038B5B22745B719A91CCDF20D43AA96843C63ACD2E1EBBF87B53C38DEB62A24AA576608FACFC319503B815A6BB57B5357224BF31EC74CE93673065ED11E8F5029992CD948100000

package lbj.professor.pairwise;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class univ_features extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public univ_features() { super("lbj.professor.pairwise.univ_features"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "real%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof GenericPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'univ_features(GenericPair)' defined on line 11 of pairwise_professor_s3.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == univ_features.exampleCache.get()) return (FeatureVector) univ_features.cache.get();

    GenericPair pp = (GenericPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;

    DocQueryPair pair1 = (DocQueryPair) pp.getFirst();
    DocQueryPair pair2 = (DocQueryPair) pp.getSecond();
    List diffFeatures = Utils.differentiateFeatures(new lbj.professor.univ_features(), pair1, pair2);
    for (Iterator it = diffFeatures.iterator(); it.hasNext(); )
    {
      RealFeature f = (RealFeature) it.next();
      __id = this.name + (f.getIdentifier());
      __result.addFeature(new RealFeature(this.containingPackage, __id, f.getValue()));
    }

    univ_features.exampleCache.set(__example);
    univ_features.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'univ_features(GenericPair)' defined on line 11 of pairwise_professor_s3.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "univ_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof univ_features; }
}

