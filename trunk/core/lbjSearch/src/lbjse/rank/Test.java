package lbjse.rank;

import lbjse.trainer.TrainingSession;
import LBJ2.classify.DiscreteFeature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.FeatureVectorReturner;
import LBJ2.learn.Learner;

public class Test {
	public static void main(String[] args) throws Exception{
//		other_ranker ranker = new other_ranker();
		TrainingSession session = new TrainingSession(1);
		Learner ranker = session.getLearner();
		ranker.write(System.out);
		FeatureVector vector = new FeatureVector();
//		vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInTitle_InDict", "news" ));
//		vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "campus" ));
//		vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "publications" ));
//		vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "school" ));
		vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "fax" ));
		System.out.println("Vector : " + vector);
//		System.out.println("Value : " + ranker.classify(vector) );
		ranker.setExtractor(new FeatureVectorReturner());
		System.out.println("Score : " + ranker.scores(vector).get("true") );
//		ranker.write(System.out);
		
//		ranker.setLabeler(
//				   new LabelVectorReturner(){
//					   public String getOutputType() { return "discrete"; }
//					   public String[] allowableValues() { 
//						   return DiscreteFeature.BooleanValues; 
//					   }
//					   public String discreteValue(Object e)
//					   {
//						   return ((DiscreteFeature) classify(e).firstFeature()).getValue();
//					   }
//				});
//		
	}
}


/*
 * The Feature.equals() bug 
 
vector.addFeature(new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "fax" ));
DiscreteFeature f1 = searchFeatures.get(4);
DiscreteFeature f2 = new DiscreteFeature("lbjse.objectsearch.lbj", "WordsInBody_InDict", "fax" );
DiscreteFeature f3 = new DiscreteFeature(f1.getPackage().intern(), f1.getIdentifier().intern(), f1.getValue().intern());

System.out.println("Vector : " + vector);
System.out.println("Feature 1 : " + f1);
System.out.println("Feature 2 : " + f2);
System.out.println("Feature 3 : " + f3);
learner.setExtractor(new FeatureVectorReturner());

vector.addFeature(f3);
System.out.println("Score : " + learner.scores(vector).get("true") );
System.out.println(f1.equals(f2));
System.out.println(f1.equals(f3));
System.out.println(f2.equals(f3));
System.out.println(" P : " + f1.getPackage().equals(f2.getPackage()));
System.out.println(" I : " + f1.getIdentifier().equals(f2.getIdentifier()));
System.out.println(" V : " + f1.getValue().equals(f2.getValue()));
System.out.println("Score : " + vector);
*/