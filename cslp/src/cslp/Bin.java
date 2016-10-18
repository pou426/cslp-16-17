package cslp;

public class Bin {
	
	// bin volume (cubic meters)
	private static float binVolume;
	private static float disposalDistrRate;
	private static short disposalDistrShape;
	
	private static float wasteVolume;
	
	public Bin(float binVolume, float disposalDistrRate, short disposalDistrShape) {
		this.binVolume = binVolume;
		this.disposalDistrRate = disposalDistrRate;
		this.disposalDistrShape = disposalDistrShape;
		this.wasteVolume = 0;
	}
	
	public void dispose(){
		System.out.println("User disposes a waste bag");
	}

	public boolean isOverflow() {
		return (wasteVolume >= binVolume);
	}
	
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
}
