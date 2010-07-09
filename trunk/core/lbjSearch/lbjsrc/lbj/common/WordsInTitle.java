// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2D8DCA02C030148F55692809C1CE308F37AA792888261C37846DAB852B8B951FF8EBBB945F6333C733318297145C91C98524A63674A30A9D0BF3CD15E5B774201E66165378F043C3C08E5983287857ECBEE1533A6C611D1F5132DB13CF62CE21ECC206A55417A95196EE8FA9EFEB004A5F5C5AD1E35B83B9F2216C4805550B0036E8A241B7B50B83136B65ED2F305A1796A78116CF2F76AA9262C000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInTitle extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInTitle() { super("lbj.common.WordsInTitle"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInTitle(DocQueryPair)' defined on line 19 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInTitle.exampleCache.get()) return (FeatureVector) WordsInTitle.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedTitle();
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      __id = this.name + ("");
      __value = "" + (((String) it.next()).toLowerCase());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    WordsInTitle.exampleCache.set(__example);
    WordsInTitle.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInTitle(DocQueryPair)' defined on line 19 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInTitle".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInTitle; }
}

