// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056151CE43C03C0DF51BA7A4451117F2BB03484390120188BC631ACA57B3B6121698B49188F77C967B17049B8DE7F2FCFE52D05AA32232C2CF5786E0FAEDEB1AAB905F36F81F0F0692243B7D075710FD0F4C19C770D21AB6E5CABE116620A9E09742B2D9BCBD33AAA8CA55863C1EE2C0817E631A2D51CC3837853350F05F951463E47FF93CE0D3D71639DD46AF6240786D30D8EF4E24B6D52CAE8E669B68F8CBC2560ECBAD4A7774CAA85DA259588609760216C5650D6880A61C81D2B454C23DAD0D1BFCBF88DC6D6AB7CF4ECD9EFBCD380559A9A1D996E74ECFBC74D28A1C0EEB71F9A6237B4A5BECA4456931E26666743E6C1AFE87B32C7A88C1B7948B98867751CF8C1498C9903568C319F3BE3E9E55464FBEC9A7BDB8C100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InBody_name extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InBody_name() { super("lbj.professor.InBody_name"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InBody_name(DocQueryPair)' defined on line 32 of professor_s4.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("name").toLowerCase();
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
        System.err.println("Classifier 'InBody_name(DocQueryPair)' defined on line 32 of professor_s4.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InBody_name".hashCode(); }
  public boolean equals(Object o) { return o instanceof InBody_name; }
}

