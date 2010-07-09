// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D4155584A2A4CCB498F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D550FCCB09CC29C94D870B25D1027D92F35A216CB0E2D2A2ACF2DCB49CCCB4F0FCF2A492645950066D93BA356000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class brand_features extends Classifier
{
  private static final InTitle_brand __InTitle_brand = new InTitle_brand();
  private static final InBody_brand __InBody_brand = new InBody_brand();
  private static final SurroundingWordsInBody_brand __SurroundingWordsInBody_brand = new SurroundingWordsInBody_brand();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public brand_features() { super("lbj.laptop.brand_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'brand_features(DocQueryPair)' defined on line 64 of laptop_s31.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__InTitle_brand.classify(example));
    result.addFeatures(__InBody_brand.classify(example));
    result.addFeatures(__SurroundingWordsInBody_brand.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'brand_features(DocQueryPair)' defined on line 64 of laptop_s31.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "brand_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof brand_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__InTitle_brand);
    result.add(__InBody_brand);
    result.add(__SurroundingWordsInBody_brand);
    return result;
  }
}

