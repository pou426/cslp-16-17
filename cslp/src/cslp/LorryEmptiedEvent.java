package cslp;

public class LorryEmptiedEvent extends AbstractEvent {

	private ServiceArea sa;
	private Lorry lorry;
	private BinServiceEvent binServiceEvent;
	
	public LorryEmptiedEvent(int eventTime, ServiceArea sa, Lorry lorry, BinServiceEvent binServiceEvent) {
		schedule(eventTime);
		this.sa = sa;
		this.lorry = lorry;
		this.binServiceEvent = binServiceEvent;
	}

	@Override
	public void execute(Simulator simulator) {
		System.out.println("LOGGING INFO: [LorryEmptiedEvent] LorryEmptiedEvent executed for areaIdx : "+sa.getAreaIdx());
		System.out.println("Current lorry location should be 0, location = "+lorry.getLorryLocation());
		lorry.emptyLorry();
		if (sa.isDone()) {
			System.out.println("No more bins in queue. bin service event finishes.");
			System.out.println("current time : "+getEventTime()+" stop time: "+getStopTime());
			binServiceEvent.updateObserver(getEventTime()); 
		} else {
			System.out.println("There is still bins in queue, this should be a rescheduling event.. compute paths again!");
			binServiceEvent.reschedule();
		}
	}
}
