// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005615D4B63C03C0DFB22272B9033BB76D357C0A056C6CACE2D691E62A4A2A6CE5D65A9D70BFFE393929D168F229ED3F3DB767349AE88C80B2FB12678F6DB7AB8ABF05F37F81F3F9C25486ECA1EEE60EB1E58329FE0A5247DCBA57D32C24043D12F8465A379F033AAA8CA55863C16D160C8B4B9059EA0691C93CA992878A76509D83DDFD4831A7AF2C664B399F7801C1A5F0439349B2DA5790BABAF9DEE1E297D5AC0CCBCD4A7774CAA8DDE259588809760216CD650D6880A65C81D2B454C23DAD0D5BFCB098DC1D6A74CF0ECD9EFB4E380559A9A1D996E74ECFB484D28A1C0E9B71F9A6237B5ADBECA4456931E26661753E6C1AFE8F832C3764E8DB44C3444B7AA0E74E0A46ECC89244E98CFB5F17E77199DF2086995F87DC100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InTitle_univ extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InTitle_univ() { super("lbj.professor.InTitle_univ"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InTitle_univ(DocQueryPair)' defined on line 9 of professor_s3.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("univ").toLowerCase();
    Collection c = dq.getDoc().getTokenizedTitle();
    boolean inTitle = false;
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
          inTitle = true;
          break;
        }
      }
      else
      {
        i = 0;
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
        System.err.println("Classifier 'InTitle_univ(DocQueryPair)' defined on line 9 of professor_s3.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InTitle_univ".hashCode(); }
  public boolean equals(Object o) { return o instanceof InTitle_univ; }
}

