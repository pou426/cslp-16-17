package cslp;

public class Lorry {
	/**
	 * Lorries are scheduled periodically at fixed intervals and their 
	 * daily frequency is given as input. Lorries have fixed capacity 
	 * both in terms of volume and weight and these values are input 
	 * parameters. On the other hand, upon service a lorry compresses 
	 * the contents of a bin to half its original volume. We will 
	 * consider a bin is serviced (emptied) in constant time (expressed 
	 * in seconds), irrespective of the load of a particular bin being 
	 * emptied. When at depot we will consider the time required to 
	 * empty a lorry is also fixed and this is five times as long as 
	 * the bin service time.
	 */
	
	// total waste volume a lorry can accommodate (cubic metres)
	public static int lorryVolume; // unit8_t
	// Maximum lorry load (kg)
	public static int lorryMaxLoad; // unit16_t
	// time required to empty a bin (in seconds)
	public static double binServiceTime;
	
	public Lorry(int lorryVolume, int lorryMaxLoad, double binServiceTime) {
		Lorry.lorryVolume = lorryVolume;
		Lorry.lorryMaxLoad = lorryMaxLoad;
		Lorry.binServiceTime = binServiceTime;
	}
}
