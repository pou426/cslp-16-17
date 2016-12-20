package cslp;

import java.util.logging.Logger;

public class BinServiceEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(BinServiceEvent.class.getName());

	private ServiceArea sa; 
	private Simulator simulator;
	
	private int nextEventTime; // time for next bin servicing event
	private BinServiceEvent nextEvent; // generate next BinServiceEvent until stopTime 
		
	// STATISTICS
	private int noTrips; // no of trips performed during each scheduled servicing event
	
	public BinServiceEvent(int eventTime, ServiceArea sa) {	
		schedule(eventTime);
		this.sa = sa;
		this.nextEventTime = eventTime + sa.getServiceInterval(); // next event time = current event time + service Interval 
		if (nextEventTime < getStopTime()) { 
			this.nextEvent = new BinServiceEvent(nextEventTime, sa);
		} 
		this.noTrips = 0;
	}
	
	/**
	 * Method called when rescheduling is required. This occurs when a lorry has exceeded its capacity and returned to depot
	 * with bins in the service queue not yet serviced. 
	 * 
	 * @param lorryEmptiedEventTime		time when lorry finished emptying its content
	 * @param collectedWeight			weight of collected trash
	 * @param collectedVol				volume of collected trash
	 */
	public void reschedule(int lorryEmptiedEventTime, float collectedWeight, float collectedVol) {
		if (!(lorryEmptiedEventTime < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return;
		}
		Lorry lorry = sa.getLorry(); 
		int currLocation = lorry.getLorryLocation(); // should be 0
		if (sa.isDone() || (lorry.getLorryLocation() != 0)) {
			LOGGER.severe("Should not reach this state.");
		}
		
		int startTime = getEventTime(); // start time for this event
		schedule(lorryEmptiedEventTime); // start time for next event
		
		int leftoverBins = sa.getServiceQueueSize();
		sa.computePath(); // compute path to service what's left 
		int currDestination = sa.getNextBinInQueue();

		LorryDepartureEvent departLorry = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(departLorry);
		
		LOGGER.info("\tCurrent time: "+getEventTime()+" ("+timeToString()+")"+
				"\n\tCurrent Location: "+currLocation+
				"\n\tBefore rescheduling: service queue size = "+leftoverBins+"	Lorry trash weight = "+lorry.getCurrentTrashWeight()+"	Lorry trash vol = "+lorry.getCurrentTrashVolume()+
				"\n\tAfter reshceduling:  service queue size = "+sa.getServiceInterval()+
				"\n\tInserted lorry departure event: current Location = "+currLocation+" current destination = "+currDestination);
		
		// Statistics
		if (!(lorryEmptiedEventTime < getWarmUpTime())) {
			noTrips += 1;
			int currTripDuration = getEventTime() - startTime;
			float currTripEfficiency = collectedWeight/(currTripDuration/60); // unit : kg/min
			if (collectedWeight == 0) { // avoid division by 0
				currTripEfficiency = 0;
			}
			sa.addTripDuration(currTripDuration);
			sa.addTripEfficiency(currTripEfficiency); // kg/min
			sa.addVolCollected(collectedVol);
			
			LOGGER.info("\tStatistics: collected"+
					  "\n\tcurrent time = "+lorryEmptiedEventTime+"	warm up time = "+getWarmUpTime()+
					  "\n\tcurrent no trips = "+noTrips+
					  "\n\tcurrent trip duration = "+currTripDuration+
					  "\n\tcurrent trip efficiency = "+currTripEfficiency+
					  "\n\tcollected volume = "+collectedVol+
					  "\n\tcollected weight = "+collectedWeight);
		} else {
			LOGGER.info("\tStatistics: NOT collected"+
					  "\n\tcurrent time = "+lorryEmptiedEventTime+"	warm up time = "+getWarmUpTime()+
					  "\n\tcurrent no trips = "+noTrips+
//					  "\n\tcurrent trip duration = "+currTripDuration+
//					  "\n\tcurrent trip efficiency = "+currTripEfficiency+
					  "\n\tcollected volume = "+collectedVol+
					  "\n\tcollected weight = "+collectedWeight);
		}
	}
	
	/**
	 * Update event time (to include any delay) for next Bin Service Event (attribute of this bin service event)
	 * insert new bin service event
	 * 
	 * @param finishTime		finish time for this bin service event
	 */
	public void update(int finishTime, float collectedWeight, float collectedVol) {
		// Statistics
		int currTripDuration = finishTime - getEventTime();
		float currTripEfficiency = collectedWeight/(currTripDuration/60); // kg/min
		if (collectedWeight == 0) 	currTripEfficiency = 0;
		
		if (!(finishTime < getWarmUpTime())) {
			if (!(currTripDuration == 0)) {
				noTrips += 1 ;
			}
			sa.addNoTrip(noTrips);
			sa.addTripDuration(currTripDuration);
			sa.addTripEfficiency(currTripEfficiency); // kg/min
			sa.addVolCollected(collectedVol);
			
			LOGGER.info("\tStatistics: collected. areaIdx = "+sa.getAreaIdx()+
					  "\n\tcurrent time = "+finishTime+"	warm up time = "+getWarmUpTime()+
					  "\n\tcurrent no trips = "+noTrips+
					  "\n\tcurrent trip duration = "+currTripDuration+
					  "\n\tcurrent trip efficiency = "+currTripEfficiency+
					  "\n\tcollected volume = "+collectedVol+
					  "\n\tcollected weight = "+collectedWeight);
		} else {
			LOGGER.info("\tStatistics: NOT collected. areaIdx = "+sa.getAreaIdx()+
					  "\n\tcurrent time = "+finishTime+"	warm up time = "+getWarmUpTime()+
					  "\n\tcurrent no trips = "+noTrips+
					  "\n\tcurrent trip duration = "+currTripDuration+
					  "\n\tcurrent trip efficiency = "+currTripEfficiency+
					  "\n\tcollected volume = "+collectedVol+
					  "\n\tcollected weight = "+collectedWeight);
		}
		
		int original = nextEventTime;
		if (finishTime > nextEventTime) { // if delayed
			nextEventTime = finishTime;
			nextEvent.schedule(nextEventTime);
			LOGGER.info("\tDelay occurs: areaIdx = "+sa.getAreaIdx()+"	original next time: "+original+"	finish time = "+finishTime);
		} else {
			LOGGER.info("\tNo delay: areaIdx = "+sa.getAreaIdx()+"	original next time: "+original+"	finish time = "+finishTime);
		}
		
		sa.setBinServiceEvent(null);
		
		if (nextEventTime < getStopTime()) {
			this.simulator.insert(nextEvent);
			LOGGER.info("Another BinServiceEvent inserted to queue. nextEventTime : "+nextEventTime+" ("+nextEvent.timeToString()+")");
			return;
		}
		LOGGER.info("No more bin service events. areaIdx = "+sa.getAreaIdx());
		return;
	}
		
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state. areaIdx = "+sa.getAreaIdx());
			return;
		}
		
		this.simulator = simulator;
		sa.setBinServiceEvent(this);
		
		// Statistics
		float overflowPercent = sa.computeOverflowPercent();

		if (!(getEventTime() < getWarmUpTime())) {
			sa.addOverflowPercent(overflowPercent);	
			LOGGER.info("\tStatistics: collected"
					+ "\n\tcurrent time = "+getEventTime()+"	warm up time = "+getWarmUpTime()
					+ "\n\tOverflow percent = "+overflowPercent);
		} else {
			LOGGER.info("\tStatistics: NOT collected"
					+ "\n\tcurrent time = "+getEventTime()+"	warm up time = "+getWarmUpTime()
					+ "\n\tOverflow percent = "+overflowPercent);
		}
		
		Lorry lorry = sa.getLorry(); // bin service event carried out by lorry
		
		if (lorry.getLorryLocation() != 0) { // for checking
			LOGGER.severe("Should not reach this state. areaIdx = "+sa.getAreaIdx());
			return;
		}
		
		sa.computePath();  // a queue of integers to indicate the order of bins to serve
		
		if (sa.isDone()) {
			LOGGER.warning("No bin to be served. areaIdx = "+sa.getAreaIdx());
			update(getEventTime(), 0, 0);
			return;
		}
				
		int currLocation = lorry.getLorryLocation(); // should be 0 
		int currDestination = sa.getNextBinInQueue();
		LorryDepartureEvent departLorry = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(departLorry);

		LOGGER.info("\tLorry departure event inserted: areaIdx = "+sa.getAreaIdx()+" currLocation = "+currLocation+" currDestination = "+currDestination);
	}
	
}
