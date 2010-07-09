// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056E8DCA44413C064F552C080DE2CE308F3B17CD41EA2A828BEAD6C99037D44BD8C83A8FEE62A3E245AB9C7DE9E793D864DE8A8700772DBD8CC762D679972A1A32C9B4DBE71CEBBBA24D1AD3748393487785ACC336552168A07A67F9658A68688E3CDAC6099ED0B9715887C0F02D12465CE54D6225BF2531DE3B304A96D56C52EBAA7A86B1E64B31FA06B6646478FE81D14ED35311F60B9B52CCEAA68978B0C7CB7CE36D4F49AA0B6126C69938715259946B8D79560688FFCB2B53C753DF25BCF7DC602F0485C20E8CEDE746E3CFC72039589CE94100000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInBodyInLists extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInBodyInLists() { super("lbj.common.WordsInBodyInLists"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInBodyInLists(DocQueryPair)' defined on line 84 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInBodyInLists.exampleCache.get()) return (FeatureVector) WordsInBodyInLists.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedBody();
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String word = (String) it.next();
      LinkedList lists = ListMembership.containedIn(word.toLowerCase());
      for (Iterator I = lists.iterator(); I.hasNext(); )
      {
        __id = this.name + ("");
        __value = "" + (I.next());
        __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
      }
    }

    WordsInBodyInLists.exampleCache.set(__example);
    WordsInBodyInLists.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInBodyInLists(DocQueryPair)' defined on line 84 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInBodyInLists".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInBodyInLists; }
}

