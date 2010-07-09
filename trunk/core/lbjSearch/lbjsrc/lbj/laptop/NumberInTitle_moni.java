// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D4051DE43C03C0CF51B6F49840DF182C3D012D424318810F882D6DDDCA561B07C559188F77C9615782F2EBCDD5EE2EE827B28A80BD1E4D0AC62DE8432EBF98319BB3E6F960493F360218EE3C3CDC53C73C3BA05AD34F481BB7D017048B532BAD3AE4267EBC87FB0BE655EDA59FA16DC1326BA4C90AD5C36913B367C74C44F58DD450C99EB16E881210DCD9CC2D78891B68E950CD641528AD44A6C4B51DF1E2E42DAE012F61F35B02F792D3A94D9B918F22B4396C75ACF0C32ACA34E92A9A707F2A4137593825EEFCB6E4955205257907487369ECD8EFAEFD61C7989B455591CA963281E853CF8D1B52F02BC76CEEE7108814B16DA7100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumberInTitle_moni extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumberInTitle_moni() { super("lbj.laptop.NumberInTitle_moni"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumberInTitle_moni(DocQueryPair)' defined on line 10 of laptop_s32.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    DocQueryPair dq = (DocQueryPair) __example;
    String fieldValue = dq.getQuery().getFieldValue("moni");
    Collection c = dq.getDoc().getTokenizedTitle();
    boolean inTitle = false;
    for (Iterator it = c.iterator(); it.hasNext(); )
    {
      String w = ((String) it.next()).toLowerCase();
      if (Utils.satisfyConstraint(new Word(w), fieldValue))
      {
        inTitle = true;
        break;
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
        System.err.println("Classifier 'NumberInTitle_moni(DocQueryPair)' defined on line 10 of laptop_s32.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumberInTitle_moni".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumberInTitle_moni; }
}

