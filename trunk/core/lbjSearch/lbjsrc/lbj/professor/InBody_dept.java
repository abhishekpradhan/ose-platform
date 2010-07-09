// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056151CE43C03C0DF51BA7A4451117F2BB03484390120188BC631ACA57B3B6152B52E25602EFD17ADD6C105E26BFDBC3FB794349AE88C80B0F7B1A93EB738B765771AE7EE13E1F9C25486E0A1E6EA0EB1E58329FE0A5247DCB957D32CC4043D12F8465A3797F71455195BA0D683C348103ECD6245AB289707E0B66A0E1AEB828C6C9EEFB68D1A7AF2C62BB9C4FD480E0DA70A1DF9C586DAB485D9CDC27D0F19795AC0C565B94B774CAA85DA259588609760216C5750D6880A61C81D2B454C23DAD0D9AFCBF88DC6D6A74CF4ECD9EFBCD380559A9A1D996E74ECFBC74D28A1C0E1A71F9A6237B4A5BECA4456931E26666723E6C1AFE87B32C7E88C1B7948B98867751CF8C1498C9903568C319F3BE3E9F55464FB5D00B15F8C100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InBody_dept extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InBody_dept() { super("lbj.professor.InBody_dept"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InBody_dept(DocQueryPair)' defined on line 32 of professor_s2.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("dept").toLowerCase();
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
        System.err.println("Classifier 'InBody_dept(DocQueryPair)' defined on line 32 of professor_s2.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InBody_dept".hashCode(); }
  public boolean equals(Object o) { return o instanceof InBody_dept; }
}

