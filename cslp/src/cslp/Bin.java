package cslp;

public class Bin {
	
	private static float binVolume;
	private float wasteVolume;
	private float wasteWeight;
	private short areaIdx;
	private int binIdx;
	private float thresholdVal;
	private boolean isOverflow;
	private boolean isExceedThreshold;

	
	public Bin(short areaIdx, int binIdx, float thresholdVal) {
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.areaIdx = areaIdx;
		this.binIdx = binIdx;
		this.thresholdVal = thresholdVal;
		this.isOverflow = false;
		this.isExceedThreshold = false;
	}
	
	// output event
	public void disposeBag(Event e) {
		// do something with simulator like add an event???
		Bag bag = new Bag();
		float bagWeight = bag.getWeight();
		if (isOverflow) {
			//System.out.println("bin overflowed so no event generated anymore");
		} else {
			this.wasteVolume += Bag.getBagVolume();
			this.wasteWeight += bagWeight;
			String disposalString = e.timeToString() + " -> bag weighing "+bagWeight+" disposed of at bin "+areaIdx+"."+binIdx;
			System.out.println(disposalString);
			String binStatusString = e.timeToString() + " -> load of bin "+areaIdx+"."+binIdx+" became "+wasteWeight+" and contents volume "+wasteVolume+" m^3";
			System.out.println(binStatusString);
			double currentOccupancy = wasteVolume/binVolume;
			if (currentOccupancy > thresholdVal) {
				isExceedThreshold = true;
				String exceedThresholdString = e.timeToString() + " -> occupancy threshold of bin "+areaIdx+"."+binIdx+" exceeded";
				System.out.println(exceedThresholdString);
			}
			if (wasteVolume >= binVolume) {
				isOverflow = true;
				String overflowString = e.timeToString() + " -> "+ "bin "+areaIdx+"."+binIdx+" overflowed";
				System.out.println(overflowString);
			}
		}
	}
	/*
	public boolean isOverflow() {
		this.isOverflow = this.wasteVolume >= binVolume;
		return isOverflow;
	}*/
	
	// TODO check decimal places or data type 
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
}
