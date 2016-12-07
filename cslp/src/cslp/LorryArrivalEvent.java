package cslp;

import java.util.logging.Logger;

public class LorryArrivalEvent extends AbstractEvent {

	private static final Logger LOGGER = Logger.getLogger(LorryDepartureEvent.class.getName());
	
	private ServiceArea sa;
	private int currLocation;
	
	public LorryArrivalEvent(int eventTime, int arriveAt, ServiceArea sa) {
		schedule(eventTime);
		this.currLocation = arriveAt;
		this.sa = sa;
		if (currLocation == 0) { // for logging
			LOGGER.info("LorryArrivalEvent destination is depot.");
		}
	}
	
	public int getCurrLocation() {
		return this.currLocation;
	}
	
	@Override
	public void execute(Simulator simulator) {

		LOGGER.info("areaIdx : "+sa.getAreaIdx()+" currLocation : "+currLocation+" currTime : "+getEventTime()+" ("+timeToString()+")");
		if (!(getEventTime() < getStopTime())) {
			LOGGER.info("Should not reach this state.");
			return;
		}
		Lorry lorry = sa.getLorry();
		lorry.setLorryLocation(this); // move lorry 
		if (currLocation != 0) { // not at depot right now
			int timeToEmptyBin = Lorry.getBinServiceTime();
			int binEmptiedTime = getEventTime() + timeToEmptyBin; // new time for bin emptied event
			if (binEmptiedTime < getStopTime()) {
				// do checking here for the bin and lorry capacity..... 
				Bin bin = sa.getBin(currLocation); // current bin...
				float binVol = bin.getWasteVolume()/2; // compressed
				float binWeight = bin.getWasteWeight();
				float remainingVol = Lorry.getLorryVolume() - lorry.getCurrentTrashVolume();
				float remainingWeight = Lorry.getLorryMaxLoad() - lorry.getCurrentTrashWeight();
				LOGGER.info("currLocation : "+currLocation+" binVol (compressed) : "+binVol+" binWeight : "+binWeight+" remainingVol : "+remainingVol+" remainingWeight : "+remainingWeight);
				if (binVol > remainingVol) {
					LOGGER.info("Lorry does not have enough volume to empty bin. Inserted new LorryDepartureEvent to depot.");
					LorryDepartureEvent lorryDepartureEvent = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa);
					simulator.insert(lorryDepartureEvent);
				} else if (binWeight > remainingWeight) {
					LOGGER.info("Lorry does not have enough load to empty bin. Inserted new LorryDepartureEvent to depot.");
					LorryDepartureEvent lorryDepartureEvent = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa);
					simulator.insert(lorryDepartureEvent);
				} else {
					bin.setIsServicing(); // isServicing until binEmptiedEvent is executed, such that dipsoal events cannot happen at that bin.
	 				BinEmptiedEvent binEmptiedEvent = new BinEmptiedEvent(binEmptiedTime, sa, bin);
					simulator.insert(binEmptiedEvent);	
				}
			}
			return;
		} else if (currLocation == 0) {
			int timeToEmptyLorry = Lorry.getBinServiceTime() * 5; // 5 times as long as bin service time
			int lorryEmptiedTime = getEventTime() + timeToEmptyLorry;
			if (lorryEmptiedTime < getStopTime()) {
				LorryEmptiedEvent lorryEmptiedEvent = new LorryEmptiedEvent(lorryEmptiedTime, sa);
				simulator.insert(lorryEmptiedEvent);
				LOGGER.info("Inserted LorryEmpitedEvent. areaIdx : "+sa.getAreaIdx());
			}
		}
	}

}
