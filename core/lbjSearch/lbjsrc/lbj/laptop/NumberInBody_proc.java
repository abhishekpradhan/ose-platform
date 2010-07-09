// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005405B4F43C03C0EFB26DE49C18EF182C582A9894862E52EA8CD4DD21D2423C1755A02EFB3E470D9271F3EB793D9FCE894806F3EB7B4CBB87D9AB9F5F8C9C99B94EE1642E9FE1D3347F161E2F20EB1E948D7C10A7F41AB71C0321C59E2BA184610B1B5ACDEAB53B92A5B1B53439240272E35407B274D3E4C87E470A8EFB8AB2EF641EF88170A625CC2C8E3A00712D23CDE817199B5C8D502EAE88C99CC930929BD49201640FBC93A4CE1346AA1AF4C066724C82A5971DD8BACFF5F5C5DB45F6897F4F925A3B7EB77258A137A6C6185C5036B294779622E605334118E1C0BBA23A8FCDFC662B5436D812C3A6A86990F053CF8E3DFAF19FF37AE8E710092F883EF8100000

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


public class NumberInBody_proc extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInBody_proc() { super("lbj.laptop.NumberInBody_proc"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInBody_proc(DocQueryPair)' defined on line 29 of laptop_s37.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("proc");
    Collection c = dq.getDoc().getTokenizedBody();
    RangeConstraint rc = RangeFunctionHandler.parse(fieldValue);
    boolean inBody = false;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (rc.satisfy(w))
      {
        inBody = true;
        break;
      }
    }
    return "" + (inBody);
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
        System.err.println("Classifier 'NumberInBody_proc(DocQueryPair)' defined on line 29 of laptop_s37.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInBody_proc".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInBody_proc; }
}

