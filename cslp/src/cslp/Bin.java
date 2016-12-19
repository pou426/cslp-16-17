package cslp;

import java.util.logging.Logger;

/**
 * Bin Class
 *
 */
public class Bin {
	
	public static final Logger LOGGER = Logger.getLogger(Bin.class.getName());

	private static float binVolume; 
	
	private short areaIdx; 
	private int binIdx;	   // binIdx starts from 1, equiv. to location in roadsLayout matrix
	private float thresholdVal;
	
	private Bag bag; // each Bin has one bag instance to save memory
	private float wasteVolume;
	private float wasteWeight;
	
	private boolean isOverflow; // flag for overflow event 
	private boolean isExceedThreshold; // flag for exceed threshold event
	private boolean isServicing; // if servicing, cannot dispose bags

	public Bin(ServiceArea serviceArea, int binIdx) {
		this.areaIdx = serviceArea.getAreaIdx();
		this.binIdx = binIdx;
		this.thresholdVal = serviceArea.getThresholdVal();
		this.bag = new Bag();
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.isOverflow = false;
		this.isExceedThreshold = false;
		this.isServicing = false;
//		Bin.LOGGER.setLevel(Level.OFF);
	}
	
	/**
	 * When a disposal event is executed, the contents and status of the bin are modified accordingly.
	 * This method outputs the event in a readable format, keeps track of and updates current status of the bin.
	 * 
	 * @param e		a disposal event
	 */
	public void disposeBag(DisposalEvent e) {
		float bagWeight = bag.getWeight();
		
		if (isOverflow || isServicing) {	// if servicing event is happening or if bin already overflowed, do not update its content, just output the disposal event
			String disposalString = e.timeToString() + " -> bag weighing "+String.format("%.3f",bagWeight)+" kg disposed of at bin "+areaIdx+"."+binIdx;
			if (!AbstractEvent.getIsExperiment()) 	System.out.println(disposalString);				// output disposal event
			else	LOGGER.info(disposalString);
		} 
		
		else {
			this.wasteVolume += Bag.getBagVolume(); // update bin content
			this.wasteWeight += bagWeight;
			
			String disposalString = e.timeToString() + " -> bag weighing "+String.format("%.3f",bagWeight)+" kg disposed of at bin "+areaIdx+"."+binIdx;
			if (!AbstractEvent.getIsExperiment()) 	System.out.println(disposalString);				// output disposal event
			else	LOGGER.info(disposalString);
			
			String binStatusString = e.timeToString() + " -> load of bin "+areaIdx+"."+binIdx+" became "+String.format("%.3f",wasteWeight)+" kg and contents volume "+String.format("%.3f", wasteVolume)+" m^3";
			if (!AbstractEvent.getIsExperiment()) 	System.out.println(binStatusString);			// output change in bin content event
			else	LOGGER.info(binStatusString);
			
			if (!isExceedThreshold) {	// only outputs exceed thresholdVal event if this is first occurence between bin service
				if (currentOccupancy() > thresholdVal) { 		// updates isExceedThreshold variable if necessary
					isExceedThreshold = true;
					String exceedThresholdString = e.timeToString() + " -> occupancy threshold of bin "+areaIdx+"."+binIdx+" exceeded";
					if (!AbstractEvent.getIsExperiment()) 	System.out.println(exceedThresholdString);	// output exceed threshold event
					else	LOGGER.info(exceedThresholdString);
				}
			}
			
			if (currentOccupancy() >= 1) { 	// updates isOverflow variable if necessary			
				isOverflow = true;
				String overflowString = e.timeToString() + " -> "+ "bin "+areaIdx+"."+binIdx+" overflowed";
				if (!AbstractEvent.getIsExperiment()) System.out.println(overflowString);			// output bin overflow event
				else	LOGGER.info(overflowString);
			}
		}
	}
	
	public boolean isOverflow() {
		return isOverflow;
	}

	public boolean isExceedThreshold() {
		return this.isExceedThreshold;
	}
	
	public boolean isServicing() {
		return this.isServicing;
	}
	public void setIsServicing() {
		this.isServicing = true;
	}
	
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
	
	public double currentWeight() {
		return this.wasteWeight;
	}
	public double currentVol() {
		return this.wasteVolume;
	}
	
	public short getAreaIdx() {
		return this.areaIdx;
	}
	public int getBinIdx() {
		return this.binIdx;
	}
	
	public static float getBinVolume() {
		return binVolume;
	}
	public static void setBinVolume(float binVolume) {
		Bin.binVolume = binVolume;
	}
	public float getWasteWeight() {
		return this.wasteWeight;
	}
	public float getWasteVolume() {
		return this.wasteVolume;
	}
	
	public void resetAll() {
		this.isOverflow = false;
		this.isExceedThreshold = false;
		this.isServicing = false;
		this.wasteVolume = 0;
		this.wasteWeight = 0;
	}
}
