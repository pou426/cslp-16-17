package cslp;

import java.util.logging.Logger;

public class LorryArrivalEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(LorryDepartureEvent.class.getName());
	
	private ServiceArea sa;
	private int currLocation;
	
	public LorryArrivalEvent(int eventTime, int arriveAt, ServiceArea sa) {
		schedule(eventTime);
		this.currLocation = arriveAt;
		this.sa = sa;
//		LorryArrivalEvent.LOGGER.setLevel(Level.OFF);
	}
	
	public int getCurrLocation() {
		return this.currLocation;
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.info("Should not reach this state.");
			return;
		}

		LOGGER.info("\tareaIdx = "+sa.getAreaIdx()
				 +"\n\tcurrent location = "+currLocation
				 +"\n\tcurrent time = "+getEventTime()+" ("+timeToString()+")");
		Lorry lorry = sa.getLorry();
		lorry.setLorryLocation(this);

		if (currLocation != 0) { // not at depot right now
			int timeToEmptyBin = Lorry.getBinServiceTime();
			int binEmptiedTime = getEventTime() + timeToEmptyBin; // new time for bin emptied event
			if (binEmptiedTime < getStopTime()) {
				Bin bin = sa.getBin(currLocation);
				float binVol = bin.getWasteVolume()/2; // compressed
				float binWeight = bin.getWasteWeight();
				float remainingVol = Lorry.getLorryVolume() - lorry.getCurrentTrashVolume(); // lorry's remaining capacity
				float remainingWeight = Lorry.getLorryMaxLoad() - lorry.getCurrentTrashWeight();
				if ((binVol > remainingVol) || (binWeight > remainingWeight)) {
					sa.insertToQueue(currLocation); // insert thie bin back to service queue
					LorryDepartureEvent lorryDepartureEvent = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa);
					simulator.insert(lorryDepartureEvent);
					LOGGER.info("\tcurrent location = "+currLocation+"	bin volume (compressed) = "+binVol+"	binWeight = "+binWeight+" remaining lorry volume = "+remainingVol+"	remaining lorry weight = "+remainingWeight
							 +"\n\tLorry does not have enough volume/load to empty bin."
							 +"\n\tLorryDepartureEvent inserted back to depot.");
					return;
				} else {
					bin.setIsServicing(); // isServicing until binEmptiedEvent is executed, such that dipsoal events cannot happen at that bin.
	 				BinEmptiedEvent binEmptiedEvent = new BinEmptiedEvent(binEmptiedTime, sa, bin);
					simulator.insert(binEmptiedEvent);	
					LOGGER.info("\tcurrent location = "+currLocation+"	bin volume (compressed) = "+binVol+"	binWeight = "+binWeight+" remaining lorry volume = "+remainingVol+"	remaining lorry weight = "+remainingWeight
							 +"\n\tBinEmptiedEvent inserted: areaIdx = "+sa.getAreaIdx()+"	bin = "+bin.getBinIdx()+"	binEmptiedTime = "+binEmptiedTime);
					return;
				}
			}
			return;
		} else if (currLocation == 0) { // at depot, empties lorry straight away
			if ((lorry.getCurrentTrashVolume() == 0) && (lorry.getCurrentTrashWeight() == 0)) {
				LOGGER.warning("LorryArrivalEvent with empty lorry.");
				return;
			}
			int timeToEmptyLorry = Lorry.getBinServiceTime() * 5; // 5 times as long as bin service time
			int lorryEmptiedTime = getEventTime() + timeToEmptyLorry;
			if (lorryEmptiedTime < getStopTime()) {
				LorryEmptiedEvent lorryEmptiedEvent = new LorryEmptiedEvent(lorryEmptiedTime, sa);
				simulator.insert(lorryEmptiedEvent);
				LOGGER.info("\tInserted LorryEmptiedEvent: areaIdx = "+sa.getAreaIdx()+"	lorryEmptiedTime = "+lorryEmptiedTime);
				return;
			}
		}
	}

}
