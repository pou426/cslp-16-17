package cslp;

/**
 * Lorry class
 *
 */
public class Lorry {

	private static short lorryVolume; 
	private static int lorryMaxLoad; 
	private static int binServiceTime;
	
	private ServiceArea serviceArea; // points to service area it belongs
	private int location; // keeps track of lorry's location in the service area
	private float currentTrashVolume; 
	private float currentTrashWeight;
	
	public Lorry(ServiceArea serviceArea) {
		this.serviceArea = serviceArea;
		this.currentTrashVolume = 0;
		this.currentTrashWeight = 0;
		this.location = 0; // lorry stations at the depot in the initial state
	}
	
	/**
	 * Empties the waste inside a bin. 
	 * Compress the waste volume to half its original volume
	 * 
	 * @param bin	The Bin instance this lorry is servicing
	 */
	public void emptyBin(Bin bin) {
//		// check that lorry has enough capacity to hold the trash...
//		float binVol = bin.getWasteVolume()/2;
//		float binWeight = bin.getWasteWeight();
//		float remainingVolCapacity = lorryVolume - this.currentTrashVolume;
//		float remainingWeightCapacity = lorryMaxLoad - this.currentTrashWeight;
//		if (remainingVolCapacity < binVol) {
//			System.out.println("LOGGING INFO: lorry volume capacity exceeded, do not collect this bin.");
//			// reschedule it here
//			// enter code here..
//			// another lorrydeparture event????? or send a signal to the bin emtpied event??? 
//			return;
//		} 
//		if (remainingWeightCapacity < binWeight) {
//			System.out.println("LOGGING INFO: lorry weight capacity exceeded, do not collect this bin.");
//			// reschedule it here 
//			return;
//		}
		this.currentTrashVolume += (bin.getWasteVolume())/2;
		this.currentTrashWeight += bin.getWasteWeight();
		// should i check if exceeded capcity???????????????
		bin.resetIsOverflow();
		bin.resetIsExceedThreshold();
		bin.resetWasteVolume();
		bin.resetWasteWeight();
	}
	
	public void emptyLorry() {
		if (location != 0) {
			System.out.println("HOW THE HELL DID U GET HERE?");
		}
		this.currentTrashVolume = 0;
		this.currentTrashWeight = 0;
	}
	
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
	public int getLorryLocation() {
		return this.location;
	}
	public void setLorryLocation(int location) {
		this.location = location;
	}
	public float getCurrentTrashVolume() {
		return this.currentTrashVolume;
	}
	public float getCurrentTrashWeight() {
		return this.currentTrashWeight;
	}
}
