package ose.learning;


public class CameraIntegrationModel extends LogisticIntegrationModel{
	
//	
//	public static String featureQueryDomain = "" 
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) "
//		+ "%BooleanFeature(Token(someweirdstring)) ";
	
	public static String featureQueryDomain = "%BooleanFeature(Token(checkout)) "
		+ "%BooleanFeature(And(Token(buy),Token(now))) "
		+ "%BooleanFeature(And(Token(shopping),Token(cart))) "
		+ "%BooleanFeature(And(Token(sale,product),Token(price))) "
		+ "%BooleanFeature(Token(megapixel,megapixels,zoom) ) "
		+ "%BooleanFeature(Phrase(Token(digital), Token(camera, cameras) ) ) "
		+ "%BooleanFeature(Proximity(Number(),Token(megapixel,megapixels,mp),-3,2)) "
		+ "%BooleanFeature(Proximity(Number(),Phrase(Token(optical),Token(zoom)),-4,2)) ";

	public static String featureQueryRelevance = "%TFFeature(Token(BRAND)) "
		+ "%TFFeature(HTMLTitle(BRAND)) ";
//
//	public static String featureQueryConstraints =  
//	      "%BooleanFeature(Token(someweirdstring))"
//		+ "%BooleanFeature(Token(someweirdstring))"
//		+ "%BooleanFeature(Token(someweirdstring))";
//	
	public static String featureQueryConstraints =  
	      "%DivideBy(%CountNumber(Proximity(Number(MEGAPIXEL),Token(megapixel,megapixels,mp),-3,1)), "
				  + "%CountNumber(Proximity(Number(),Token(megapixel,megapixels,mp),-3,1))) "
		+ "%DivideBy(%CountNumber(Proximity(Number(ZOOM),Phrase(Token(optical),Token(zoom)),-4,3)), "
		          + "%CountNumber(Proximity(Number(),Phrase(Token(optical),Token(zoom)),-4,3))) "
		+ "%DivideBy(%CountNumber(Proximity(Number(PRICE),Token('$',usd,dollar,dollars),-2,1)), "
		          + "%CountNumber(Proximity(Number(),Token('$',usd,dollar,dollars),-2,1))) ";
	

	public static String ALL_FEATURES = featureQueryDomain + " " + featureQueryRelevance + " " + featureQueryConstraints;

	static private final String MODEL_DIR = "vldb1";
	
	private int [] FEATURE_MODEL_MAPPING = new int [] {
			0,0,0,0,0,0,0,0,
			1,1,
			2,
			3,
			4
			};

	private static final String [] MODEL_FILES = new String[] {"domain.model","brand.model","megapixel.model","zoom.model","price.model" };
	private static final double [] MODEL_ACCURACY = new double [] {      0.95,        0.95 ,            0.95 ,       0.95 ,       0.75 };
	private static final String [] KEY2NAME = new String []{"","BRAND", "MEGAPIXEL", "ZOOM", "PRICE"};
	
	public CameraIntegrationModel() throws Exception{
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
