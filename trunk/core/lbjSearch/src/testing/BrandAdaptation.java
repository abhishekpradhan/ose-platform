package testing;

import java.io.IOException;

import lbj.common.Oracle;
import lbj.laptop.InTitle_brand;
import lbj.laptop.brand_features;
import lbj.laptop.brand_ranker;
import lbjse.data.DocumentFromTrec;
import lbjse.objectsearch.DocQueryFileParser;
import lbjse.objectsearch.DocQueryPairFromFile;
import LBJ2.classify.Classifier;
import LBJ2.classify.TestDiscrete;
import LBJ2.parse.Parser;
import LBJ2.util.TableFormat;

public class BrandAdaptation {

	public static void main(String[] args) throws IOException {
		
		
		brand_ranker ranker = new brand_ranker();

		DocQueryFileParser trainingData1 =
      new DocQueryFileParser("/Users/rizzolo/cogcomp/LBP/data/objectSearch/camera_no_sony.trec","/Users/rizzolo/cogcomp/LBP/data/objectSearch/query_canon_only.txt");
		DocQueryFileParser trainingData2 =
      new DocQueryFileParser("/Users/rizzolo/cogcomp/LBP/data/objectSearch/camera1.trec","/Users/rizzolo/cogcomp/LBP/data/objectSearch/query_canon_only.txt");
		DocQueryFileParser trainingData3 =
      new DocQueryFileParser("/Users/rizzolo/cogcomp/LBP/data/objectSearch/camera1.trec","/Users/rizzolo/cogcomp/LBP/data/objectSearch/query.txt");
		DocQueryFileParser trainingData4 =
      new DocQueryFileParser("/Users/rizzolo/cogcomp/LBP/data/objectSearch/camera_sony.trec","/Users/rizzolo/cogcomp/LBP/data/objectSearch/query.txt");
		learn(ranker, 1, trainingData3);

		DocQueryFileParser testingData =
      new DocQueryFileParser("/Users/rizzolo/cogcomp/LBP/data/objectSearch/camera_sony.trec", "/Users/rizzolo/cogcomp/LBP/data/objectSearch/query.txt");

    /*
		DocQueryFileParser trainingData1 = new DocQueryFileParser("C:\\working\\camera_no_sony.trec","C:\\working\\query_canon_only.txt");
		DocQueryFileParser trainingData2 = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query_canon_only.txt");
		DocQueryFileParser trainingData3 = new DocQueryFileParser("C:\\working\\camera1.trec","C:\\working\\query.txt");
		DocQueryFileParser trainingData4 = new DocQueryFileParser("C:\\working\\camera_sony.trec","C:\\working\\query.txt");
		DocQueryFileParser trainingData5 = new DocQueryFileParser("C:\\working\\camera_no_sony.trec","C:\\working\\query.txt");
		
		learn(ranker, 200, trainingData5);
		
		DocQueryFileParser testingData = new DocQueryFileParser("C:\\working\\camera_sony.trec", "C:\\working\\query.txt");
    */
//		TestDiscrete result = test(ranker, trainingData3);
		TestDiscrete result = test(ranker, testingData);

		result.printPerformance(System.out);
		
		ranker.write(System.out);
	}

	static private void learn(brand_ranker ranker, int nRounds, Parser data ){
    ranker.forget();
		brand_ranker.isTraining = true;
    Oracle oracle = new Oracle();
    brand_features features = new brand_features();
    InTitle_brand feature = new InTitle_brand();
    double[][] stats = new double[2][2];
		int count = 0;

		for (int i = 0; i < nRounds; i++) {
			data.reset();
			count = 0;
			DocQueryPairFromFile pair = null;
			while ( (pair = (DocQueryPairFromFile) data.next()) != null){
				DocumentFromTrec trec = (DocumentFromTrec) pair.getDoc();
				if (trec.getTagForField("other").size() > 0)
					continue;
        String label = oracle.discreteValue(pair);
        String value = feature.discreteValue(pair);
        // If the classifier doesn't have a discreteValue() method, use the
        // code below instead:
        //String value = feature.classify(pair).firstFeature().getStringValue();

        if (label.equals("false"))
        {
          if (value.equals("false")) ++stats[0][0];
          else ++stats[0][1];
        }
        else
        {
          if (value.equals("false")) ++stats[1][0];
          else ++stats[1][1];
        }

				
//				System.out.println(pair.oracle());
//				System.out.println(" -- " + feature.discreteValue(pair) + "  ,  " + oracle.discreteValue(pair));
				
				ranker.learn(pair);
        System.out.println(pair.getDoc().getTitle() + ", "
                           + pair.getQuery().getFieldValue("brand") + ", "
                           + features.classify(pair) + ", " + label);
			}
			//System.out.println("Total " + count + " examples. ");
		}
		System.out.println("Total " + count + " examples. ");
		brand_ranker.isTraining = false;
    ranker.save();

    String[] columnLabels = { "feature:", "false", "true" };
    String[] rowLabels = { "oracle:false", "oracle:true" };
    int[] significantDigits = { 0, 0 }; // One for each column
    int[] dashRows = { 0 };
    String[] result =
      TableFormat.tableFormat(columnLabels, rowLabels, stats,
                              significantDigits, dashRows);
    System.out.println();
    for (int i = 0; i < result.length; ++i)
      System.out.println(result[i]);
    System.out.println();
	}

	static public TestDiscrete test(Classifier classifier, Parser parser)  {
		parser.reset();
		TestDiscrete results = new TestDiscrete();
		Object obj;
		while ( (obj = parser.next()) != null){
			results.reportPrediction(classifier.discreteValue(obj) , ((DocQueryPairFromFile) obj).oracle() + "");
		}
		return results;
	}
}
