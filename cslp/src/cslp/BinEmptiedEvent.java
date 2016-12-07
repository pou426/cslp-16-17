package cslp;

import java.util.logging.Logger;

public class BinEmptiedEvent extends AbstractEvent {

	private static final Logger LOGGER = Logger.getLogger(BinEmptiedEvent.class.getName());

	private ServiceArea sa;
	private Bin bin; // as a location
	
	public BinEmptiedEvent(int eventTime, ServiceArea sa, Bin bin) {
		schedule(eventTime);
		this.sa = sa;
		this.bin = bin;
	}
	
	public Bin getBin() {
		return this.bin;
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.info("Should not reach this state.");
			return;
		}
		Lorry lorry = sa.getLorry();
		LOGGER.info("Before emptying: bin weight : "+bin.currentWeight()+" vol : "+bin.currentVol()+" Lorry weight : "+lorry.getCurrentTrashWeight()+" vol : "+lorry.getCurrentTrashVolume());
		lorry.emptyBin(this); 
		LOGGER.info("After emptying: bin weight : "+bin.currentWeight()+" vol : "+bin.currentVol()+" Lorry weight : "+lorry.getCurrentTrashWeight()+" vol : "+lorry.getCurrentTrashVolume());
		int currLocation = lorry.getLorryLocation();
		if (sa.isDone()) {
			LOGGER.info("BinServiceEvent done. areaIdx : "+sa.getAreaIdx()+" insert new lorry departure event. currLocation : "+currLocation);
			LorryDepartureEvent goHome = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa);
			simulator.insert(goHome);
			return;
		}
		int currDestination = sa.getNextBinInQueue();
		// no need to check time because binEmptied = departEvent..
		LorryDepartureEvent goToNextBin = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(goToNextBin);
		LOGGER.info("Inserted LorryDepartureEvent. areaIdx : "+sa.getAreaIdx()+" currLocation : "+currLocation+" currDestination : "+currDestination);
	}

}
