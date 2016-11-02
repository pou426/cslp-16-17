package cslp;

public class Lorry {
	/*
	 * Lorries are scheduled periodically at fixed intervals and 
	 * their daily frequency is given as input. Lorries have fixed 
	 * capacity both in terms of volume and weight and these values 
	 * are input parameters. On the other hand, upon service a lorry 
	 * compresses the contents of a bin to half its original volume. 
	 * We will consider a bin is serviced (emptied) in constant time 
	 * (expressed in seconds), irrespective of the load of a particular 
	 * bin being emptied. When at depot we will consider the time 
	 * required to empty a lorry is also fixed and this is five times 
	 * as long as the bin service time.
	 */
	private static short lorryVolume; 
	private static int lorryMaxLoad; 
	private static int binServiceTime;
	
	private int location;
	private float currentTrashVolume;
	private float currentTrashWeight;
	
	public Lorry() {
		this.currentTrashVolume = 0;
		this.location = 0; // lorry stations at the depot in the initial state
	}
	
	public void emptyBin(Bin bin) {
		this.currentTrashVolume += (Bin.getBinVolume())/2;
		this.currentTrashWeight += bin.getWasteWeight();
	}
	
	// getters and setters
	public static short getLorryVolume() {
		return lorryVolume;
	}
	public static int getLorryMaxLoad() {
		return lorryMaxLoad;
	}	
	public static int getBinServiceTime() {
		return binServiceTime;
	}
	public static void setLorryVolume(short lorryVolume) {
		Lorry.lorryVolume = lorryVolume;
	}	
	public static void setLorryMaxLoad(int lorryMaxLoad) {
		Lorry.lorryMaxLoad = lorryMaxLoad;
	}
	public static void setBinServiceTime(int binServiceTime) {
		Lorry.binServiceTime = binServiceTime;
	}
	
	
}
