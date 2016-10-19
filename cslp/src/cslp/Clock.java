package cslp;

public class Clock {
	
	private int currTime;
	
	public Clock() {
		this.currTime = 0;
	}
	
	// output time in the required format
	public String toString() {
		return "s";
	}
	
	public void setTime(int delay) {
		this.currTime += delay;
	}
	
	public int getTime() {
		return currTime;
	}
	
}
