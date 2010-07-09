// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D405D5F43C03C0CFB26DE92190AF70A0F24112D4A96201F5F882D6CDDCA588583EAA414CF77CDE060ACB8FCEB3FD5C19A4728A80BD1E5B54957EB2E83DBCE36477DCDDDD0823DD602188F6E1E2EC1E31EE5582FE0A72C41F924A101E2D685DE05712B3F379737A9AB59DAA59FA1A1E490B352EC0DD942661741C30F1033D706C9DDD91DB56E481230D297C40D78450B68E950CDA51528A554A639EA2AF1C3B094BAD782B5C77D919FFB4C3A15D9B320F33D2F2C1F592F6874496241C5614F0EE159259AA405A2DF4D07E2A2182BABC832C33B4473AF3BF7720F3BDC929AAC0614B51C078A1EBCE9D97709FDFA85BEB10C490FACA37100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumberInBody_hdd extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInBody_hdd() { super("lbj.laptop.NumberInBody_hdd"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInBody_hdd(DocQueryPair)' defined on line 25 of laptop_s36.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("hdd");
    Collection c = dq.getDoc().getTokenizedBody();
    boolean inBody = false;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (Utils.satisfyConstraint(new Word(w), fieldValue))
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
        System.err.println("Classifier 'NumberInBody_hdd(DocQueryPair)' defined on line 25 of laptop_s36.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInBody_hdd".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInBody_hdd; }
}

