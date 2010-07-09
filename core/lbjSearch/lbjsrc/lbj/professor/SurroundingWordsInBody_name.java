// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D709F4B43013015CFBAC8B02420BD5CB4F26DDB419280E1A2DA5F82BC666B486C91B9F32659E77772BB2BC611CB480973FE7FE56C8D0DA7C887D0B94ED37223696FF2CED4870A52B93DB253F68AEE9BD7A48EF4BE6CA70374D077338F6874B47043B3C632B788C7042BF5862BB0A687E86D58AA56AF04F17BC3D9556E85DE13A0459EC79DE4DAA4BE50C62A79A10D9547667D8B4820C1C4D7D81C6BA155554E695881F2A2578D4C4E13C8D64C439F5EAE7565715DBC94A6903B9790371E672835B6E063A56ACBE9E731FE9F8AFFA0A7C8EC19AE6400DF8E55D049C9B5CF36CCA4548F915BC2830250482A08BD1ED506972590FEC07EF101E029A46BB100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInBody_name extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInBody_name() { super("lbj.professor.SurroundingWordsInBody_name"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInBody_name(DocQueryPair)' defined on line 55 of professor_s4.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInBody_name.exampleCache.get()) return (FeatureVector) SurroundingWordsInBody_name.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("name");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInBody_name.exampleCache.set(__example);
    SurroundingWordsInBody_name.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInBody_name(DocQueryPair)' defined on line 55 of professor_s4.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInBody_name".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInBody_name; }
}

