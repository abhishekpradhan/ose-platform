// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D4155584C2A4D4C8F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D5580E2D2A2ACF2DCB49CCCB4F0FCF2A4926FCC37ACF49AC870964D15041E000029CD594435000000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class area_features extends Classifier
{
  private static final SurroundingWordsInBody_area __SurroundingWordsInBody_area = new SurroundingWordsInBody_area();
  private static final InBody_area __InBody_area = new InBody_area();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public area_features() { super("lbj.professor.area_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'area_features(DocQueryPair)' defined on line 41 of professor_s5.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__SurroundingWordsInBody_area.classify(example));
    result.addFeatures(__InBody_area.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'area_features(DocQueryPair)' defined on line 41 of professor_s5.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "area_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof area_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__SurroundingWordsInBody_area);
    result.add(__InBody_area);
    return result;
  }
}

