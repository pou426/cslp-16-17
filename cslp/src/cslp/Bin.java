package cslp;

public class Bin {
	/**
	 * The Bin class incorporates the User class.
	 * ********************************************************************
	 * Bin
	 * ********************************************************************
	 * All bins has a fixed volume that is specified as an input parameter.
	 * We assume bins do not have maximum weight limitations, but we 
	 * consider such limits on the waste bags disposed (as above). We 
	 * consider bins are equipped with sensors that indicate their current 
	 * occupancy as a fraction of their maximum volume. Sensors also track 
	 * the weight of the current contents. In addition, you should account 
	 * for the event that a bin has ’overflowed’. Since the system relies 
	 * on bin sensors, such events can only be tracked at most once between
	 * two bin service instances. It is also acceptable to assume the 
	 * occupancy of a bin may exceed capacity, when it becomes full after 
	 * the disposal of one bag. Again, for any bin this can happen at most 
	 * once between two service instances.
	 * ********************************************************************
	 * Uesr
	 * ********************************************************************
	 * At any bin, we consider users dispose of waste bags at time intervals 
	 * that follow an Erlang-k distribution with the rate and shape given as 
	 * an input parameters.
	 */
	
	// bin volume (cubic meters)
	public static double binVolume;
	public static double wasteVolume;
	public static double binWeight;
	// Rate of the Erlang distribution of the disposal events (avg. no. per hour)
	private static double disposalDistrRate;
	// Shape of the Erlang distribution
	private static int disposalDistrShape; // unit8_t

	public Bin(double volume, double disposalDistrRate, int disposalDistrShape) {
		Bin.binVolume = volume;
		Bin.wasteVolume = 0;
		Bin.binWeight = 0;
		Bin.disposalDistrRate = disposalDistrRate;
		Bin.disposalDistrShape = disposalDistrShape;
	}
	
	public void dispose(){
		System.out.println("User disposes a waste bag");
	}

	public boolean isOverflow() {
		return (wasteVolume >= binVolume);
	}
	
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
}
