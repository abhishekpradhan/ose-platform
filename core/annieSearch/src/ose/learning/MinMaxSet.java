package ose.learning;

public class MinMaxSet {
	double [] minSet;
	double [] maxSet;
	
	public MinMaxSet(int N) {
		minSet = new double [N];
		maxSet = new double [N];
		for (int i = 0; i < N; i++) {
			minSet[i] = 1e+10;
			maxSet[i] = -1e+10;
		}
	}
	
	public void addInstance(VectorInstance inst){
		if (inst.getNumberOfFeatures() != minSet.length){
			throw new RuntimeException("Incompatible set");
		}
		for (int i = 0; i < inst.getNumberOfFeatures(); i++) {
			double d = inst.getFeature(i).toNumber() ;
			if ( d < minSet[i] ) minSet[i] = d;
			if ( d > maxSet[i] ) maxSet[i] = d;
		}		
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < minSet.length; i++) {
			buffer.append("\t[ " + minSet[i] + "  " + maxSet[i] + " ]");
		}
		return buffer.toString();
	}
}
