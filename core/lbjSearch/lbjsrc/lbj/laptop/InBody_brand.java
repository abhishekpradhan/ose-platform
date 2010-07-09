// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000054151CA4301301DF591A7A48260FEB6F2654828882A8796B84677767B3434263995B651FFDD946BD59C5233FEDCB97F29692531191165EF634B7A7FA3A5FDAABB0DC3F081F4F469224B70D073750F30F2C19C7F0D11AB6FDCAB1016120A9E19B0959EC7DBF90553B22633D51C238378D0350F0DC4342B46C197D0B74F4FD8D6670A48E5780E0DA70A2E9460A3BE21657630BED2C766DF491896B994F1E885DC63B943FCB29C330903EAB28E24405B26C8695E64C2DD6C0D9BECBF88DCEC6A74CF2EC59EFF8A741AA25361A33DC71E863C1E12C113E2D62C220D18A3A1C3C02E35D86E6D4B5D95986E3F117133B83B173E0D7FCBB20F52227C14216D11DEEB28F59382193316CC09B32FF434CBCBA84BEF0F1555BF1CB100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InBody_brand extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InBody_brand() { super("lbj.laptop.InBody_brand"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InBody_brand(DocQueryPair)' defined on line 32 of laptop_s31.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("brand");
    Collection c = dq.getDoc().getTokenizedBody();
    boolean inBody = false;
    String[] values = fieldValue.split("\\s+");
    int i = 0;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (w.equals(values[i]))
      {
        i++;
        if (i == values.length)
        {
          inBody = true;
          break;
        }
      }
      else
      {
        i = 0;
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
        System.err.println("Classifier 'InBody_brand(DocQueryPair)' defined on line 32 of laptop_s31.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InBody_brand".hashCode(); }
  public boolean equals(Object o) { return o instanceof InBody_brand; }
}

