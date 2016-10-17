package cslp;

public class Bag {
	/**
	 * We assume a big is of fixed volume (in cubic meters), given as input.
	 * A bag's weight (in kilograms) is a random value, uniformly distributed
	 * between a lower and an upper bound, which are also input parameters.
	 */
	
	// Bag volume (cubic meters)
	public static double volume;
	// Bag weight (kg), 3 d.p.
	public static double weight;
	
	public Bag(double volume, double weightMin, double weightMax) {
		Bag.volume = volume;
		Bag.weight = (weightMax-weightMin)*Math.random() + weightMin;
	}
}
