package cslp;

import org.junit.Test;

public class Event implements Comparable {

	private static float stopTime;
	public static void setStopTime(float stopTime) {
		Event.stopTime = stopTime;
	}

	private int time;
	public int getTime() {
		return time;
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
	public Event(int time) {
		this.time = time;
	}
	
	
	
	
	// disposal event
	private Bin bin;
	public Event(int time, Bin bin) {
		this.time = time;
		this.bin = bin;
	}
	public void execute(Simulator simulator) {
    	this.bin.disposeBag(this); 
    	this.time += Random.erlangk();
    	if (this.time < stopTime) {
    		simulator.insert(this);
    	}
    }

	@Override
	public int compareTo(Object anotherEvent) {
		// TODO Auto-generated method stub
		if (!(anotherEvent instanceof Event)) {
			throw new ClassCastException("An Event object expected.");
		}
		int anotherEventTime = ((Event) anotherEvent).getTime();
		if (this.time == anotherEventTime)		return 0;
		else if (this.time > anotherEventTime)	return 1;
		else return -1;
	}

}
