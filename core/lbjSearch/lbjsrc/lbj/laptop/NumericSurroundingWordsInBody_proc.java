// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D71914B430130158FFAC8B024616BB84F86DE545A828845B5BE1B48466B48EA3967298855AFFDD9C65AE611CB480999FEDB7331BE2816C887E0F89ED09D9995266F98CA3A5DBA76B1EE9EADBDDD273CED8AB5F6E9212FE6AAD138DD69075308F287074B64BB0431D3344FB6427F9863350368798EAD05B1F4FE8C17EEBFDBACE6BE5164115556EBCCBF8AA2BC11CC22B4C086C16B671ABD48228708AB8170C627CAAA227AC240C76D4B2CB1F4122B674118D809DD3E421998EC3DD962BD227DB1DC105DFA380C723F438A3626C07C154C1B7B2A9CF495D9CCD55F227526E51C06851C054BB1194535F1C5EC097FBDDA2739F81FFD24974BFC6BAE2440AB6D3B1305A6BD1DF306EA4D48F11B49F1A084101A820E2F0FE2269B3598EDE16FFD0888BA02F30200000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;
import ose.parser.RangeFunctionHandler;
import ose.processor.cascader.RangeConstraint;


public class NumericSurroundingWordsInBody_proc extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInBody_proc() { super("lbj.laptop.NumericSurroundingWordsInBody_proc"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInBody_proc(DocQueryPair)' defined on line 59 of laptop_s37.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInBody_proc.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInBody_proc.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedBody = Utils.convertToLinkedVector(dq.getDoc().getTokenizedBody());
    String fieldValue = dq.getQuery().getFieldValue("proc");
    RangeConstraint rc = RangeFunctionHandler.parse(fieldValue);
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedBody, fieldValue, rc, -2, 2);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInBody_proc.exampleCache.set(__example);
    NumericSurroundingWordsInBody_proc.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInBody_proc(DocQueryPair)' defined on line 59 of laptop_s37.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInBody_proc".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInBody_proc; }
}

