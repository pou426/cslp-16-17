package cslp;

public class Clock {
	private double stopTime; // private?
	private double warmUpTime; // or public?
	
	public Clock(double stopTime, double warmUpTime) {
		this.stopTime = stopTime;
		this.warmUpTime = warmUpTime;
	}
}
