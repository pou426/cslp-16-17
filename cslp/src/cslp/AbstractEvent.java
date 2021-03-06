package cslp;

/**
 * Abstract class AbstractEvent for all possible events that will occur during the simulation.
 * Implements the Comparable interface to compare the times of occurrence of two events.
 * 
 */
public abstract class AbstractEvent implements Comparable {	
	
	private static float warmUpTime; // unit: second
	private static float stopTime;	// unit: second 
	private static boolean isExperiment; // no output if experiment 
	private int eventTime; // unit: second. The time during which this event happens.

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
		String result = String.format("%02d", dayCount)+":"+String.format("%02d", hrCount)+":"+String.format("%02d", minCount)+":"+String.format("%02d", secCount);
		return result;
	}
	
	/** 
	 * Schedule event time
	 * 
	 * @param eventTime
	 */
	public void schedule(int eventTime) {
		this.eventTime = eventTime;
	}
	
	/**
	 * Abtract method called to execute the event during the simulation.
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
		int anotherEventTime = ((AbstractEvent) anotherEvent).getEventTime();
		if (getEventTime() == anotherEventTime)		return 0;
		else if (getEventTime() > anotherEventTime)	return 1;
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
	public static boolean getIsExperiment() {
		return AbstractEvent.isExperiment;
	}
	public static void setIsExperiment(boolean isExperiment) {
		AbstractEvent.isExperiment = isExperiment;
	}	
	public int getEventTime() {
		return eventTime;
	}
	
}
