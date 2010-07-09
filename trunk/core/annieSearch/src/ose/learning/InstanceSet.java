/**
 * 
 */
package ose.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pham Kim Cuong
 *
 */
public class InstanceSet {
	private ArrayList<Instance> instanceList;
	private int numberOfFeatures = -1;
	
	public InstanceSet(){
		instanceList = new ArrayList<Instance>();
	}
	
	public Instance getInstance(int nth){
		if (nth >= 0 && nth < instanceList.size() ){
			return instanceList.get(nth);
		}
		else 
			return null;
	}
	
	public int getNumberOfInstances(){
		return instanceList.size();
	}
	
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}
	
	public void addInstance(Instance inst){
		if (numberOfFeatures == -1){
			numberOfFeatures = inst.getNumberOfFeatures();
		}
		else if (numberOfFeatures != inst.getNumberOfFeatures()) {
			throw new RuntimeException("Number of feature does not match");
		}
		instanceList.add(inst);
	}
	
	public void removeInstances(List<Integer> idList){
		Integer [] idArr = idList.toArray(new Integer[]{});
		//need to sort to preserve the correctness of each id
		Arrays.sort(idArr);
		for (int i = idArr.length-1; i >= 0 ; i--) {
			removeInstance(idArr[i]);
		}
	}

	/**
	 * @param idArr
	 * @param i
	 */
	public void removeInstance(int i) {
		instanceList.remove(i);
	}
}
