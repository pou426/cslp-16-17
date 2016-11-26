package cslp;

/**
 * Bin Class
 *
 */
public class Bin {
	
	private static float binVolume; 
	
	private ServiceArea serviceArea; // points to service area
	private short areaIdx;
	private int binIdx;
	private float thresholdVal;
	
	private float wasteVolume;
	private float wasteWeight;
	
	private boolean isOverflow; // flag for overflow event 
	private boolean isExceedThreshold; // flag for exceed threshold event

	public Bin(ServiceArea serviceArea, int binIdx) {
		this.serviceArea = serviceArea;
		this.areaIdx = serviceArea.getAreaIdx();
		this.binIdx = binIdx;
		this.thresholdVal = serviceArea.getThresholdVal();
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.isOverflow = false;
		this.isExceedThreshold = false;
	}
	
	public void printStatus() {
		System.out.println("wasteVolume: "+wasteVolume);
		System.out.println("wasteWeight: "+wasteWeight);
		System.out.println("isOverflow: "+isOverflow);
		System.out.println("isExceedThreshold: "+isExceedThreshold);
	}
	/**
	 * When a disposal event is executed, the contents and status of the bin are modified accordingly.
	 * This method outputs the event in a readable format, keeps track of and updates current status of the bin.
	 * 
	 * @param e		a disposal event
	 */
	public void disposeBag(DisposalEvent e) {
		Bag bag = new Bag();
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
	
	/**
	 * Check whether a bin is overflow
	 * 
	 * @return boolean		true is the ratio of waste volume to bin volume is greater than or equal to 1
	 */
	public boolean isOverflow() {
		return isOverflow;
	}
	/**
	 * Return the isOverflow flag to false when a bin has been serviced
	 */
	public void resetIsOverflow() {
		this.isOverflow = false;
	}
	
	public boolean isExceedThreshold() {
		return this.isExceedThreshold;
	}
	
	public void resetIsExceedThreshold() {
		this.isExceedThreshold = false;
	}
	/**
	 * Method to calculate the current ratio of waste volume to the bin volume
	 * 
	 * @return double		the current occupancy of the bin given as waste volume divided by the bin volume
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
