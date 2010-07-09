// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420BD5CB4F26DDB419280E1A2DA5F82BC666B486898D4625CA2DFFEE467569D22879012F6ED7FE56C8D8D6046CB58D4A01C7223696FF6E3898F44B4F6ECFE98CE94D3AF6F521683FAB1B10CC153C3CC0EB1E9D2D10DCE0B56F108DF109CE71A9CE28A1E59DAB855BE9E48187BE7A3BACC1BAD32B0459EC79DE4DAA4BE50C68384D08EC2A33BB6C52410E06AEB6C06B5D8AAA227BC24C87559A3C68350C836B113D4E79BAF595D555F2729A52CC6E52CC58BD90E4DAD74BC6D35E5F4FB98F8C74DF750D3647E84577208E74F6A60A4ECD2EF13665A22CF46D2B0E884111A820EE7877185E9452CBB0C5E700A340A1D4BB100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInBody_univ extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInBody_univ() { super("lbj.professor.SurroundingWordsInBody_univ"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInBody_univ(DocQueryPair)' defined on line 55 of professor_s3.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInBody_univ.exampleCache.get()) return (FeatureVector) SurroundingWordsInBody_univ.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("univ");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInBody_univ.exampleCache.set(__example);
    SurroundingWordsInBody_univ.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInBody_univ(DocQueryPair)' defined on line 55 of professor_s3.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInBody_univ".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInBody_univ; }
}

