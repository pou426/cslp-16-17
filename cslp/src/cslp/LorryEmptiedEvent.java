package cslp;

import java.util.logging.Logger;

public class LorryEmptiedEvent extends AbstractEvent {

	private static final Logger LOGGER = Logger.getLogger(LorryEmptiedEvent.class.getName());
	
	private ServiceArea sa;
	private Lorry lorry;
	
	public LorryEmptiedEvent(int eventTime, ServiceArea sa) {
		schedule(eventTime);
		this.sa = sa;
		this.lorry = sa.getLorry();
	}

	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.info("Should not reach this state.");
			return;
		}
		float beforeEmptyWeight = lorry.getCurrentTrashWeight(); // before emptying...
		float beforeEmptyVol = lorry.getCurrentTrashVolume(); // before emptying...
		LOGGER.info("Execute LorryEmptiedEvent. areaIdx : "+sa.getAreaIdx()+" currLocation : "+lorry.getLorryLocation());
		lorry.emptyLorry(this);
		LOGGER.info("Emptied lorry. before emptying : weight : "+beforeEmptyWeight+" vol : "+beforeEmptyVol+" after emptying : weight : "+lorry.getCurrentTrashWeight()+" vol : "+lorry.getCurrentTrashVolume());
		if (sa.isDone()) {
			sa.getBinServiceEvent().update(getEventTime(), beforeEmptyWeight, beforeEmptyVol); 
			LOGGER.info("BinServiceEvent finishes. updated BinServiceEvent. currTime : "+getEventTime()+" stopTime : "+getStopTime());
		} else {
			sa.getBinServiceEvent().reschedule(getEventTime(), beforeEmptyWeight, beforeEmptyVol);
			LOGGER.info("Still bins in queue. Rescheduled BinServiceEvent. currTime : "+getEventTime()+" before emptying weight : "+beforeEmptyWeight+" vol : "+beforeEmptyVol);
		}
	}
}
