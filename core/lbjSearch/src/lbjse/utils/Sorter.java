package lbjse.utils;

import java.util.ArrayList;
import java.util.Collections;

public class Sorter {
	private ArrayList<OrderPair> list;
	public Sorter() {
		list = new ArrayList<OrderPair>();
	}
	
	public void addPair(OrderPair pair){
		list.add(pair);
	}
	
	public ArrayList<OrderPair> getOrderedList() {
		Collections.sort(list);
		return list;
	}
}