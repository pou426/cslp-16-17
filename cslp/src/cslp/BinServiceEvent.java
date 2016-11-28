package cslp;


/**
 * Service area service bin event.... 
 *
 */

// becomes an observer here
// observes all lorry departure and arrival events... to keep track???
public class BinServiceEvent extends AbstractEvent {

	private ServiceArea sa; 
	private Simulator simulator;
	
	private int nextEventTime; // time for next bin servicing event
	private BinServiceEvent nextEvent; // this will keep scheduling new BinServiceEvent 
	
	public BinServiceEvent(int eventTime, ServiceArea sa) {	
		schedule(eventTime);	// time in the simulation at which this disposal event occurs  
		this.sa = sa;
		this.nextEventTime = eventTime + sa.getServiceInterval(); // current event time + service Interval 
		System.out.println("LOGGING INFO: ---------------- CREATE NEW BIN SERVICE EVENT -------------");
		System.out.println("LOGGING INFO: for areaIdx : "+sa.getAreaIdx()+" for time at : "+timeToString()+" and next time at : "+nextEventTime);
		if (nextEventTime < getStopTime()) {
			this.nextEvent = new BinServiceEvent(nextEventTime, sa);
		} else {
			System.out.println("LOGGING INFO: --------------------- FINISH LOGGING ----------------------");
		}
	}
	
	public void reschedule() {
		System.out.println("LOGGING INFO: This is a rescdeduling...");
//		sa.computePath(); // ??????
		System.out.println("current priority queue service queue size : "+sa.getPQsize());
		//this.execute(simulator); // execute again.... currently commented out because this will cause a infinite loop (because of  naive comput path...)
	}
	
	public void updateObserver(int finishTime) {
		// i am an observer 
		// insert next bin service event with any delay. 
		System.out.println("LOGGING INFO: updated observer for bin service event for areaIdx : "+sa.getAreaIdx());
		System.out.println("original next bin service event time is : "+nextEventTime);
		if (finishTime > nextEventTime) { // if delayed
			nextEventTime = finishTime;
		}
		System.out.println("last bin service event finish time is : "+finishTime);
		System.out.println("next bin service event time is : "+nextEventTime);
		if (nextEventTime < getStopTime()) {
			nextEvent.schedule(nextEventTime);
			this.simulator.insert(nextEvent);
			System.out.println("LOGGING INFO: I just inserted another bin service event.!!!!!!!");
			System.out.println("nextEventTime : "+nextEventTime+" in hh:mm:ss format: "+nextEvent.timeToString());
			return;
		}
		System.out.println("no more bin service event....");
	}
		
	@Override
	public void execute(Simulator simulator) {
		this.simulator = simulator;
		System.out.println("LOGGING INFO: ------------------ EXECUTING BIN SERVICE EVENT -------------");
		System.out.println("Event time is: "+timeToString()+" for service area with areaIdx : "+sa.getAreaIdx());
		
		if (!(getEventTime() < getStopTime())) {
			System.out.println("STUPID!!! THIS SHOULD NEVER OCCUR!!!!!!!!!!!!!!!!");
			return;
		}
		Lorry lorry = sa.getLorry(); // bin service event carried out by lorry
		if (lorry.getLorryLocation() == 0) { // if at depot
			sa.computePath();  // a queue of integers to indicate the order of bins to serve
			if (sa.isDone()) {
				System.out.println("Done servicing. No bins to be serviced. U stupid.");
				return;
			}
			int currLocation = lorry.getLorryLocation(); // should be at 0... 
			int currDestination = sa.getNextBinInQueue();
			LorryDepartureEvent departLorry = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa, this);
			simulator.insert(departLorry);
			System.out.println("LOGGING INFO: ------------------ INSERTED LORRY DEPARTURE EVENT ----------------");
			System.out.println("currLocation : "+currLocation+" currDestination : "+currDestination);
		} else {
			System.out.println("ERROR YOU STUPID : IT SHOULD NEVER REACH THIS STAGE!!!");
			System.out.println("I AM BIN SERVICE EVENT !!! CHECK YOUR EXECUTE FUNCTION U STUPID!!");
		}		
	}
	
	
}
