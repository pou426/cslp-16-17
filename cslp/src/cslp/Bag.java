package cslp;

/**
 * Bag Class
 *
 */
public class Bag {

	private static float bagVolume;
	private static float bagWeightMin;
	private static float bagWeightMax;
	
//	private float weight;
	
//	public Bag() {
//		// assign random weight to bag
////		float w =  (float) (Math.random() * (bagWeightMax - bagWeightMin) + bagWeightMin); 
////		this.weight = w;
//	}
	
	public float getWeight() {
		float weight =  (float) (Math.random() * (bagWeightMax - bagWeightMin) + bagWeightMin); 
		return weight;
	}
//	
//	/**
//	 * Method to assign weight to a Bag instance
//	 */
//	public void setWeight() {
//		float w =  (float) (Math.random() * (bagWeightMax - bagWeightMin) + bagWeightMin); 
//		this.weight = w;
//	}
//	
//	public float getWeight() {
//		return this.weight;
//	}
	
	public static float getBagVolume() {
		return bagVolume;
	}

	public static void setBagVolume(float bagVolume) {
		Bag.bagVolume = bagVolume;
	}
	
	public static void setBagWeightMin(float bagWeightMin) {
		Bag.bagWeightMin = bagWeightMin;
	}

	public static void setBagWeightMax(float bagWeightMax) {
		Bag.bagWeightMax = bagWeightMax;
	}
	
	public static float getBagWeightMin() {
		return Bag.bagWeightMin;
	}

	public static float getBagWeightMax() {
		return Bag.bagWeightMax;
	}
}
