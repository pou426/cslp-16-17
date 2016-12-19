package cslp;

import java.util.logging.Logger;

public class LorryEmptiedEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(LorryEmptiedEvent.class.getName());
	
	private ServiceArea sa;
	private Lorry lorry;
	
	public LorryEmptiedEvent(int eventTime, ServiceArea sa) {
		schedule(eventTime);
		this.sa = sa;
		this.lorry = sa.getLorry();
//		LorryEmptiedEvent.LOGGER.setLevel(Level.OFF);
	}

	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return;
		}
		float beforeEmptyWeight = lorry.getCurrentTrashWeight(); // lorry's weight before emptying
		float beforeEmptyVol = lorry.getCurrentTrashVolume(); // lorry's volume before emptying
		lorry.emptyLorry(this);
		
		if (sa.isDone()) {
			sa.getBinServiceEvent().update(getEventTime(), beforeEmptyWeight, beforeEmptyVol); 
			LOGGER.info("\tareaIdx = "+sa.getAreaIdx()+" current location = "+lorry.getLorryLocation()
					+"\n\tbefore emptying: lorry weight = "+beforeEmptyWeight+"	lorry volume = "+beforeEmptyVol
					+"\n\tafter emptying:  lorry weight = "+lorry.getCurrentTrashWeight()+"	lorry volume = "+lorry.getCurrentTrashVolume()
					+"\n\tBinServiceEvent finishes, update BinServiceEvent: current time = "+getEventTime()+"	stop time = "+getStopTime());
			return;
		} else {
			sa.getBinServiceEvent().reschedule(getEventTime(), beforeEmptyWeight, beforeEmptyVol);
			LOGGER.info("\tareaIdx = "+sa.getAreaIdx()+" current location = "+lorry.getLorryLocation()
					+"\n\tbefore emptying: lorry weight = "+beforeEmptyWeight+"	lorry volume = "+beforeEmptyVol
					+"\n\tafter emptying:  lorry weight = "+lorry.getCurrentTrashWeight()+"	lorry volume = "+lorry.getCurrentTrashVolume()
					+"\n\tStill bins in queue, reschedule BinServiceEvent: current time = "+getEventTime());
			return;
		}
	}
}
