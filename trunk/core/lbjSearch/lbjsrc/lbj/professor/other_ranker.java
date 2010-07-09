// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D2DC1CA0280441581E7593B45031B0CD5B90AD764F00179993AE0D43ADD991522A77FC540FF6F3E7BE2A1562E7A71F19512966E7135A19A77590F0A617E9CC5335FDD98358D75983EE0E92A1071513E98C1D581EF6A7A4ACAC885D5A1118B2E63B864E961AAC04BD15D07E4A35826F573516BE052CC22E1D26B1AD9E901F3F8286AE6A5038DF10D7C4E61B2A000000

package lbj.professor;

import LBJ2.classify.*;
import LBJ2.infer.*;
import LBJ2.learn.*;
import LBJ2.parse.*;
import lbj.common.*;
import lbjse.objectsearch.*;




public class other_ranker extends SparseAveragedPerceptron
{
  public static boolean isTraining = false;
  private static java.net.URL lcFilePath;
  private static other_ranker instance;
  public static other_ranker getInstance()
  {
    if (instance == null)
      instance = (other_ranker) Classifier.binaryRead(lcFilePath, "other_ranker");
    return instance;
  }

  static
  {
    lcFilePath = other_ranker.class.getResource("other_ranker.lc");

    if (lcFilePath == null)
    {
      System.err.println("ERROR: Can't locate other_ranker.lc in the class path.");
      System.exit(1);
    }
  }

  public void save()
  {
    if (instance == null) return;

    if (lcFilePath.toString().indexOf(".jar!" + java.io.File.separator) != -1)
    {
      System.err.println("WARNING: other_ranker.lc is part of a jar file.  It will be written to the current directory.  Use 'jar -u' to update the jar file.  To avoid seeing this message in the future, unpack the jar file and put the unpacked files on your class path instead.");
      instance.binaryWrite(System.getProperty("user.dir") + java.io.File.separator + "other_ranker.lc", "other_ranker");
    }
    else instance.binaryWrite(lcFilePath.getPath(), "other_ranker");
  }

  public static Parser getParser() { return null; }

  public static TestingMetric getTestingMetric() { return null; }

  private static ThreadLocal cache = new ThreadLocal(){ };
  private static ThreadLocal exampleCache = new ThreadLocal(){ };

  private boolean isClone;

  public other_ranker()
  {
    super("lbj.professor.other_ranker");
    isClone = true;
    if (instance == null)
      instance = (other_ranker) Classifier.binaryRead(lcFilePath, "other_ranker");
  }

  private other_ranker(boolean b)
  {
    super(1.0, 0, 2);
    containingPackage = "lbj.professor";
    name = "other_ranker";
    setLabeler(new Oracle());
    setExtractor(new other_features());
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
        System.err.println("Classifier 'other_ranker(DocQueryPair)' defined on line 14 of professor_s1.lbj received '" + type + "' as input.");
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
        System.err.println("Classifier 'other_ranker(DocQueryPair)' defined on line 14 of professor_s1.lbj received '" + type + "' as input.");
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
        System.err.println("Classifier 'other_ranker(DocQueryPair)' defined on line 14 of professor_s1.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }
    }

__classify:
    {
      if (__example == other_ranker.exampleCache.get()) break __classify;
      other_ranker.exampleCache.set(__example);

      other_ranker.cache.set(super.classify(__example));
    }

    if (__saveExtractor != this.extractor) setExtractor(__saveExtractor);
    return (FeatureVector) other_ranker.cache.get();
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
        System.err.println("Classifier 'other_ranker(DocQueryPair)' defined on line 14 of professor_s1.lbj received '" + type + "' as input.");
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

  public int hashCode() { return "other_ranker".hashCode(); }
  public boolean equals(Object o) { return o instanceof other_ranker; }

  public void write(java.io.PrintStream a0)
  {
    if (isClone)
    {
      instance.write(a0);
      return;
    }

    super.write(a0);
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

  public LBJ2.classify.ScoreSet scores(java.lang.Object a0)
  {
    if (isClone)
      return instance.scores(a0);
    return super.scores(a0);
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

