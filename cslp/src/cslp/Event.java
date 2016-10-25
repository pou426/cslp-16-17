package cslp;

public class Event implements Comparable{
	
	private int duration;
	private String eventName;
	
	public Event(int duration, String eventName) {
		this.duration = duration;
		this.eventName = eventName;
	}
		
	public int getDuration() {
		return duration;
	}
	
	public String getEventName() {
		// output event to a human readable format
		return eventName;
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
