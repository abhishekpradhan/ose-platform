/**
 * 
 */
package ose.weka;

import weka.core.Instance;

/**
 * @author Pham Kim Cuong
 *
 */
public class AnnieLogistic extends Logistic {

	/**
	 * 
	 */
	public AnnieLogistic() {
		// TODO Auto-generated constructor stub
	}
	
	public double [][] getModel(){
		return this.m_Par;
	}
	
	public Instance getPreprocessedInstance(Instance instance){
		m_ReplaceMissingValues.input(instance);
	    instance = m_ReplaceMissingValues.output();
	    m_NominalToBinary.input(instance);
	    instance = m_NominalToBinary.output();
	    return instance;
	}
	
	public void pruneModel(double absValue) {
		int nR = this.m_Par.length;
		int nK = this.m_Par[0].length;
		for (int i = 0 ; i < nR ; i++)
			for (int j = 0 ; j < nK ; j++){
				if (Math.abs(m_Par[i][j]) < absValue )
					m_Par[i][j] = 0;
			}
	}

}
