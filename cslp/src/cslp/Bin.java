package cslp;

public class Bin {
	
	private static float binVolume;
	private float wasteVolume;
	private float wasteWeight;
	private short areaIdx;
	private int binIdx;
	private boolean isOverflow;
		
	public Bin(short areaIdx, int binIdx) {
		this.wasteVolume = 0;
		this.wasteWeight = 0;
		this.areaIdx = areaIdx;
		this.binIdx = binIdx;
		this.isOverflow = false;
	}
	
	// output event
	public void disposeBag(Event e) {
		// do something with simulator like add an event???
		Bag bag = new Bag();
		int currTime = e.getTime();
		float bagWeight = bag.getWeight();
		if (this.isOverflow()) {
			System.out.println("bin overflowed so no event generated anymore");
		} else {
			this.wasteVolume += Bag.getBagVolume();
			this.wasteWeight += bagWeight;
			String s = e.timeToString() + " -> " + "bag weighing "+bagWeight+" disposed of at bin "+areaIdx+"."+binIdx;
			System.out.println(s);
		}
	}
	
	public boolean isOverflow() {
		this.isOverflow = this.wasteVolume >= binVolume;
		return isOverflow;
	}
	
	// TODO check decimal places or data type 
	public double currentOccupancy() {
		return this.wasteVolume/binVolume;
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
