package cslp;

/**
 * Bin Class
 *
 */
public class Bin {
	/*
	 * Overflow events can be tracked at most once between
	 * two bin service instances. 
	 * Occupancy of a bin may exceed capacity, when it becomes
	 * full after the disposal of one bag. For any bag this
	 * can happen at most once between two service instances
	 */
	private static float binVolume; 
	
	private short areaIdx;
	private int binIdx;
	private float thresholdVal;
	private float wasteVolume;
	private float wasteWeight;
	private boolean isOverflow; // = true when the bin is overflowed 
	private boolean isExceedThreshold; // = true when the waste content exceed threshold

	public Bin(short areaIdx, int binIdx, float thresholdVal) {
		this.areaIdx = areaIdx;
		this.binIdx = binIdx;
		this.thresholdVal = thresholdVal;
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.isOverflow = false;
		this.isExceedThreshold = false;
	}
	
	/**
	 * When a disposal event is executed, the contents and status of the bin are modified accordingly.
	 * This method outputs the event in a readable format, keeps track of and updates current status of the bin.
	 * 
	 * @param e		a disposal event
	 */
	public void disposeBag(DisposalEvent e) {
		Bag bag = new Bag(); 	// bag to be disposed in this bin
		float bagWeight = bag.getWeight();
		if (isOverflow) {	// if bin already overflowed, do not update its content, just output the disposal event
			String disposalString = e.timeToString() + " -> bag weighing "+String.format("%.3f",bagWeight)+" kg disposed of at bin "+areaIdx+"."+binIdx;
			System.out.println(disposalString);				// output disposal event
		} else {
			this.wasteVolume += Bag.getBagVolume();
			this.wasteWeight += bagWeight;
			
			String disposalString = e.timeToString() + " -> bag weighing "+String.format("%.3f",bagWeight)+" kg disposed of at bin "+areaIdx+"."+binIdx;
			System.out.println(disposalString);				// output disposal event
			
			String binStatusString = e.timeToString() + " -> load of bin "+areaIdx+"."+binIdx+" became "+String.format("%.3f",wasteWeight)+" kg and contents volume "+String.format("%.3f", wasteVolume)+" m^3";
			System.out.println(binStatusString);			// output change in bin content event
			
			if (!isExceedThreshold) {	// only outputs exceed thresholdVal event if this is first occurence between bin service
				if (currentOccupancy() > thresholdVal) { 		// updates isExceedThreshold variable if necessary
					isExceedThreshold = true;
					String exceedThresholdString = e.timeToString() + " -> occupancy threshold of bin "+areaIdx+"."+binIdx+" exceeded";
					System.out.println(exceedThresholdString);	// output exceed threshold event
				}
			}
			
			if (currentOccupancy() >= 1) { 	// updates isOverflow variable if necessary			
				isOverflow = true;
				String overflowString = e.timeToString() + " -> "+ "bin "+areaIdx+"."+binIdx+" overflowed";
				System.out.println(overflowString);			// output bin overflow event
			}
		}
	}
	
	public boolean isOverflow() {
		return isOverflow;
	}
	public void resetIsOverflow() {
		this.isOverflow = false;
	}
	
	/**
	 * Method to calculate the current ratio of waste volume to the bin volume
	 * @return		the current occupancy of the bin given as waste volume divided by the bin volume
	 */
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
	
	public double currentWeight() {
		return this.wasteWeight;
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
	public void resetWasteWeight() {
		this.wasteWeight = 0;
	}
	public float getWasteVolume() {
		return this.wasteVolume;
	}
	public void resetWasteVolume() {
		this.wasteVolume = 0;
	}
}
