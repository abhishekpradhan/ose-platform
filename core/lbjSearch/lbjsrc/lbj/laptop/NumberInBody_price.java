// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D405BCE44C03C0CF51B6F49840DF182C58224A590DA04CBE882D4CDD5B634C0E8BA2501FFE42D58505E2E1FCC8D3170ACE50511633EB4F82BE471C16E7E7512F86E29DFDE8823FD832180F661ECE41E31EE4582D61602C81E1D5C111ECB09DC615711B1B5BCBA32B665BC0B59D61A3E811DB21720F743595270BCD3FE131D7068A90C4197FCC11D520A523513C0E266C61606103B65417A5A22D2C8F68E707532963B379738FEA519DFBCC3519A137006BAC2D2A1BD82F53F482D9BC8BC0810CC3825CCD467A49789B3E4955C152539072872690662B72FFE806BEA93625591B40DE50DDEB58FA2FAC98749FDFA496D7302CA1173477100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumberInBody_price extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInBody_price() { super("lbj.laptop.NumberInBody_price"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInBody_price(DocQueryPair)' defined on line 10 of laptop_s33.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("price");
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
        System.err.println("Classifier 'NumberInBody_price(DocQueryPair)' defined on line 10 of laptop_s33.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInBody_price".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInBody_price; }
}

