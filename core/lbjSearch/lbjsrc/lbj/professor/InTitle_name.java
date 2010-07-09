// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B880000000000000005615D4F43C03C0DFB265F498A222EE567A124A94312013179D62495BE676D242C21792F12EFB3E4B35930AC5C6FB797E7F296825D1191165E734CE0F5CBD75457B1AE7CE13E7E385A80DC953CDC51C73C31742FD14B48EA976BEA74858086A34E19CA47E2FE66455195BA0D683CA3C08179631A2D51C23837853350F05FCA02B17ABFB90724F4F58DC86723FF012834BE186278275A5BE216571F3BDD3CB7E5792303F2739EDC11BA267BB45612224E9184817D514B1228A51364BC2511BC4B63479E3F2426374B9EE1F38377AFF29F02455A6A6476A9F193FF2125B0A60387EE5C7AA9CCD696FA3B21595E48B8995C5C8B178EB3E3E80FC91936F211F011DE9A28F19382993336A019722FF6D7C9FD5466FB0A7AF13B5DC100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InTitle_name extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InTitle_name() { super("lbj.professor.InTitle_name"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InTitle_name(DocQueryPair)' defined on line 9 of professor_s4.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("name").toLowerCase();
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
        System.err.println("Classifier 'InTitle_name(DocQueryPair)' defined on line 9 of professor_s4.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InTitle_name".hashCode(); }
  public boolean equals(Object o) { return o instanceof InTitle_name; }
}

