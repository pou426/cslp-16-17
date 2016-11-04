package cslp;

/**
 * DisposalEvent class
 * All disposal events occurring in a bin is an instance.
 * When initialized, the time parameter is the current time of simulation,
 * and the bin parameter is the bin in which this disposal event occurs.
 * 
 * @author home
 *
 */
public class DisposalEvent extends AbstractEvent {
	// link this event to the output list???
	// set this at constructor...
	// when execute.... 
	// set a string to describe disposal event
	// in the disposeBag in the bin, set the string to ... 
	// then append new event strings to the output list 
	
	private Bin bin;
	
	public DisposalEvent(int time, Bin bin) {
		setTime(time);
		this.bin = bin;
	}
	
	/**
	 * Method called to execute this disposal event. Disposal event occurs at the
	 * time given by the 'time' variable. 
	 * If maximum time of simulation not exceeded, the time is incremented according
	 * to the Erlang k distribution and is passed as a new DisposalEvent object to 
	 * the list of events (stored in a Priority Queue in the class Simulator).
	 */
	public void execute(Simulator simulator) {
    	this.bin.disposeBag(this); 
    	int currTime = getTime();
    	int newTime = currTime + Random.erlangk();
    	setTime(newTime);
    	if (getTime() < getStopTime()) {
    		simulator.insert(this);
    	}
    }
}
