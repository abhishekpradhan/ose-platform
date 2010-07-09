// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D415558282ACF4E8F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D550FB2DCD4D2ACC4E0E2D2A2ACF2DCB49CCCB4F0FCF2A4926FCCB09CC29C94D870910A38B51935E7A45224D425A61168E30B81292300DA241AC499000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;
import ose.parser.RangeFunctionHandler;
import ose.processor.cascader.RangeConstraint;


public class proc_features extends Classifier
{
  private static final NumericSurroundingWordsInTitle_proc __NumericSurroundingWordsInTitle_proc = new NumericSurroundingWordsInTitle_proc();
  private static final NumericSurroundingWordsInBody_proc __NumericSurroundingWordsInBody_proc = new NumericSurroundingWordsInBody_proc();
  private static final NumberInTitle_proc __NumberInTitle_proc = new NumberInTitle_proc();
  private static final NumberInBody_proc __NumberInBody_proc = new NumberInBody_proc();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public proc_features() { super("lbj.laptop.proc_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'proc_features(DocQueryPair)' defined on line 71 of laptop_s37.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__NumericSurroundingWordsInTitle_proc.classify(example));
    result.addFeatures(__NumericSurroundingWordsInBody_proc.classify(example));
    result.addFeatures(__NumberInTitle_proc.classify(example));
    result.addFeatures(__NumberInBody_proc.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'proc_features(DocQueryPair)' defined on line 71 of laptop_s37.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "proc_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof proc_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__NumericSurroundingWordsInTitle_proc);
    result.add(__NumericSurroundingWordsInBody_proc);
    result.add(__NumberInTitle_proc);
    result.add(__NumberInBody_proc);
    return result;
  }
}

