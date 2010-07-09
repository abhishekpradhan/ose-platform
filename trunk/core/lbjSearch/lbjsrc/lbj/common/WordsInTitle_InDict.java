// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2E8DCA02013C048F552C280DE1CE308BA72DBC28882E287492DD8A1C521C632BEF0EBBBDEA29BCC48F2339E825888A8318384CE253CD296F87C6875414DCA42CEEE81F9B5F411ABB9589F41ED0B49EB7C0A42C0106197FEEC8A91536B8865EA8C4F2CE6C4236B68394403D8264FA95196EB90E8EFEB004AEE2E3D60F1A5C9DC51B7D84C768123A6CCFC8D281F8C857A2B69103E2D7A1B18E406ACFF5E9671485D3172338D2159093124551CC068A1E356EB322D7A185F000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInTitle_InDict extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInTitle_InDict() { super("lbj.common.WordsInTitle_InDict"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInTitle_InDict(DocQueryPair)' defined on line 28 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInTitle_InDict.exampleCache.get()) return (FeatureVector) WordsInTitle_InDict.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedTitle();
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

    WordsInTitle_InDict.exampleCache.set(__example);
    WordsInTitle_InDict.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInTitle_InDict(DocQueryPair)' defined on line 28 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInTitle_InDict".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInTitle_InDict; }
}

