package cslp;

import java.util.logging.Logger;

/**
 * Lorry class
 *
 */
public class Lorry {
	
	private static final Logger LOGGER = Logger.getLogger(Lorry.class.getName());

	private static short lorryVolume; 
	private static int lorryMaxLoad; 
	private static int binServiceTime; // change to become service area's attribute
	
	private ServiceArea sa;
	private int location; // keeps track of lorry's location in the service area
	private float currentTrashVolume; 
	private float currentTrashWeight;
	
	public Lorry(ServiceArea sa) {
		this.currentTrashVolume = 0;
		this.currentTrashWeight = 0;
		this.sa = sa;
		this.location = 0; // lorry stations at the depot in the initial state
	}
	
	/**
	 * Empties the waste inside a bin. 
	 * Compress the waste volume to half its original volume
	 * 
	 * @param bin	The Bin instance this lorry is servicing
	 */
	public void emptyBin(BinEmptiedEvent e) {
		Bin bin = e.getBin();
		this.currentTrashVolume += (bin.getWasteVolume())/2;
		this.currentTrashWeight += bin.getWasteWeight();
		bin.resetIsOverflow();
		bin.resetIsExceedThreshold();
		bin.resetIsServicing(); // bin not being serviced anymore...
		bin.resetWasteVolume();
		bin.resetWasteWeight();
		String binStatusString = e.timeToString() + " -> load of bin "+bin.getAreaIdx()+"."+bin.getBinIdx()+" became "+String.format("%.3f",bin.currentWeight())+" kg and contents volume "+String.format("%.3f", bin.currentVol())+" m^3";
		if (!AbstractEvent.getIsExperiment()) System.out.println(binStatusString);// output change in bin content event
		String binEmptiedString = e.timeToString()+" -> load of lorry "+sa.getAreaIdx()+" became "+String.format("%.3f",currentTrashWeight)+" kg and contents volume "+String.format("%.3f", currentTrashVolume)+" m^3";
		if (!AbstractEvent.getIsExperiment()) System.out.println(binEmptiedString);
	}
	
	public void emptyLorry(LorryEmptiedEvent e) {
		if (location != 0) {
			LOGGER.severe("Should not reach this state.");
		}
		this.currentTrashVolume = 0;
		this.currentTrashWeight = 0;
		String lorryEmptiedString = e.timeToString()+" -> load of lorry "+sa.getAreaIdx()+" became "+String.format("%.3f",currentTrashWeight)+" kg and contents volume "+String.format("%.3f", currentTrashVolume)+" m^3";
		if (!AbstractEvent.getIsExperiment()) System.out.println(lorryEmptiedString);

	}
	
	public void setLorryLocation(LorryArrivalEvent e) {
		this.location = e.getCurrLocation();
		String lorryArrivalString = e.timeToString()+" -> lorry "+sa.getAreaIdx()+" arrived at location "+sa.getAreaIdx()+"."+location;
		if (!AbstractEvent.getIsExperiment()) System.out.println(lorryArrivalString);				// output disposal event
	}
	
	public void departLorry(LorryDepartureEvent e) {
		String lorryDepartureString = e.timeToString()+" -> lorry "+sa.getAreaIdx()+" left location "+sa.getAreaIdx()+"."+location;
		if (!AbstractEvent.getIsExperiment()) System.out.println(lorryDepartureString);				// output disposal event
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
	public float getCurrentTrashVolume() {
		return this.currentTrashVolume;
	}
	public float getCurrentTrashWeight() {
		return this.currentTrashWeight;
	}
}
