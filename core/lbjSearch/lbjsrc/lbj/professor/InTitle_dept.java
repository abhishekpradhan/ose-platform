// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005615D4B63C03C0DFB22272B9033BB76D357C0A056C6CACE2D691E62A4A2A6CE6D6569D70BFFE393929D168F229ED3F3DB767349AE88C80B2FB12678F6D0E995DD78AF9B7C8F9F469224371D077730FD0F2C19C770D21AB6E5DABE116120A9E09742B2D9BC789155546DA24B1E0BE0306C5AD48A4750BC0EC16DC41C34D3B28C6C9EEF62C90D3D71633AD9CCF3480E0DA70A9C1AC596DAB485D5DFC67F0FE97D5AC0CCBCD4A3B32655CE679AC24448C330903E6B28634405B26C8695A226996D68EAD7E584C6E863D32E707EE4FF52F148AA4D4D8EC43F327EF524A614D060F2DB8F45399BD2DE57652A2BC907133B8BA173E0D77C7C11E93327CE522E122AD3550F3270523766C4122F44EFDAF83FBB8CCE710C4675307DC100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InTitle_dept extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InTitle_dept() { super("lbj.professor.InTitle_dept"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InTitle_dept(DocQueryPair)' defined on line 9 of professor_s2.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("dept").toLowerCase();
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
        System.err.println("Classifier 'InTitle_dept(DocQueryPair)' defined on line 9 of professor_s2.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InTitle_dept".hashCode(); }
  public boolean equals(Object o) { return o instanceof InTitle_dept; }
}

