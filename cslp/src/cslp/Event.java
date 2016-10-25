package cslp;

// this is a disposal event event class....
public class Event implements Comparable{
	
	private int duration;
	private float bagWeight;
	private short areaIdx;
	private int binIdx;
	
	public Event(int duration, float bagWeight, int binIdx, short areaIdx) {
		this.duration = duration;
		this.bagWeight = bagWeight;
		this.binIdx = binIdx;
		this.areaIdx = areaIdx;
	}
		
	public int getDuration() {
		return duration;
	}
	
	public String disposalEventToString() {
		String s = "Bag weighting " + bagWeight + "kg disposed of at bin " + areaIdx + "." + binIdx;
		return s;
	}
	
	// Events with shorter duration should have higher priority!! 
	// Events with shorter duration should go to the front of the queue
	@Override
	public int compareTo(Object anotherEvent) {
		// TODO Auto-generated method stub
		if (!(anotherEvent instanceof Event)) {
			throw new ClassCastException("An Event object expected.");
		}
		int anotherEventDuration = ((Event) anotherEvent).getDuration();
		if (this.duration == anotherEventDuration)		return 0;
		else if (this.duration < anotherEventDuration)	return 1;
		else return -1;
	}
	
}
