package cslp;

import java.util.logging.Logger;

public class Lorry {
	
	public static final Logger LOGGER = Logger.getLogger(Lorry.class.getName());

	private static short lorryVolume; 
	private static int lorryMaxLoad; 
	private static int binServiceTime; // unit : second
	
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
	
	private void outputString(Bin bin, String timeStr) {
		String binStatusString = timeStr+" -> load of bin "+bin.getAreaIdx()+"."+bin.getBinIdx()+" became "+String.format("%.3f",bin.currentWeight())+" kg and contents volume "+String.format("%.3f", bin.currentVol())+" m^3";
		if (!AbstractEvent.getIsExperiment()) 	System.out.println(binStatusString);// output change in bin content event
		else	LOGGER.info(binStatusString);
		
		String binEmptiedString = timeStr+" -> load of lorry "+sa.getAreaIdx()+" became "+String.format("%.3f",currentTrashWeight)+" kg and contents volume "+String.format("%.3f", currentTrashVolume)+" m^3";
		if (!AbstractEvent.getIsExperiment()) 	System.out.println(binEmptiedString);
		else 	LOGGER.info(binEmptiedString);
	}
	
	/**
	 * Empties the waste inside a bin. 
	 * Compress the waste volume to half its original volume
	 * 
	 * @param e		The current BinEmptiedEvent instance
	 */
	public void emptyBin(BinEmptiedEvent e) {
		Bin bin = e.getBin();
		this.currentTrashVolume += (bin.getWasteVolume())/2; // compressed volume
		this.currentTrashWeight += bin.getWasteWeight();
		bin.resetAll();

		outputString(bin, e.timeToString());
	}
	
	/**
	 * Empties the content of the lorry.
	 * 
	 * @param e		The current LorryEmptiedEvent instance. Once executed, lorry becomes empty.
	 */
	public void emptyLorry(LorryEmptiedEvent e) {
		if (location != 0) {
			LOGGER.severe("Should not reach this state.");
		}
		
		this.currentTrashVolume = 0;
		this.currentTrashWeight = 0;
		
		String lorryEmptiedString = e.timeToString()+" -> load of lorry "+sa.getAreaIdx()+" became "+String.format("%.3f",currentTrashWeight)+" kg and contents volume "+String.format("%.3f", currentTrashVolume)+" m^3";
		if (!AbstractEvent.getIsExperiment())	System.out.println(lorryEmptiedString);
		else 	LOGGER.info(lorryEmptiedString);

	}
	
	public void setLorryLocation(LorryArrivalEvent e) {
		this.location = e.getCurrLocation();
		String lorryArrivalString = e.timeToString()+" -> lorry "+sa.getAreaIdx()+" arrived at location "+sa.getAreaIdx()+"."+location;
		if (!AbstractEvent.getIsExperiment()) System.out.println(lorryArrivalString);
		else 	LOGGER.info(lorryArrivalString);
	}
	
	public void departLorry(LorryDepartureEvent e) {
		String lorryDepartureString = e.timeToString()+" -> lorry "+sa.getAreaIdx()+" left location "+sa.getAreaIdx()+"."+location;
		if (!AbstractEvent.getIsExperiment()) System.out.println(lorryDepartureString);				
		else 	LOGGER.info(lorryDepartureString);
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
