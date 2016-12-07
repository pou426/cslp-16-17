package cslp;

import java.util.logging.Logger;

public class LorryDepartureEvent extends AbstractEvent {

	private static final Logger LOGGER = Logger.getLogger(LorryDepartureEvent.class.getName());
	
	private ServiceArea sa;
	private int departFrom;
	private int arriveAt;
	
	public LorryDepartureEvent(int eventTime, int departFrom, int arriveAt, 
			ServiceArea sa) {
		schedule(eventTime);
		this.departFrom = departFrom;
		this.arriveAt = arriveAt;
		this.sa = sa;
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return; // actually should never get to this stage?? 
		}
		
		int timeToNextBin = sa.getTimeBetweenLocations(departFrom, arriveAt); // assuming always positive here...
		int arrivalTime = getEventTime() + timeToNextBin;
		LOGGER.info("areaIdx : "+sa.getAreaIdx()+" currLocation : "+departFrom+" currDestination : "+arriveAt+" currTime : "+getEventTime()+" arrivalTime: "+arrivalTime);
		
		Lorry lorry = sa.getLorry();
		lorry.departLorry(this);
		
		if (arrivalTime < getStopTime()) {
			LorryArrivalEvent getThere = new LorryArrivalEvent(arrivalTime, arriveAt, sa);
			simulator.insert(getThere); // insert arrival event...
			LOGGER.info("Inserted LorryArrivalEvent for areaIdx : "+sa.getAreaIdx()+" arrivalTime : "+arrivalTime+" destination : "+arriveAt);
		}
	}
	
	
}
