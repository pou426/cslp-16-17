package cslp;

public class Event implements Comparable {
	
	private int duration;
	
	public Event(int duration) {
		this.duration = duration;
	}
		
	public int getDuration() {
		return duration;
	}
	
	public String toString() {
		// output event to a human readable format
		String s = "event output format.";
		return s;
	}
	
	@Override
	public int compareTo(Object anotherEvent) throws ClassCastException {
		// TODO Auto-generated method stub
		if (!(anotherEvent instanceof Event)) {
			throw new ClassCastException("An Event object expected.");
		}
		int anotherEventDuration = ((Event) anotherEvent).getDuration();
		return this.duration = anotherEventDuration;
	}
}
