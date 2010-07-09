// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420BD5CB4F26DDB419280E1A2DA5F82BE666B486898D9422659EF7772BB2BC611CB480973FEB7F236C6869132E53C6213BF446C2DEF5C3B90F04B4F6E4FAF6CD09157FEBD7A48C7A573691CC153CDDC0EB1E1D2D10DCE0BD8E912AF3029DF2439D6053C374BE245DA7AF04E8B5F3D9556E85DE13A0459EC79DE4DAA4BE50C622B4F08EC2A33BB6C52410E06AEB6C06B5D8AAA2AFA95883F2A3578D4C4C816CA32EA9CF375FB2BAB8EE5E426B489DCB489B07B31C9A5BF063A5F49714DFA2ED3F15FF514F81D9325DD800AF1DBAA182937B8F7C8959A80F33A69D0704A08054107B3CBB0C2F4A21ED91ECF3D82B7164DB100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInBody_brand extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInBody_brand() { super("lbj.laptop.SurroundingWordsInBody_brand"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInBody_brand(DocQueryPair)' defined on line 55 of laptop_s31.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInBody_brand.exampleCache.get()) return (FeatureVector) SurroundingWordsInBody_brand.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("brand");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInBody_brand.exampleCache.set(__example);
    SurroundingWordsInBody_brand.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInBody_brand(DocQueryPair)' defined on line 55 of laptop_s31.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInBody_brand".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInBody_brand; }
}

