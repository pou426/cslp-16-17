package cslp;

/**
 * Bin Class
 * 
 * @author home
 *
 */
public class Bin {
	/*
	 * Overflow events can be tracked at most once between
	 * two bin service instances. 
	 * Occupancy of a bin may exceed capacity, when it becomes
	 * full after the disposal of one bag. For any bag this
	 * can happen at most once between two servcie instances
	 */
	private static float binVolume; 
	private float wasteVolume;
	private float wasteWeight;
	private short areaIdx;
	private int binIdx;
	private float thresholdVal;
	private boolean isOverflow; // = true when the bin is overflowed 
	private boolean isExceedThreshold; // = true when the waste content exceed threshold

	public Bin(short areaIdx, int binIdx, float thresholdVal) {
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.areaIdx = areaIdx;
		this.binIdx = binIdx;
		this.thresholdVal = thresholdVal;
		this.isOverflow = false;
		this.isExceedThreshold = false;
	}
	
	/**
	 * When a disposal event is executed, the contents and status of the bin are modified accordingly.
	 * This method outputs the event in a readable format, keeps track of and updates current status of the bin.
	 * 
	 * @param e
	 */
	public void disposeBag(DisposalEvent e) {
		if (isOverflow) {
			// if a bin is overflowed, disposal events cannot happen anymore.
			// System.out.println("disposal event occurs but bin has overflowed. bin content unchanged. time = "+e.timeToString());
		} else {
			Bag bag = new Bag(); // bag to be disposed in this bin
			float bagWeight = bag.getWeight();
			this.wasteVolume += Bag.getBagVolume();
			this.wasteWeight += bagWeight;
			// output disposal event
			String disposalString = e.timeToString() + " -> bag weighing "+String.format("%.3f",bagWeight)+" kg disposed of at bin "+areaIdx+"."+binIdx;
			System.out.println(disposalString);
			// output change in bin content event
			String binStatusString = e.timeToString() + " -> load of bin "+areaIdx+"."+binIdx+" became "+String.format("%.3f",wasteWeight)+" kg and contents volume "+String.format("%.3f", wasteVolume)+" m^3";
			System.out.println(binStatusString);
			if (currentOccupancy() > thresholdVal) { // updates isExceedThreshold variable if necessary
				isExceedThreshold = true;
				// output exceed threshold event
				String exceedThresholdString = e.timeToString() + " -> occupancy threshold of bin "+areaIdx+"."+binIdx+" exceeded";
				System.out.println(exceedThresholdString);
			}
			if (currentOccupancy() >= 1) { // updates isOverflow variable if necessary
				isOverflow = true;
				// output bin overflow event
				String overflowString = e.timeToString() + " -> "+ "bin "+areaIdx+"."+binIdx+" overflowed";
				System.out.println(overflowString);
			}
		}
	}
	
	public boolean isOverflow() {
		return isOverflow;
	}
	
	/**
	 * Method to calculate the current ratio of waste volume to the bin volume
	 * @return
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
}
