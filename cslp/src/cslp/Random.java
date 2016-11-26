package cslp;

/**
 * Random class
 * 
 */
public class Random {
	private float disposalDistrRate; // expressed per hour
	private short disposalDistrShape;

	public Random(float disposalDistrRate, short disposalDistrShape) {
		this.disposalDistrRate = disposalDistrRate;
		this.disposalDistrShape = disposalDistrShape;
	}
	/**
	 * Method to create Erlang K values for disposal event
	 * 
	 * @return int		time for the next disposal event to happen in second
	 */
	public int erlangk() {
		double erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			double result = (-1/disposalDistrRate)*(Math.log(Math.random()));
			erlangk += result;
		}
		int erlangkSec = (int) Math.round(erlangk*60*60);	// convert to second
		return erlangkSec;
	}
	
	/** For checking **/
	public double meanErlangK() {
		return 60*60*disposalDistrShape/disposalDistrRate;
	}
}
