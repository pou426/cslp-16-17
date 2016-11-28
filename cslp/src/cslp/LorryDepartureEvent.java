package cslp;

public class LorryDepartureEvent extends AbstractEvent {

	private ServiceArea sa;
	private int departFrom;
	private int arriveAt;
	private BinServiceEvent binServiceEvent; // a pointer.. keeps track of bin servicing event? 
	
	public LorryDepartureEvent(int eventTime, int departFrom, int arriveAt, 
			ServiceArea sa, BinServiceEvent binServiceEvent) {
		schedule(eventTime);
		this.departFrom = departFrom;
		this.arriveAt = arriveAt;
		this.sa = sa;
		this.binServiceEvent = binServiceEvent;
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			System.out.println("STUPID!! LORRYDEPARTUREEVENT this should never execute u stupid stupid stupid");
			return; // actually should never get to this stage?? 
		}
		System.out.println("LOGGING INFO : Executing LorryDepartureEvent for areaIdx : "+sa.getAreaIdx());
		int timeToNextBin = sa.getTimeBetweenLocations(departFrom, arriveAt); // assuming always positive here...
		int arrivalTime = getEventTime() + timeToNextBin;
		System.out.println("current location : "+departFrom+" current destination : "+arriveAt);
		System.out.println("current time : "+getEventTime()+" arrival time : "+arrivalTime);
		if (arrivalTime < getStopTime()) {
			LorryArrivalEvent getThere = new LorryArrivalEvent(arrivalTime, arriveAt, sa, binServiceEvent);
			simulator.insert(getThere); // insert arrival event...
			System.out.println("LOGGING INFO : Inserted lorry arrival event for areaIdx : "+sa.getAreaIdx());
			System.out.println("arrival time : "+arrivalTime+" destination : "+arriveAt);
		}
	}
	
	
}
