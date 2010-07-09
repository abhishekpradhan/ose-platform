// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056E8B4E43413C0547B265524A4604610C76456219E18044526C1213DA5D78306B159F88DB3E4923004998F62727D7A1965143C328BBE2D433FA8C66CCC319A968B8E5F6E505EDEAB0904B7E80767C0F10BCE3FC85D8A3345837FBF4B6437434C13CAAF6199ED1BDEB924C35878E2012B1A413F98CCFF4D4478C3002B4B92A758FA63254F517B624CB68D9B393D1EB36C102F18A9887B8D6862CC3C5D1B11E21F1FE154734F49A676B24C8D23781549CAF4D7782BC2AEA5FFCB2B73CEB9E79A5EFB66A8CA80B850C98FBDF8CC7E83F52E63A806B4100000

package lbj.common;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import common.GenericPair;
import java.util.*;
import lbjse.objectsearch.*;


public class WordsInTitleInLists extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public WordsInTitleInLists() { super("lbj.common.WordsInTitleInLists"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'WordsInTitleInLists(DocQueryPair)' defined on line 70 of common.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == WordsInTitleInLists.exampleCache.get()) return (FeatureVector) WordsInTitleInLists.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    Collection c = dq.getDoc().getTokenizedTitle();
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

    WordsInTitleInLists.exampleCache.set(__example);
    WordsInTitleInLists.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'WordsInTitleInLists(DocQueryPair)' defined on line 70 of common.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "WordsInTitleInLists".hashCode(); }
  public boolean equals(Object o) { return o instanceof WordsInTitleInLists; }
}

