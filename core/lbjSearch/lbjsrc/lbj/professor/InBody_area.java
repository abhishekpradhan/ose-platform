// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B8800000000000000056151CE43C03C0DF51BA7A4451117F2BB0348439624020179D62495BE676D2A4852E25602EFD17ADD6C105E26BFD3BDFE93D05AA32232C2CFD686E8F6632A55771AE7AE13E1F1D25486E0A1E6EA0EB1E99329FE0A5247DCBA57D32CC4043D12F8465A3787F71455197651AD078568103ECD6245AB289707E0B66A0E1AEBC409D835FFB48D3A7AF2C62BA9C4FD680E0DA70A15F9434B6D52CAE4A665B18F8CBC2560E2BAD4A7774CAA85FA35958CC02FC042C8BEA0AD0114D2813A5698885AA5B1A35E97F11B9D9D4F08F9C933DF7EB701AA25352A33DCF8C9F7EF8A505381C34F2A35D42E654B1D9721595E48B8899D948B178EB3EDD80F9D227CE5C1E65EC7BFA0E74E1A85EC489C34EA8CF95F1FC75192DF28610E15C8C100000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class InBody_area extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public InBody_area() { super("lbj.professor.InBody_area"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'InBody_area(DocQueryPair)' defined on line 9 of professor_s5.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("area").toLowerCase();
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
        System.err.println("Classifier 'InBody_area(DocQueryPair)' defined on line 9 of professor_s5.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "InBody_area".hashCode(); }
  public boolean equals(Object o) { return o instanceof InBody_area; }
}

