// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2E81CA63013C044F754C241CE34DF109D6F2DDC561248486127C26C65251954226BAC67B1AFFE5B6B5479911F43A9C4535145C7083949C574E719CBCBF8CB5A4AE6B29E8F98569344A209F6E1E9E11EE038C431625216840FCD6F1E28AD057ED4CB9C5199EB13B5093FD3C95A08B151B44D6A84BD94A04FFED002D0F11BEE1FB4DC9F6F1E55B01F506E68A37F76CB16CB23E38ACE466C234CA8B60C91C95B7BEC1290B642EAE66F615519B2247D1C606EE1E7C6E7105C9FFB133F000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInBody_InDict extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInBody_InDict() { super("lbj.common.WordsInBody_InDict"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInBody_InDict(DocQueryPair)' defined on line 46 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInBody_InDict.exampleCache.get()) return (FeatureVector) WordsInBody_InDict.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedBody();
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (WordDict.contains(w))
      {
        __id = this.name + ("");
        __value = "" + (w);
        __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
      }
    }

    WordsInBody_InDict.exampleCache.set(__example);
    WordsInBody_InDict.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInBody_InDict(DocQueryPair)' defined on line 46 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInBody_InDict".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInBody_InDict; }
}

