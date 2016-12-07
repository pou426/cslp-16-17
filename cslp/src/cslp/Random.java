package cslp;

/**
 * Random class
 * 
 */
public class Random {
	private static float disposalDistrRate; // expressed per hour
	private static short disposalDistrShape;

//	public Random(float disposalDistrRate, short disposalDistrShape) {
//		this.disposalDistrRate = disposalDistrRate;
//		this.disposalDistrShape = disposalDistrShape;
//	}
	/**
	 * Method to create Erlang K values for disposal event
	 * 
	 * @return int		time for the next disposal event to happen in second
	 */
	public static int erlangk() {
		double erlangk = 0;
		for (int i = 0; i < Random.disposalDistrShape; i++) {
			double result = (-1/Random.disposalDistrRate)*(Math.log(Math.random()));
			erlangk += result;
		}
		int erlangkSec = (int) Math.round(erlangk*60*60);	// convert to second
		return erlangkSec;
	}
	
	/** For checking **/
	public static double meanErlangK() {
		return 60*60*Random.disposalDistrShape/Random.disposalDistrRate;
	}

	public static void setDisposalDistrRate(float disposalDistrRate) {
		Random.disposalDistrRate = disposalDistrRate;
	}

	public static void setDisposalDistrShape(short disposalDistrShape) {
		Random.disposalDistrShape = disposalDistrShape;
	}
	
}
