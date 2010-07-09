// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D4F41DA44013C0CF598B024B88DF0075F5EEEE561444150F1F82DDCDA1C531D43BEA7A8FFE6BB778A41A46299C466A7AC941D0F41654386C79741DE377CB29E7FBDE87349CCD642DDD48ABFBD84A0DFB978BC378F285BC832623216840755AF1604B2457EB61F02FC8C4F98D75127EB58B7352E10E551FD946AC56387A17C61672A0EA334D865A22B238418E88BE229587A89F60F3CA22F5E2F15A6E2457EE00C75A1F2C1F1C4EA5664D57CC8B80CE0C5D4553B4842C61983BB9D759AA3BF5B47270F45BF91933243D0C5CF93E33866BD49F7E61EBFF7E850797F3A38D20C6A4100000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class BigramWordsInBody_InDict extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public BigramWordsInBody_InDict() { super("lbj.common.BigramWordsInBody_InDict"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'BigramWordsInBody_InDict(DocQueryPair)' defined on line 56 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == BigramWordsInBody_InDict.exampleCache.get()) return (FeatureVector) BigramWordsInBody_InDict.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedBody();
    String previous = null;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (WordDict.contains(w))
      {
        if (previous != null)
        {
          __id = this.name + ("");
          __value = "" + (previous + "_" + w);
          __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
        }
        previous = w;
      }
    }

    BigramWordsInBody_InDict.exampleCache.set(__example);
    BigramWordsInBody_InDict.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'BigramWordsInBody_InDict(DocQueryPair)' defined on line 56 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "BigramWordsInBody_InDict".hashCode(); }
  public boolean equals(Object o) { return o instanceof BigramWordsInBody_InDict; }
}

