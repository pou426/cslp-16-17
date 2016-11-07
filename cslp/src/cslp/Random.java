package cslp;

/**
 * Random class
 * 
 * @author home
 *
 */
public class Random {
	private static float disposalDistrRate; // expressed per hour
	private static short disposalDistrShape;

	/**
	 * Method to create Erlang K values for disposal event
	 * @return int		time for the next disposal event to happen
	 */
	public static int erlangk() {
		double erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			double rand = Math.random();
			double rate = -1/disposalDistrRate;
			double logged = Math.log(rand);
			double result = rate*logged;
			erlangk += result;
		}
		double convertToSec = erlangk*60*60; // conversion from hour to second
		int finalresult = (int) Math.round(convertToSec);
		return finalresult;
	}
	
	public static void setDisposalDistrRate(float disposalDistrRate) {
		Random.disposalDistrRate = disposalDistrRate;
	}
	
	public static void setDisposalDistrShape(short disposalDistrShape) {
		Random.disposalDistrShape = disposalDistrShape;
	}
}
