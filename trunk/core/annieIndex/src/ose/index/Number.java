package ose.index;

public class Number extends AnnotationBase {
	private double number;
	
	public Number(String span, int start, int end, String indexField) {
		super(span, start, end, indexField);
		number = toNumber(span);
	}
	
	static public double toNumber(String numberStr) {
		int lastDot = numberStr.lastIndexOf(".");
		int lastComma = numberStr.lastIndexOf(",");
		if (lastDot != -1 && lastDot >= numberStr.length() - 3) {
			try {
				return Double.parseDouble(numberStr.replaceAll("[^0-9.]", "") );
			} catch (NumberFormatException e) {
				return Double.NaN;
			}			
		} else if(lastComma != -1 && lastComma >= numberStr.length() - 3){
			numberStr = numberStr.replaceAll("[^0-9,]", "");
			numberStr = numberStr.replace(',', '.');
			try {
				return Double.parseDouble(numberStr);
			} catch (Exception ex) {
				return Double.NaN;
			}
		} else {
			numberStr = numberStr.replaceAll("[^0-9,]", "");
			try {
				return Double.parseDouble(numberStr.replaceAll("[^0-9.]", "") );
			} catch (NumberFormatException e) {
				return Double.NaN;
			}
		}		
	}
	
	public double getNumber() {
		return number;
	}
	
}
