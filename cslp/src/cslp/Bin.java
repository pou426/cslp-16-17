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
	
	public void dispose(){
		System.out.println("User disposes a waste bag");
		// create a Bag instance
		Bag b = new Bag();
		
	}

	public boolean isOverflow() {
		return (wasteVolume >= binVolume);
	}
	
	// check decimal places or data type 
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
}
