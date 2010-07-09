package lbjse.features;


public class Suggestor {

	static public double information(Double prob){
		if (prob == 0 || prob == 1 || prob.equals(Double.NaN)) 
			return 0;
		else
			return - prob * Math.log(prob) - (1 - prob) * Math.log(1-prob);
	}

}
