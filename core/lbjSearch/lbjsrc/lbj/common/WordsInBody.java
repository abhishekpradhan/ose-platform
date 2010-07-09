// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2D8DCA02C030148F55692809C1CE308F371BE5A022A8287E09CA571B46173B2A55AFEE625CBDCC0FDCC40A4E5051760716909AD8B1E0389D2BF3E315683832180F0B0BA93C71A1EEB74FA4C11C3CA37E5778A91536B8833FD132D703C4B06790756103DA2A83DCA84375C7D4F7F5002DAF6E2DE1FDA5C9DCF09036248AA28508137251A8D9D285C981B5B2FE8F582D8B485A232C8F39AFB65C40C000000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInBody extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInBody() { super("lbj.common.WordsInBody"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInBody(DocQueryPair)' defined on line 39 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInBody.exampleCache.get()) return (FeatureVector) WordsInBody.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedBody();
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      __id = this.name + ("");
      __value = "" + (((String) it.next()).toLowerCase());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    WordsInBody.exampleCache.set(__example);
    WordsInBody.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInBody(DocQueryPair)' defined on line 39 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInBody".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInBody; }
}

