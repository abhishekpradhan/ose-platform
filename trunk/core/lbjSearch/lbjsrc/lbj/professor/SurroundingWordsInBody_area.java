// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D70914B430130158FFAC8B02420BD54F0DB857F254A02878A4B6D3AC2B99D21A16267298855AFFDD9CEAC2B540F2124EDCBFEDBC81B1A56C887D0B94CCE3191B4BF75FC62C32D2DB93DB53C8D8A70FDE3724E3DAB1BC06E8A1EE760FD0F469E086678D64FC01DF109CE71A9CE28A1E52A571AA6D3D7027CDAF9ECA237CA6F8150AA47EBC67A655A5F2063195A604761D99D53E21A007035F5360BDA6455519B56126CB8A4D1631313681BD88962FBC5DFACAE2AA79394D21667B3F216E20EE487A6DE38D86D35EDF4FBA87FC74DF750D3667EC4573208E74FAA60A4ECD2EF13665A22CFC8A563C1092024150CDD0FE20BC39A4877683FF00098F43B6CB100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class SurroundingWordsInBody_area extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public SurroundingWordsInBody_area() { super("lbj.professor.SurroundingWordsInBody_area"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'SurroundingWordsInBody_area(DocQueryPair)' defined on line 32 of professor_s5.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == SurroundingWordsInBody_area.exampleCache.get()) return (FeatureVector) SurroundingWordsInBody_area.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("area");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, -15, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    SurroundingWordsInBody_area.exampleCache.set(__example);
    SurroundingWordsInBody_area.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'SurroundingWordsInBody_area(DocQueryPair)' defined on line 32 of professor_s5.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "SurroundingWordsInBody_area".hashCode(); }
  public boolean equals(Object o) { return o instanceof SurroundingWordsInBody_area; }
}

