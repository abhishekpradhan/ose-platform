package ose.learning;


public class ProfessorIntegrationModel extends LogisticIntegrationModel {

	static private final String MODEL_DIR = "prof";
	
	public static String featureQueryDomain = "" 
		+ "%Log(%TFFeature(Token(bio,biography))) "
		+ "%Log(%TFFeature(Token(publication,publications,paper,papers))) " 
		+ "%Log(%TFFeature(Token(research))) "
		+ "%Log(%TFFeature(Token(teaching,class))) "
		+ "%Log(%TFFeature(Token(students))) "
		+ "%Log(%TFFeature(Token(professor))) "
		+ "%TFFeature(HTMLTitle(home, homepages)) ";

//	public static String featureQueryDomain = "" 
//		+ "%TFFeature(Token(magicshouldnothappen))"
//		+ "%TFFeature(Token(magicshouldnothappen))" 
//		+ "%TFFeature(Token(magicshouldnothappen))"
//		+ "%TFFeature(Token(magicshouldnothappen))"
//		+ "%TFFeature(Token(magicshouldnothappen))"
//		+ "%TFFeature(Token(magicshouldnothappen))"
//		+ "%TFFeature(Token(magicshouldnothappen))";

	public static String featureName = ""	
		+ "%BooleanFeature(Proximity(Token(professor),Token(NAME),-3,3)) " 
		+ "%Log(%TFFeature(Token(NAME))) "
		+ "%TFFeature(HTMLTitle(NAME)) ";
	
	public static String featureDept = ""
		+ "%BooleanFeature(Proximity(Token(professor),Token(DEPARTMENT),-3,3)) "  /* professor of "computer science" */
		+ "%BooleanFeature(Proximity(Token(department),Token(DEPARTMENT),-5,5)) "
		+ "%Log(%TFFeature(Token(DEPARTMENT))) ";
	
	public static String featureArea = "" 
		+ "%BooleanFeature(Proximity(Phrase('research group'),Token(AREA),-10,10)) " 			
		+ "%BooleanFeature(Proximity(Phrase('group'),Token(AREA),-3,3)) " 
		+ "%BooleanFeature(Proximity(Or(Phrase('my research'),Phrase('research interests'),Phrase('research summary')),Token(AREA),-50,0)) "
		+ "%Log(%TFFeature(Token(AREA))) ";
	
	public static String featureUniversity = ""
		+ "%BooleanFeature(Proximity(Token(university),Token(UNIVERSITY),-5,5)) "
		+ "%Log(%TFFeature(Token(UNIVERSITY))) ";

	public static String ALL_FEATURES = featureQueryDomain + " " + featureName + " " + featureDept + " " + featureArea + " " + featureUniversity;
	
	private int [] FEATURE_MODEL_MAPPING = new int [] {
			0,0,0,0,0,0,0,
			1,1,1,
			2,2,2,
			3,3,3,3,
			4,4
			};

	private static final String [] MODEL_FILES = new String[] {"domain.model","name.model","dept.model","area.model","uni.model" };
	private static final double [] MODEL_ACCURACY = new double [] {     0.877,      0.960 ,      0.943 ,       0.942,     0.917 };
	private static final String [] KEY2NAME = new String []{"","NAME", "DEPARTMENT", "AREA", "UNIVERSITY"};
	
	public ProfessorIntegrationModel() throws Exception{
		super(MODEL_DIR, MODEL_FILES, MODEL_ACCURACY);
	}
	
	@Override
	public String getFeatureQuery() {
		return ALL_FEATURES;
	}
	
	@Override
	protected int[] getFeatureModelMapping() {
		return FEATURE_MODEL_MAPPING;
	}
	
	@Override
	public int getKeyForPredicate(String key) {
		for (int i = 0; i < KEY2NAME.length; i++) {
			if (key.equals(KEY2NAME[i]))
				return i;
		}
		return -1;
	}

}
