// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2C8BCA02C034150F75EC2B58A5AA0DD9B11CDB51F304EA9C9E3036A5F6296111FFDDA02C03BB91B3403AC8C77B2E20B044D4CF0E6A2EDE575DD9A9D17437E44D7532382CE337CE7307415F839A81744A038FEEFD4B49894910BC01B787E28BC42A187899A2D1D63453C92AE8E3BD69551859D5E033B834D8FD16C704CD4DB0AA2BAA14F6FB06D17C0F92A000000

package lbj.laptop;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import java.util.*;
import lbj.common.*;
import lbjse.objectsearch.*;




public class brand_ranker extends SparseAveragedPerceptron
{
  public static boolean isTraining = false;
  private static java.net.URL lcFilePath;
  private static brand_ranker instance;
  public static brand_ranker getInstance()
  {
    if (instance == null)
      instance = (brand_ranker) Classifier.binaryRead(lcFilePath, "brand_ranker");
    return instance;
  }

  static
  {
    lcFilePath = brand_ranker.class.getResource("brand_ranker.lc");

    if (lcFilePath == null)
    {
      System.err.println("ERROR: Can't locate brand_ranker.lc in the class path.");
      System.exit(1);
    }
  }

  public void save()
  {
    if (instance == null) return;

    if (lcFilePath.toString().indexOf(".jar!" + java.io.File.separator) != -1)
    {
      System.err.println("WARNING: brand_ranker.lc is part of a jar file.  It will be written to the current directory.  Use 'jar -u' to update the jar file.  To avoid seeing this message in the future, unpack the jar file and put the unpacked files on your class path instead.");
      instance.binaryWrite(System.getProperty("user.dir") + java.io.File.separator + "brand_ranker.lc", "brand_ranker");
    }
    else instance.binaryWrite(lcFilePath.getPath(), "brand_ranker");
  }

  public static Parser getParser() { return null; }

  public static TestingMetric getTestingMetric() { return null; }

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  private boolean isClone;

  public brand_ranker()
  {
    super("lbj.laptop.brand_ranker");
    isClone = true;
    if (instance == null)
      instance = (brand_ranker) Classifier.binaryRead(lcFilePath, "brand_ranker");
  }

  private brand_ranker(boolean b)
  {
    super(1.0, 0, 2);
    containingPackage = "lbj.laptop";
    name = "brand_ranker";
    setLabeler(new Oracle());
    setExtractor(new brand_features());
    isClone = false;
  }

  public String getInputType() { return "lbjse.objectsearch.DocQueryPair"; }
  public String getOutputType() { return "discrete"; }

  public String[] allowableValues()
  {
    return DiscreteFeature.BooleanValues;
  }

