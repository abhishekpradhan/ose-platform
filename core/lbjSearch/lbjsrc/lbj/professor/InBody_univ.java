// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056151CE43C03C0DF51BA7A4431117F2BB03484390120188BC631ACA57B3B6152C21773602EFD17ADEAC105E26BFDBC3FB794D49AA88C80B4F7B1AE3FB77E9E8AEE245FCD16C3F395A805F143CDC51C73CB0742FD24348EAE73BEA3489B086A54EE9CA47E2FE72455195BA0D683C348316C58D48A4790B80EC165C41C3453928C6C1EEFB68D3A7AF2CA3BB9C4FD680E0DA70AEDF9C586CAB485E8E665B1836E5692303DA639E3C11BA26DBE43B24438C330903EAB48624405B46C8695A2269965686CE3FE3263B3B9E11F39377AFF27F948AA4D0D8EC43FD37EF5E3A604D9C0E1A31F9A6037B2AD8ECA443B9D08B8999F86CD834FD2FEA78F21193672907B11DEEB48F193821933168C09722F76D5CBCBA8C8E7104E7804FE8C100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InBody_univ extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InBody_univ() { super("lbj.professor.InBody_univ"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InBody_univ(DocQueryPair)' defined on line 32 of professor_s3.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("univ").toLowerCase();
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
        System.err.println("Classifier 'InBody_univ(DocQueryPair)' defined on line 32 of professor_s3.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InBody_univ".hashCode(); }
  public boolean equals(Object o) { return o instanceof InBody_univ; }
}

