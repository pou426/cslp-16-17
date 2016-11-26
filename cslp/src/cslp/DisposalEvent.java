package cslp;

/**
 * DisposalEvent class
 * All disposal events occurring in a bin is an instance.
 * When initialized, the time parameter is the current time of simulation,
 * and the bin parameter is the bin in which this disposal event occurs.
 * 
 */
public class DisposalEvent extends AbstractEvent {

	private Bin bin;	// each disposal event belongs to a specific bin
	private Random random; 
	
	public DisposalEvent(int eventTime, Bin bin, Random random) {	
		schedule(eventTime);	// time in the simulation at which this disposal event occurs  
		this.bin = bin;
		this.random = random;
;	}
	
	/**
	 * Method called to execute this disposal event. 
	 * If maximum time of simulation not exceeded, the time is incremented according
	 * to the Erlang k distribution and is passed as a new DisposalEvent object to 
	 * the list of events (stored in a Priority Queue in the class Simulator).
	 * 
	 * @param simulator		a Simulator instance that executes this event
	 */
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			return;
		}
    	this.bin.disposeBag(this); 
    	int currTime = getEventTime(); // schedule next disposal event for this bin
    	int newTime = currTime + random.erlangk();
    	schedule(newTime);
    	if (getEventTime() < getStopTime()) {
    		simulator.insert(this);
    	}
    }
	
	/** For checking **/
	public String toString() {
		String s = "I am a DisposalEvent for time = "+getEventTime();
		s += " and areaIdx = "+bin.getAreaIdx()+" and binIdx = "+bin.getBinIdx();
		return s;
	}
}
