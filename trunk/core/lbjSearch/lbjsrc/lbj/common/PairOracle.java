// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294DAE4B4CC92E45D158292A2D4DA558084CCC22FF2A4C4EC945D07F4DCB4D2ACC460988241009826D450B1D558A6500A6A2D2AC3580D29CCC926DBC788A588CB5B24D200755C860F65000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class PairOracle extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public PairOracle() { super("lbj.common.PairOracle"); }

  public String getInputType() { return "common.GenericPair"; }
  public String getOutputType() { return "discrete"; }

  public String[] allowableValues()
  {
    return DiscreteFeature.BooleanValues;
  }

  private String _discreteValue(GenericPair pairs)
  {
    return "" + (Utils.oracle(pairs));
  }

  public String discreteValue(Object example)
  {
    if (!(example instanceof GenericPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'PairOracle(GenericPair)' defined on line 14 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    String result = _discreteValue((GenericPair) example);

    if (valueIndexOf(result) == -1)
    {
      System.err.println("Classifier 'PairOracle' defined on line 13 of common.lbj produced '" + result + "' as a feature value, which is not allowable.");
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
      if (!(examples[i] instanceof GenericPair))
      {
        System.err.println("Classifier 'PairOracle(GenericPair)' defined on line 14 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "PairOracle".hashCode(); }
  public boolean equals(Object o) { return o instanceof PairOracle; }
}

