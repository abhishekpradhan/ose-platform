// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// mixed% price_features(DocQueryPair dq) <- NumericSurroundingWordsInBody_price

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.nlp.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class price_features extends Classifier
{
  private static final NumericSurroundingWordsInBody_price __NumericSurroundingWordsInBody_price = new NumericSurroundingWordsInBody_price();

  public price_features() { super("lbj.laptop.price_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    return __NumericSurroundingWordsInBody_price.classify(example);
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'price_features(DocQueryPair)' defined on line 34 of laptop_s33.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "price_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof price_features; }
}

