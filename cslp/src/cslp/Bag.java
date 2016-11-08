package cslp;

/**
 * Bag Class
 *
 */
public class Bag {

	private static float bagVolume;
	private static float bagWeightMin;
	private static float bagWeightMax;
	
	private float weight; // instance variable
	
	public Bag() {
		float w =  (float) (Math.random() * (bagWeightMax - bagWeightMin) + bagWeightMin);
		this.weight = w;
		//this.weight = (float) (Math.round(w*100.0)/100.0);
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
	/*
	public static float getBagWeightMin() {
		return bagWeightMin;
	}
	*/
	public static void setBagWeightMin(float bagWeightMin) {
		Bag.bagWeightMin = bagWeightMin;
	}
	/*
	public static float getBagWeightMax() {
		return bagWeightMax;
	}
	*/
	public static void setBagWeightMax(float bagWeightMax) {
		Bag.bagWeightMax = bagWeightMax;
	}

}
