// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D709F5B43C03415CFBAC5B02420D51F56F2ECEB9C040199CAEC74929CDECBC262EE6211FF0BFEE62DA496116F2124ECD3FB372753975C810F21E12EB1239AD646671DA62BBF767CADFD9D68281C7975DA5CDA35F8119FB63D2138E3A48B950CF0CD39D30AED1AA0E8128B30A5AF64DDBB0A687A046C75AC9DF04E0D8B9E0B0D7CA6F8121558CC79666E5125EA06B1835118E80D8ED5B62662E0EAEB8C0EB5F8AA8225DC229F66D9A3C634464F36B94E99C737DF72B8977F2729A52C26952C2318BB4C31B17E92093B9714DFE2ED3F15FF714E89D9335C55204F3A71538D86CCAEC813B2595CFC023DA8D3A5F8054107D3CB7285E9419877283DF2005D40E6C4C100000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class NumericSurroundingWordsInTitle_hdd extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInTitle_hdd() { super("lbj.laptop.NumericSurroundingWordsInTitle_hdd"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInTitle_hdd(DocQueryPair)' defined on line 40 of laptop_s36.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInTitle_hdd.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInTitle_hdd.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedTitle = Utils.convertToLinkedVector(dq.getDoc().getTokenizedTitle());
    String fieldValue = dq.getQuery().getFieldValue("hdd");
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedTitle, fieldValue, -5, 5);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInTitle_hdd.exampleCache.set(__example);
    NumericSurroundingWordsInTitle_hdd.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInTitle_hdd(DocQueryPair)' defined on line 40 of laptop_s36.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInTitle_hdd".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInTitle_hdd; }
}

