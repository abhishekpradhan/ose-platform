// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D415558CF29C84D2A8F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D5580FCF2A4926FCCB09CC29C94D87FCC379CC4E21D1890A35E7A452C5C00B2127FFC35000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class other_features extends Classifier
{
  private static final WordsInTitle_InDict __WordsInTitle_InDict = new WordsInTitle_InDict();
  private static final WordsInBody_InDict __WordsInBody_InDict = new WordsInBody_InDict();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public other_features() { super("lbj.laptop.other_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'other_features(DocQueryPair)' defined on line 7 of laptop_s35.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__WordsInTitle_InDict.classify(example));
    result.addFeatures(__WordsInBody_InDict.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'other_features(DocQueryPair)' defined on line 7 of laptop_s35.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "other_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof other_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__WordsInTitle_InDict);
    result.add(__WordsInBody_InDict);
    return result;
  }
}

