// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420BD5CB4F26DDB419280E1A2DA5F82BC666B486894B9C44CA2DFFEE467569D22879012F6ED7FE56C8D8D6046CB58D4A01C7223696FF6E3898F44B4F6ECFE60F8CAE1D7BF2903C97DD8D006E4A1E1660FD0FC69E086678D2BF00CEF0846FB0D46714D0FAC6D5CAA5F4F18187BE7A3BACC9AAD32B0459EC79DE4DAA4BE50C68384D08EC2A33BB6C52410E06AEB6C06B5D8AAA227BC24C87559A3C68350C836B113D4E79BAF595D555F2729A52CC6E52CC58BD90E4DAD74BC6D35E5F4FB9836E3AEFB28E13A374AAB3104F3A73530527E61FF813B2511E72B6950744A88054107F3CBB0C2F4A21ED50E2F39BC472D7BB100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInBody_dept extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInBody_dept() { super("lbj.professor.SurroundingWordsInBody_dept"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInBody_dept(DocQueryPair)' defined on line 55 of professor_s2.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInBody_dept.exampleCache.get()) return (FeatureVector) SurroundingWordsInBody_dept.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("dept");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInBody_dept.exampleCache.set(__example);
    SurroundingWordsInBody_dept.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInBody_dept(DocQueryPair)' defined on line 55 of professor_s2.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInBody_dept".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInBody_dept; }
}

