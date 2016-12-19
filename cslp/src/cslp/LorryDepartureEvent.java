package cslp;

import java.util.logging.Logger;

public class LorryDepartureEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(LorryDepartureEvent.class.getName());
	
	private ServiceArea sa;
	private int departFrom;
	private int arriveAt;
	
	public LorryDepartureEvent(int eventTime, int departFrom, int arriveAt, 
			ServiceArea sa) {
		schedule(eventTime);
		this.departFrom = departFrom;
		this.arriveAt = arriveAt;
		this.sa = sa;
//		LorryDepartureEvent.LOGGER.setLevel(Level.OFF);
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return; // actually should never get to this stage?? 
		}
		
		int timeToNextBin = sa.getTimeBetweenLocations(departFrom, arriveAt);
		int arrivalTime = getEventTime() + timeToNextBin;
		
		Lorry lorry = sa.getLorry();
		lorry.departLorry(this);
		
		if (arrivalTime < getStopTime()) {
			LorryArrivalEvent getThere = new LorryArrivalEvent(arrivalTime, arriveAt, sa);
			simulator.insert(getThere);
			LOGGER.info("\tInserted LorryArrivalEvent: areaIdx = "+sa.getAreaIdx()+"	depart from = "+departFrom+"	arrive at = "+arriveAt
					 +"\n\tdeparture time = "+getEventTime()+"	arrival time = "+arrivalTime);
		}
	}
	
	
}
