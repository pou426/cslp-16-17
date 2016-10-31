package cslp;

public abstract class AbstractEvent implements Comparable {
	private static float stopTime;
	private int time; // seconds

	// getters and setters
	public static float getStopTime() {
		return AbstractEvent.stopTime;
	}
	public static void setStopTime(float stopTime) {
		AbstractEvent.stopTime = stopTime;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	public String timeToString() {
		int secCount = time;
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
	
	public abstract void execute(Simulator simulator);
	
	@Override
	public int compareTo(Object anotherEvent) {
		// TODO Auto-generated method stub
		if (!(anotherEvent instanceof AbstractEvent)) {
			throw new ClassCastException("An Event object expected.");
		}
		int anotherEventTime = ((AbstractEvent) anotherEvent).getTime();
		if (getTime() == anotherEventTime)		return 0;
		else if (getTime() > anotherEventTime)	return 1;
		else return -1;
	}

}
