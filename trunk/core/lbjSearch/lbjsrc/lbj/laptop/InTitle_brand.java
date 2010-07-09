// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005615D4B63C03C0DFB22272B9033BB76D357C0A036C6CACE2D691E42A4A2A6CE5D6569D70BFFE393929D168F229ED3F3DB767B49A988C80B1FB52678FA574BEB557B1A97A103E7E3A5A80DE953CDC51C73C33742FD34748EAD71BE604859086A74E98CA47E2FE61455139851AD078BF0326C5BD48A4750BE0EC163C41C343B848CAC9506B1E48E9EB0BD9C056E7D12834BE1866F8275A3BE21657134BB30CB7ED79230BC6739EDC11BA26FBF45612224E9184817D51471228AD0364BC2511BC4B13479E3F2426374B9E10F38377AFF2AF82455A6E6476A9F983FF212570A64387E14C7AA9DCDE8E0A3B21595EC8B8995D5C8B178EB7E3E40F29193E0211BE886F451CF8C149CC9913788C319F9B12E2FE223BF5194391C7FC100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InTitle_brand extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InTitle_brand() { super("lbj.laptop.InTitle_brand"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InTitle_brand(DocQueryPair)' defined on line 9 of laptop_s31.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("brand").toLowerCase();
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
        System.err.println("Classifier 'InTitle_brand(DocQueryPair)' defined on line 9 of laptop_s31.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InTitle_brand".hashCode(); }
  public boolean equals(Object o) { return o instanceof InTitle_brand; }
}

