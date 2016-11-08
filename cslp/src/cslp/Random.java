package cslp;

/**
 * Random class
 * 
 *
 */
public class Random {
	private static float disposalDistrRate; // expressed per hour
	private static short disposalDistrShape;

	/**
	 * Method to create Erlang K values for disposal event
	 * @return integer		time for the next disposal event to happen in second
	 */
	public static int erlangk() {
		double erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			double result = (-1/disposalDistrRate)*(Math.log(Math.random()));
			erlangk += result;
		}
		int erlangkSec = (int) Math.round(erlangk*60*60);	// convert to second
		return erlangkSec;
	}
	
	public static void setDisposalDistrRate(float disposalDistrRate) {
		Random.disposalDistrRate = disposalDistrRate;
	}
	
	public static void setDisposalDistrShape(short disposalDistrShape) {
		Random.disposalDistrShape = disposalDistrShape;
	}
}
