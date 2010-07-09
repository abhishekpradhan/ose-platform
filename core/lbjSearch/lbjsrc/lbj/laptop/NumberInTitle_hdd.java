// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D4051DE43C03C0CF51B6F49840DF182C3D012D4243D0130E115AD8BB95B01B07C559188F77CD615782F2EBCDD5EE2E845A514548D6FFED0AC62FE9431EBD1364777CDE36F827ED502188F1E1E6EA1EB1E94582F10A32C41F524AE11E6D8CAE08A398D9F17CBF5857B2B7A65EB68537A48DA21768671B854CC68D3F9033D716C92ED99EB16E481230DCD8CC2D58450B68E850CD641528AD44A6C4B51DF1E1D94A5D1349D2E7AE88CF5A3F06257E660E7495E943EB25E70E1056D1AC41D4D18B765A45AA2149A477E537E2A2182BABC830CB2B44738FBAF7B40F36CC5AAAA4F6D4B11C07AA1E7CE8DA8B795E33677FB0A2B0225187100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumberInTitle_hdd extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInTitle_hdd() { super("lbj.laptop.NumberInTitle_hdd"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInTitle_hdd(DocQueryPair)' defined on line 10 of laptop_s36.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("hdd");
    Collection c = dq.getDoc().getTokenizedTitle();
    boolean inTitle = false;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (Utils.satisfyConstraint(new Word(w), fieldValue))
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
        System.err.println("Classifier 'NumberInTitle_hdd(DocQueryPair)' defined on line 10 of laptop_s36.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInTitle_hdd".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInTitle_hdd; }
}

