package lbjse.utils;

public class OrderPair implements Comparable{
	private Comparable<Object> value;
	private int lid;
	public OrderPair(Comparable value, int lid) {
		this.value = value;
		this.lid = lid;
	}
	
	public int compareTo(Object o) {
		if (o instanceof OrderPair) {
			OrderPair other = (OrderPair) o;
			return value.compareTo(other.value);
		}
		return 0;
	}
	
	public int getId() {
		return lid;
	}
	
	public Comparable getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "id:" + lid + ",value:"+value;
	}
}