package cslp;

public class Bag {
	
	protected static float bagVolume;
	protected static float bagWeightMin;
	protected static float bagWeightMax;
	
	private float weight;
	
	public Bag() {
		this.weight = (float) (Math.random() * (bagWeightMin - bagWeightMax) + bagWeightMin);
	}
	
	public float getWeight() {
		return weight;
	}
}