  public void learn(Object example)
  {
    if (isClone)
    {
      instance.learn(example);
      return;
    }

    Classifier saveExtractor = extractor;
    Classifier saveLabeler = labeler;

    if (!(example instanceof DocQueryPair))
    {
      if (example instanceof FeatureVector)
      {
        if (!(extractor instanceof FeatureVectorReturner))
          setExtractor(new FeatureVectorReturner());
        if (!(labeler instanceof LabelVectorReturner))
          setLabeler(new LabelVectorReturner());
      }
      else
      {
        String type = example == null ? "null" : example.getClass().getName();
        System.err.println("Classifier 'brand_ranker(DocQueryPair)' defined on line 69 of laptop_s31.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }
    }

    super.learn(example);

    if (saveExtractor != extractor) setExtractor(saveExtractor);
    if (saveLabeler != labeler) setLabeler(saveLabeler);
  }

  public void learn(Object[] examples)
  {
    if (isClone)
    {
      instance.learn(examples);
      return;
    }

    Classifier saveExtractor = extractor;
    Classifier saveLabeler = labeler;

    if (!(examples instanceof DocQueryPair[]))
    {
      if (examples instanceof FeatureVector[])
      {
        if (!(extractor instanceof FeatureVectorReturner))
          setExtractor(new FeatureVectorReturner());
        if (!(labeler instanceof LabelVectorReturner))
          setLabeler(new LabelVectorReturner());
      }
      else
      {
        String type = examples == null ? "null" : examples.getClass().getName();
        System.err.println("Classifier 'brand_ranker(DocQueryPair)' defined on line 69 of laptop_s31.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }
    }

    super.learn(examples);

    if (saveExtractor != extractor) setExtractor(saveExtractor);
    if (saveLabeler != labeler) setLabeler(saveLabeler);
  }

  public FeatureVector classify(Object __example)
  {
    if (isClone) return instance.classify(__example);

    Classifier __saveExtractor = extractor;

    if (!(__example instanceof DocQueryPair))
    {
      if (__example instanceof FeatureVector)
      {
        if (!(extractor instanceof FeatureVectorReturner))
          setExtractor(new FeatureVectorReturner());
      }
      else
      {
        String type = __example == null ? "null" : __example.getClass().getName();
        System.err.println("Classifier 'brand_ranker(DocQueryPair)' defined on line 69 of laptop_s31.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }
    }

__classify:
    {
      if (__example == brand_ranker.exampleCache.get()) break __classify;
      brand_ranker.exampleCache.set(__example);

      brand_ranker.cache.set(super.classify(__example));
    }

    if (__saveExtractor != this.extractor) setExtractor(__saveExtractor);
    return (FeatureVector) brand_ranker.cache.get();
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (isClone)
      return instance.classify(examples);

    Classifier saveExtractor = extractor;

    if (!(examples instanceof DocQueryPair[]))
    {
      if (examples instanceof FeatureVector[])
      {
        if (!(extractor instanceof FeatureVectorReturner))
          setExtractor(new FeatureVectorReturner());
      }
      else
      {
        String type = examples == null ? "null" : examples.getClass().getName();
        System.err.println("Classifier 'brand_ranker(DocQueryPair)' defined on line 69 of laptop_s31.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }
    }

    FeatureVector[] result = super.classify(examples);
    if (saveExtractor != extractor) setExtractor(saveExtractor);
    return result;
  }

  public String discreteValue(Object __example)
  {
    DiscreteFeature f = (DiscreteFeature) classify(__example).firstFeature();
    return f == null ? "" : f.getValue();
  }

  public int hashCode() { return "brand_ranker".hashCode(); }
  public boolean equals(Object o) { return o instanceof brand_ranker; }

  public void write(java.io.PrintStream a0)
  {
    if (isClone)
    {
      instance.write(a0);
      return;
    }

    super.write(a0);
  }

  public double score(java.lang.Object a0)
  {
    if (isClone)
      return instance.score(a0);
    return super.score(a0);
  }

  public void promote(java.lang.Object a0)
  {
    if (isClone)
    {
      instance.promote(a0);
      return;
    }

    super.promote(a0);
  }

  public void demote(java.lang.Object a0)
  {
    if (isClone)
    {
      instance.demote(a0);
      return;
    }

    super.demote(a0);
  }

  public void forget()
  {
    if (isClone)
    {
      instance.forget();
      return;
    }

    super.forget();
  }

  public double getLearningRate()
  {
    if (isClone)
      return instance.getLearningRate();
    return super.getLearningRate();
  }

  public void setLearningRate(double a0)
  {
    if (isClone)
    {
      instance.setLearningRate(a0);
      return;
    }

    super.setLearningRate(a0);
  }

  public void setThreshold(double a0)
  {
    if (isClone)
    {
      instance.setThreshold(a0);
      return;
    }

    super.setThreshold(a0);
  }

  public void setLabeler(LBJ2.classify.Classifier a0)
  {
    if (isClone)
    {
      instance.setLabeler(a0);
      return;
    }

    super.setLabeler(a0);
  }

  public double getInitialWeight()
  {
    if (isClone)
      return instance.getInitialWeight();
    return super.getInitialWeight();
  }

  public void setInitialWeight(double a0)
  {
    if (isClone)
    {
      instance.setInitialWeight(a0);
      return;
    }

    super.setInitialWeight(a0);
  }

  public double getThreshold()
  {
    if (isClone)
      return instance.getThreshold();
    return super.getThreshold();
  }

  public double getPositiveThickness()
  {
    if (isClone)
      return instance.getPositiveThickness();
    return super.getPositiveThickness();
  }

  public void setPositiveThickness(double a0)
  {
    if (isClone)
    {
      instance.setPositiveThickness(a0);
      return;
    }

    super.setPositiveThickness(a0);
  }

  public double getNegativeThickness()
  {
    if (isClone)
      return instance.getNegativeThickness();
    return super.getNegativeThickness();
  }

  public void setNegativeThickness(double a0)
  {
    if (isClone)
    {
      instance.setNegativeThickness(a0);
      return;
    }

    super.setNegativeThickness(a0);
  }

  public void setThickness(double a0)
  {
    if (isClone)
    {
      instance.setThickness(a0);
      return;
    }

    super.setThickness(a0);
  }

  public LBJ2.classify.ScoreSet scores(java.lang.Object a0)
  {
    if (isClone)
      return instance.scores(a0);
    return super.scores(a0);
  }

  public LBJ2.classify.Classifier getLabeler()
  {
    if (isClone)
      return instance.getLabeler();
    return super.getLabeler();
  }

  public LBJ2.classify.Classifier getExtractor()
  {
    if (isClone)
      return instance.getExtractor();
    return super.getExtractor();
  }

  public void setExtractor(LBJ2.classify.Classifier a0)
  {
    if (isClone)
    {
      instance.setExtractor(a0);
      return;
    }

    super.setExtractor(a0);
  }

  public void doneLearning()
  {
    if (isClone)
    {
      instance.doneLearning();
      return;
    }

    super.doneLearning();
  }
}

