// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D415558C849498F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D550FB2DCD4D2ACC4E0E2D2A2ACF2DCB49CCCB4F0FCF2A4926FCCB09CC29C94D870AE7D1CDA6C92F35A21EA42925B80D57185801A8000F7263C0C49000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class hdd_features extends Classifier
{
  private static final NumericSurroundingWordsInTitle_hdd __NumericSurroundingWordsInTitle_hdd = new NumericSurroundingWordsInTitle_hdd();
  private static final NumericSurroundingWordsInBody_hdd __NumericSurroundingWordsInBody_hdd = new NumericSurroundingWordsInBody_hdd();
  private static final NumberInTitle_hdd __NumberInTitle_hdd = new NumberInTitle_hdd();
  private static final NumberInBody_hdd __NumberInBody_hdd = new NumberInBody_hdd();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public hdd_features() { super("lbj.laptop.hdd_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'hdd_features(DocQueryPair)' defined on line 59 of laptop_s36.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__NumericSurroundingWordsInTitle_hdd.classify(example));
    result.addFeatures(__NumericSurroundingWordsInBody_hdd.classify(example));
    result.addFeatures(__NumberInTitle_hdd.classify(example));
    result.addFeatures(__NumberInBody_hdd.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'hdd_features(DocQueryPair)' defined on line 59 of laptop_s36.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "hdd_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof hdd_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__NumericSurroundingWordsInTitle_hdd);
    result.add(__NumericSurroundingWordsInBody_hdd);
    result.add(__NumberInTitle_hdd);
    result.add(__NumberInBody_hdd);
    return result;
  }
}

