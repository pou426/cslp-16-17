package cslp;

/**
 * Abstract class AbstractEvent for all possible events that occur during the simulation.
 * Implements the Comparable interface to compare the times of occurrence of two events.
 * 
 */
public abstract class AbstractEvent implements Comparable {	
	
	private static float warmUpTime; // in second
	private static float stopTime;	// each event knows when the stopTime (in seconds) is 
	private int eventTime; // time (in seconds) in which the event happens during the simulation

	/**
	 * method to convert time into day:hour:min:sec format
	 * 
	 * @return String	the time in which current event happens in DD:HH:MM:SS format
	 */
	public String timeToString() {
		int secCount = eventTime;
		int minCount = 0;
		int hrCount = 0;
		int dayCount = 0;
		while (secCount >= 60) {
			secCount -= 60;
			minCount += 1;
		}
		while (minCount >= 60) {
			minCount -= 60;
			hrCount += 1;
		}
		while (hrCount >= 24) {
			hrCount -= 24;
			dayCount += 1;
		}
		String sec = String.format("%02d", secCount);
		String min = String.format("%02d", minCount);
		String hr = String.format("%02d", hrCount);
		String day = String.format("%02d", dayCount);
		String result = day+":"+hr+":"+min+":"+sec;
		return result;
	}
	
	/**
	 * Method called to execute the event during the simulation.
	 * 
	 * @param simulator
	 */
	public abstract void execute(Simulator simulator); 
	
	/**
	 * Compares the time variables in two events
	 * where time is the time during the simulation in which the event occurs.
	 * If the 'time' variable of the current Event instance is greater than the
	 * other, return 1. If they are equal, return 0. Otherwise, return -1.
	 * 
	 * @param Object	another event
	 * @return int 
	 */
	@Override
	public int compareTo(Object anotherEvent) {
		if (!(anotherEvent instanceof AbstractEvent)) {
			throw new ClassCastException("Error: An Event object expected.");
		}
		int anotherEventTime = ((AbstractEvent) anotherEvent).getTime();
		if (getTime() == anotherEventTime)		return 0;
		else if (getTime() > anotherEventTime)	return 1;
		else return -1;
	}
	public static float getWarmUpTime() {
		return AbstractEvent.warmUpTime;
	}
	public static void setWarmUpTime(float warmUpTime) {
		AbstractEvent.warmUpTime = warmUpTime;
	}
	public static float getStopTime() {
		return AbstractEvent.stopTime;
	}
	
	public static void setStopTime(float stopTime) {
		AbstractEvent.stopTime = stopTime;
	}
	
	public int getTime() {
		return eventTime;
	}
	
	public void setTime(int time) {
		this.eventTime = time;
	}
}
