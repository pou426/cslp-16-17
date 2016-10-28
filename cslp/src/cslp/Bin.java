package cslp;

import java.util.ArrayList;

// send events?
// should be an observer
public class Bin {
	
	private static float binVolume;
	private static float disposalDistrRate; // expressed per hour
	private static short disposalDistrShape;
	private float wasteVolume;
	private short areaIdx;
	private int binIdx;
	
	private ArrayList<Event> disposalEventList = new ArrayList<Event>();
	
	public Bin(short areaIdx, int binIdx) {
		this.wasteVolume = 0;
		this.areaIdx = areaIdx;
		this.binIdx = binIdx;
	}
	
	private int getTimeInterval(){
		double erlangk = 0;
		for (int i = 0; i < disposalDistrShape; i++) {
			double rand = Math.random();
			double rate = -1/disposalDistrRate;
			double logged = Math.log10(rand);
			double result = rate*logged;
			erlangk += result;
		}
		double convertToSec = erlangk*60*60;
		int finalresult = (int) Math.round(convertToSec);
		return finalresult;
	}
	
	private Event disposeBag() {
		Bag b = new Bag();
		float bagWeight = b.getWeight();
		wasteVolume += Bag.getBagVolume();
		int timeInterval = Math.round(this.getTimeInterval());
		Event e = new Event(timeInterval, bagWeight, binIdx, areaIdx);
		return e;
	}

	public void createDisposalEventList(float stopTime){
		int currTime = 0;
		int duration = 0;
		int stopTimeInt = (int) Math.round(stopTime);
		// TODO check if currTIme <= stopTime or just < ????
		while (currTime <= stopTimeInt) {
			Event e = this.disposeBag();
			disposalEventList.add(e);
			duration += e.getDuration();
			currTime += duration;
		}
	}
	
	public boolean isOverflow() {
		return (wasteVolume >= binVolume);
	}
	
	// check decimal places or data type 
	public double currentOccupancy() {
		return wasteVolume/binVolume;
	}
	
	// getters and setters FOR THE INSTANCES...
	public static float getBinVolume() {
		return binVolume;
	}
	public static float getDisposalDistrRate() {
		return disposalDistrRate;
	}
	public static short getDisposalDistrShape() {
		return disposalDistrShape;
	}
	public static void setBinVolume(float binVolume) {
		Bin.binVolume = binVolume;
	}
	public static void setDisposalDistrRate(float disposalDistrRate) {
		Bin.disposalDistrRate = disposalDistrRate;
	}
	public static void setDisposalDistrShape(short disposalDistrShape) {
		Bin.disposalDistrShape = disposalDistrShape;
	}
	public ArrayList<Event> getDisposalEventList() {
		return disposalEventList;
	}
}
