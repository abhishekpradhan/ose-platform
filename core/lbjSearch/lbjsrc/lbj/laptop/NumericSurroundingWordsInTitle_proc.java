// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D719DDA430130158F556C5012B0BD5CEDA5B75A415019A6B6DB4909C4B486C94B39988F34FDDD46B25BB80DB901233FD9376626D543C8287E0F09ED09D999526E098CA3A5DB406B1FE86EE4C3EBE683815731CC3624EFC9A67C067B53C5D00EB1EED1DA1DE20D8406090B6427F586B3C06C0FC2EC7C6D40A774699783E66567BDEA052BAAAABC56ED3655D5F80662C9390C2D1A7BB0DE3515CD35D509D37393455555276591C72D4B2CBE041585B32106399CEE17298C88B047BA9CA74E673A932AAF378C07F62A52A694C81F03B467C3AD2D4E7BCAAF39737499B9CEED0C068D0C03B8F23B6AA9688EA4882B3EE6B9B927C8FF71AE38F71F5571950AB6D3B13052FE74720C259690F34AECF941192224551C5EEFD3B859E4595F670BBF16498212870200000

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


public class NumericSurroundingWordsInTitle_proc extends Classifier
{
  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public NumericSurroundingWordsInTitle_proc() { super("lbj.laptop.NumericSurroundingWordsInTitle_proc"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof DocQueryPair))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'NumericSurroundingWordsInTitle_proc(DocQueryPair)' defined on line 45 of laptop_s37.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (__example == NumericSurroundingWordsInTitle_proc.exampleCache.get()) return (FeatureVector) NumericSurroundingWordsInTitle_proc.cache.get();

    DocQueryPair dq = (DocQueryPair) __example;
    FeatureVector __result = new FeatureVector();
    String __id;
    String __value;

    LinkedVector tokenizedTitle = Utils.convertToLinkedVector(dq.getDoc().getTokenizedTitle());
    String fieldValue = dq.getQuery().getFieldValue("proc");
    RangeConstraint rc = RangeFunctionHandler.parse(fieldValue);
    LinkedVector features = Utils.getSurroundingFeatures(tokenizedTitle, fieldValue, rc, -1, 1);
    for (PositionWordPair pair = (PositionWordPair) features.get(0); pair != null; pair = (PositionWordPair) pair.next)
    {
      __id = this.name + ("");
      __value = "" + (pair.getWord());
      __result.addFeature(new DiscreteFeature(this.containingPackage, __id, __value));
    }

    NumericSurroundingWordsInTitle_proc.exampleCache.set(__example);
    NumericSurroundingWordsInTitle_proc.cache.set(__result);

    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'NumericSurroundingWordsInTitle_proc(DocQueryPair)' defined on line 45 of laptop_s37.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "NumericSurroundingWordsInTitle_proc".hashCode(); }
  public boolean equals(Object o) { return o instanceof NumericSurroundingWordsInTitle_proc; }
}

