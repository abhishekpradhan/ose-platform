package ose.learning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InstanceMap {
	private Map<Integer,Instance> instanceMap;
	private int numberOfFeatures = -1;
	
	public InstanceMap(){
		instanceMap = new HashMap<Integer, Instance>();
	}
	
	public Instance getInstance(int nth){
		return instanceMap.get(nth);
	}
	
	public int getNumberOfInstances(){
		return instanceMap.size();
	}
	
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}
	
	public void addInstance(int id, Instance inst){
		if (numberOfFeatures == -1){
			numberOfFeatures = inst.getNumberOfFeatures();
		}
		else if (numberOfFeatures != inst.getNumberOfFeatures()) {
			throw new RuntimeException("Number of feature does not match");
		}
		instanceMap.put(id, inst);
	}
	
	/**
	 * @param idArr
	 * @param i
	 */
	public void removeInstance(int i) {
		instanceMap.remove(i);
	}
	
	/**
	 * 
	 * @return InstanceSet in a sorted order of the key
	 */
	public InstanceSet toInstanceSet(){
		TreeSet<Integer> sortedKeys = new TreeSet<Integer>(instanceMap.keySet());
		InstanceSet instanceSet = new InstanceSet();
		for (Integer id : sortedKeys) {
			instanceSet.addInstance(instanceMap.get(id));
		}
		return instanceSet;
	}
}
