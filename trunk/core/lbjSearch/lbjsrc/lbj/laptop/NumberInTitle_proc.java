// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005405DCE43C03C0E751B67A4E04F50607A2A9894862E762EA8CD4DD26D2B4683EAAC04CBB394B8A55E26BFBF1F7E4BC9C90921C16837342BF07465F4FE7198ECCD747FC309C5F90950ADF4B07B730F30FAA2C1A78E89C7BF68E7028BBC065D39E44636B49BB5053B92E5B1BB58A3A7F4E4936007B862FE89517C872A0CFD4DE410C46EFB068E9AE8129A02705092AA968BB12C4E3F081A5F425D50521995315617313A72C00C3F14959D1AF44B58E2A089DB290A6EA85332EA2EFFEBCA56DAE303D18EB4B4767DB87CC45366E6C61A589836B2D8F817429A13D49B9B3032EAA48AC9ABBA91D61F85378AC0936432487AD2CF6E79FBF7095296E9DF1D69C8D6749100000

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


public class NumberInTitle_proc extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInTitle_proc() { super("lbj.laptop.NumberInTitle_proc"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInTitle_proc(DocQueryPair)' defined on line 13 of laptop_s37.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("proc");
    Collection c = dq.getDoc().getTokenizedTitle();
    RangeConstraint rc = RangeFunctionHandler.parse(fieldValue);
    boolean inTitle = false;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (rc.satisfy(w))
      {
        inTitle = true;
        break;
      }
    }
    return "" + (inTitle);
  }

  public FeatureVector classify(Object example)
  {
    if (example == exampleCache.get()) return (FeatureVector) cache.get();
    cache.set(new FeatureVector(new DiscreteFeature(containingPackage, name, discreteValue(example))));
    exampleCache.set(example);
    return (FeatureVector) cache.get();
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumberInTitle_proc(DocQueryPair)' defined on line 13 of laptop_s37.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInTitle_proc".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInTitle_proc; }
}

