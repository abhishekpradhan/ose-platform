package ose.database;

public class ModelInfo {
	private int modelId;
	private int fieldId;	
	private String path;
	private double weight;
	
	public ModelInfo(int modelId, int fieldId, String path, double weight){
		this.modelId = modelId;
		this.fieldId = fieldId;		
		this.path = path;
		this.weight = weight;
	}
	
	public int getFieldId() {
		return fieldId;
	}
	
	public int getModelId() {
		return modelId;
	}
	
	public String getPath() {
		return path;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "ModelInfo(" + modelId + "," + fieldId + "," + path + "," + weight + ")";
	}
}
