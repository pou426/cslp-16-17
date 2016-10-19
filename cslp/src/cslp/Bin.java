package cslp;

public class Bin {
	// should be an observer
	// and output events.

	protected static float binVolume;
	protected static float disposalDistrRate;
	protected static short disposalDistrShape;
	
	private float wasteVolume;
	
	// getters and setters FOR THE INSTANCES...
	public float getBinVolume() {
		return binVolume;
	}
	public float getDisposalDistrRate() {
		return disposalDistrRate;
	}
	public short getDisposalDistrShape() {
		return disposalDistrShape;
	}
	
	public Bin() {
		this.wasteVolume = 0;
	}
	
	// when initialised, use a static block... a while loop???
	// getTimeInterval, and when the clock is the right time, send that event out
	// and getTimeInterval again...
	public void getTimeInterval(){
		float erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			erlangk += (-1/disposalDistrRate)*(Math.log(Math.random()));
		}
	}

	public void disposeBag() {
		Bag b = new Bag();
		float bagWeight = b.getWeight();
		wasteVolume += Bag.bagVolume;
	}
	
	public boolean isOverflow() {
		return (wasteVolume >= binVolume);
	}
	
	// check decimal places or data type 
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
}
