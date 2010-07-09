// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294DAE4B4CC92E45D158292A2D4DA550FF2A4C4EC945D079CF4E0C2D4D2AAC084CCC22584924D450B1D558A6500A2F2D2AC302F5F2F12AC43DA51A6103D04B46784000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class Oracle extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public Oracle() { super("lbj.common.Oracle"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String[] allowableValues()
  {
    return DiscreteFeature.BooleanValues;
  }

  private String _discreteValue(DocQueryPair dq)
  {
    return "" + (dq.oracle());
  }

  public String discreteValue(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'Oracle(DocQueryPair)' defined on line 9 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    String result = _discreteValue((DocQueryPair) example);

    if (valueIndexOf(result) == -1)
    {
      System.err.println("Classifier 'Oracle' defined on line 8 of common.lbj produced '" + result + "' as a feature value, which is not allowable.");
      System.exit(1);
    }

    return result;
  }

  public FeatureVector classify(Object example)
  {
    if (example == exampleCache.get()) return (FeatureVector) cache.get();
    String value = discreteValue(example);
    cache.set(new FeatureVector(new DiscreteFeature(containingPackage, name, value, valueIndexOf(value), (short) 2)));
    exampleCache.set(example);
    return (FeatureVector) cache.get();
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'Oracle(DocQueryPair)' defined on line 9 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "Oracle".hashCode(); }
  public boolean equals(Object o) { return o instanceof Oracle; }
}

