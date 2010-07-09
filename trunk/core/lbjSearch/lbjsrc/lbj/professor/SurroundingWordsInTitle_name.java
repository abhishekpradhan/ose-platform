// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420DE224F66DDB884140F059EA5F82BC666B486C98D4622659EF7772BBABC611CB480973FEB7F236C6C63023E52C62580E3191B4BB76F1C4CB7AAD2B3C71A6E515DD9F6F131683EAB1B10CC143CDCC1EB0E1C2DE1DC61B56F108DFE19CE72A9B5F6E8051C31B571BC6D3DB360EADF476599349B34618A2D9F25FFA5BF445A5F2163C14A804761D99D63E21A017075FD360FDA6455519B66126C3BE4D163C9206C1BE88962FFC5DF8CAECABFC629A3389F2660B01E672835B6F1D2B5F49714DFA2ED2F15DF514F81D9325D5900AF1DB8A082937BCF7C8959290F385BC6832254482A08BE1ED506972590FE407AF601A8D8335DB100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInTitle_name extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInTitle_name() { super("lbj.professor.SurroundingWordsInTitle_name"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInTitle_name(DocQueryPair)' defined on line 65 of professor_s4.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInTitle_name.exampleCache.get()) return (FeatureVector) SurroundingWordsInTitle_name.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedTitle());
    String fieldValue = dq.getQuery().getFieldValue("name");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -3, 3);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInTitle_name.exampleCache.set(__example);
    SurroundingWordsInTitle_name.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInTitle_name(DocQueryPair)' defined on line 65 of professor_s4.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInTitle_name".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInTitle_name; }
}

