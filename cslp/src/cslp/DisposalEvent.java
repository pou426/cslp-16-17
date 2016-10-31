package cslp;

public class DisposalEvent extends AbstractEvent {

	// disposal event
	private Bin bin;
	public DisposalEvent(int time, Bin bin) {
		setTime(time);
		this.bin = bin;
	}
	
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
