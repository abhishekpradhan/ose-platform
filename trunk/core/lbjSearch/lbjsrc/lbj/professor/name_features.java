// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000BCDCCA84D415558CB4CCD4D8F4B4D4C292D2A4D26D079CF4E0C2D4D2AAC084CCC22584924D450B1D5580E2D2A2ACF2DCB49CCCB4F0FCF2A4926FCC37ACF49AC870964D150FCCB09CC29C94583F094A035F12B25000AA2FB845F7000000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;


public class name_features extends Classifier
{
  private static final SurroundingWordsInBody_name __SurroundingWordsInBody_name = new SurroundingWordsInBody_name();
  private static final InTitle_name __InTitle_name = new InTitle_name();
  private static final InBody_name __InBody_name = new InBody_name();
  private static final SurroundingWordsInTitle_name __SurroundingWordsInTitle_name = new SurroundingWordsInTitle_name();

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  public name_features() { super("lbj.professor.name_features"); }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "mixed%"; }

  public FeatureVector classify(Object example)
  {
    if (!(example instanceof DocQueryPair))
    {
      String type = example == null ? "null" : example.getClass().getName();
      System.err.println("Classifier 'name_features(DocQueryPair)' defined on line 75 of professor_s4.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    if (example == exampleCache.get()) return (FeatureVector) cache.get();

    FeatureVector result = new FeatureVector();
    result.addFeatures(__SurroundingWordsInBody_name.classify(example));
    result.addFeatures(__InTitle_name.classify(example));
    result.addFeatures(__InBody_name.classify(example));
    result.addFeatures(__SurroundingWordsInTitle_name.classify(example));

    exampleCache.set(example);
    cache.set(result);

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    for (int i = 0; i < examples.length; ++i)
      if (!(examples[i] instanceof DocQueryPair))
      {
        System.err.println("Classifier 'name_features(DocQueryPair)' defined on line 75 of professor_s4.lbj received '" + examples[i].getClass().getName() + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

    return super.classify(examples);
  }

  public int hashCode() { return "name_features".hashCode(); }
  public boolean equals(Object o) { return o instanceof name_features; }

  public java.util.LinkedList getCompositeChildren()
  {
    java.util.LinkedList result = new java.util.LinkedList();
    result.add(__SurroundingWordsInBody_name);
    result.add(__InTitle_name);
    result.add(__InBody_name);
    result.add(__SurroundingWordsInTitle_name);
    return result;
  }
}

