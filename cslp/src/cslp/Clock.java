package cslp;

// redundant!!!!!!
public class Clock {
	// does this have to be singleton?
	// does this require a static block?
	
	protected int currTime;
	protected static float stopTime; // short or int or float?
	protected static float warmUpTime; // short or int or float?
	
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
