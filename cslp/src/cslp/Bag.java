package cslp;

public class Bag {

	private static float bagVolume; // cm^3
	private static float bagWeightMin;
	private static float bagWeightMax;
	
	private float weight; // instance variable
	
	public Bag() {
		this.weight = (float) (Math.random() * (bagWeightMax - bagWeightMin) + bagWeightMin);
	}
	
	public float getWeight() {
		return this.weight;
	}
	
	public static float getBagVolume() {
		return bagVolume;
	}

	public static void setBagVolume(float bagVolume) {
		Bag.bagVolume = bagVolume;
	}

	public static float getBagWeightMin() {
		return bagWeightMin;
	}

	public static void setBagWeightMin(float bagWeightMin) {
		Bag.bagWeightMin = bagWeightMin;
	}

	public static float getBagWeightMax() {
		return bagWeightMax;
	}

	public static void setBagWeightMax(float bagWeightMax) {
		Bag.bagWeightMax = bagWeightMax;
	}

}
