package cslp;

import java.util.logging.Logger;


/**
 * Service area service bin event.... 
 *
 */

// becomes an observer here
// observes all lorry departure and arrival events... to keep track???
public class BinServiceEvent extends AbstractEvent {

	private static final Logger LOGGER = Logger.getLogger(BinServiceEvent.class.getName());

	private ServiceArea sa; 
	private Simulator simulator;
	
	private int nextEventTime; // time for next bin servicing event
	private BinServiceEvent nextEvent; // this will keep scheduling new BinServiceEvent 
		
	// STATISTICS here
	private int noTrips;
	
	public BinServiceEvent(int eventTime, ServiceArea sa) {	
		schedule(eventTime);	// time in the simulation at which this disposal event occurs  
		this.noTrips = 0;
		this.sa = sa;
		this.nextEventTime = eventTime + sa.getServiceInterval(); // current event time + service Interval 
		if (nextEventTime < getStopTime()) {
			this.nextEvent = new BinServiceEvent(nextEventTime, sa);
		} 
	}
	
	public void reschedule(int lorryEmptiedEventTime, float collectedWeight, float collectedVol) {
		if (!(lorryEmptiedEventTime < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return;
		}
		Lorry lorry = sa.getLorry(); // bin service event carried out by lorry
		// FOR CHECKING...
		LOGGER.info("Reschedule current BinServiceEvent. Service queue size : "+sa.getServiceQueueSize()+" lorry trash vol : "+lorry.getCurrentTrashVolume()+" lorry trash weight : "+lorry.getCurrentTrashWeight());
		if (lorry.getLorryLocation() != 0) { // for checking. should not reach this state
			LOGGER.severe("Lorry location should be depot. Current lorry location : "+lorry.getLorryLocation());
			return;
		}

		int lastStartTime = getEventTime(); // start time for this event
		schedule(lorryEmptiedEventTime); // start time for next event
		if (sa.isDone()) { // for checking.. should not reach here
			LOGGER.severe("This is a rescheduling event but no bins to be serviced?");
			update(getEventTime(), 0, 0);
			return;
		}
		int currLocation = lorry.getLorryLocation(); // should be at 0... 
		
		sa.computePath(); // compute path here. 
		
		int currDestination = sa.getNextBinInQueue();
		LorryDepartureEvent departLorry = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(departLorry);
		LOGGER.info("Inserted lorry departure event. currLocation : "+currLocation+" currDestination : "+currDestination);
		
		if (!(lorryEmptiedEventTime < getWarmUpTime())) {
			// for STATS
			noTrips += 1;
			int currTripDuration = getEventTime() - lastStartTime;
			float currTripEfficiency = (currTripDuration/60)/collectedWeight; // kg/min
			if (collectedWeight == 0) { // safety 
				currTripEfficiency = 0;
			}
			sa.addTripDuration(currTripDuration);
			sa.addTripEfficiency(currTripEfficiency); // kg/min
			sa.addVolCollected(collectedVol);
			LOGGER.info("Statistics: current noTrips : "+noTrips+" currTripDuration : "+currTripDuration+" currTripEffiency : "+currTripEfficiency+" collectedVol : "+collectedVol+" collectedWeight : "+collectedWeight);
		} else {
			LOGGER.info("statistics not collected because current time : "+lorryEmptiedEventTime+" is less than warm up time : "+getWarmUpTime());
		}
	}
	
	/**
	 * Update event time (to include any delay) for next Bin Service Event (attribute of this bin service event)
	 * insert new bin service event
	 * 
	 * @param finishTime		finish time for this bin service event
	 */
	public void update(int finishTime, float collectedWeight, float collectedVol) {
		LOGGER.info("Update BinServiceEvent. areaIdx : "+sa.getAreaIdx()+" original next bin service event time : "+nextEventTime+" current bin service event finish time : "+finishTime);
		
		if (!(finishTime < getWarmUpTime())) {
			// STATISTICS HERE
			noTrips += 1;
			sa.addNoTrip(noTrips);
			int currTripDuration = finishTime - getEventTime();
			float currTripEfficiency = (currTripDuration/60)/collectedWeight; // kg/min
			if (collectedWeight == 0) { // safety 
				currTripEfficiency = 0;
			}
			sa.addTripDuration(currTripDuration);
			sa.addTripEfficiency(currTripEfficiency); // kg/min
			sa.addVolCollected(collectedVol);
			
			LOGGER.info("Statistics: current noTrips : "+noTrips+" currTripDuration : "+currTripDuration+" currTripEffiency : "+currTripEfficiency+" collectedVol : "+collectedVol+" collectedWeight : "+collectedWeight);
		} else {
			LOGGER.info("statistics not collected because current time : "+finishTime+" is less than warm up time : "+getWarmUpTime());
		}
//		if (!(finishTime < getStopTime())) {
//			return;
//		}
		
		if (finishTime > nextEventTime) { // if delayed
			nextEventTime = finishTime;
			nextEvent.schedule(nextEventTime);
			LOGGER.info("Delay occurs: areaIdx : "+sa.getAreaIdx()+" original next time : "+nextEventTime+" ("+nextEvent.timeToString()+") finish time : "+finishTime);
		} else {
			LOGGER.info("No delay: areaIdx : "+sa.getAreaIdx()+" original next time : "+nextEventTime+" finish time : "+finishTime);
//			LOGGER.info("No delay: areaIdx : "+sa.getAreaIdx()+" original next time : "+nextEventTime+" ("+nextEvent.timeToString()+") finish time : "+finishTime);
		}
		if (nextEventTime < getStopTime()) {
			this.simulator.insert(nextEvent);
			LOGGER.info("Another BinServiceEvent inserted to queue. nextEventTime : "+nextEventTime+" in hh:mm:ss format : "+nextEvent.timeToString());
			return;
		}
		LOGGER.info("No more bin service events.");
		return;
	}
		
	@Override
	public void execute(Simulator simulator) {
		this.simulator = simulator;
		sa.setBinServiceEvent(this);
		
		if (!(getEventTime() < getWarmUpTime())) {
			// for stats
			float overflowPercent = sa.computeOverflowPercent();
			sa.addOverflowPercent(overflowPercent);			
		}
		
		LOGGER.info("Event time : "+timeToString()+" areaIdx : "+sa.getAreaIdx());
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return;
		}
		
		Lorry lorry = sa.getLorry(); // bin service event carried out by lorry
		
		if (lorry.getLorryLocation() != 0) { // for checking
			LOGGER.severe("Should not reach this state.");
			return;
		}
		
		sa.computePath();  // a queue of integers to indicate the order of bins to serve
		
		if (sa.isDone()) { // is this for checking? 
			LOGGER.warning("No bin to be served.");
			update(getEventTime(), 0, 0);
			return;
		}
				
		int currLocation = lorry.getLorryLocation(); // should be at 0... 
		int currDestination = sa.getNextBinInQueue();
		LorryDepartureEvent departLorry = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(departLorry);

		LOGGER.info("Lorry departure event inserted. currLocation : "+currLocation+" currDestination : "+currDestination);
	}
	
}
