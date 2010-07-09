// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2AC1CA0040401000DF59DB82A8F30729BCE544493A0334356762367A8FB7927D7DB0473242E2803D4B1EC1D41FA4B695B334D7AD994D1C999B2B07D858505D3F041F0C9EBF9F768A4B6F6AA26C04CBF82A0797EA40E9FFCB07A299E5F16000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class moni_features extends Classifier
{
  private static final NumberInTitle_moni __NumberInTitle_moni = new NumberInTitle_moni();
  private static final NumericSurroundingWordsInBody_moni __NumericSurroundingWordsInBody_moni = new NumericSurroundingWordsInBody_moni();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public moni_features() { super("lbj.laptop.moni_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'moni_features(DocQueryPair)' defined on line 34 of laptop_s32.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__NumberInTitle_moni.classify(example));
    result.addFeatures(__NumericSurroundingWordsInBody_moni.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'moni_features(DocQueryPair)' defined on line 34 of laptop_s32.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "moni_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof moni_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__NumberInTitle_moni);
    result.add(__NumericSurroundingWordsInBody_moni);
    return result;
  }
}

