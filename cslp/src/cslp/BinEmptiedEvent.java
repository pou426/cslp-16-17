package cslp;

import java.util.logging.Logger;

public class BinEmptiedEvent extends AbstractEvent {

	public static final Logger LOGGER = Logger.getLogger(BinEmptiedEvent.class.getName());

	private ServiceArea sa;
	private Bin bin;
	
	public BinEmptiedEvent(int eventTime, ServiceArea sa, Bin bin) {
		schedule(eventTime);
		this.sa = sa;
		this.bin = bin;
//		BinEmptiedEvent.LOGGER.setLevel(Level.OFF);
	}
	
	public Bin getBin() {
		return this.bin;
	}
	
	@Override
	public void execute(Simulator simulator) {
		if (!(getEventTime() < getStopTime())) {
			LOGGER.severe("Should not reach this state.");
			return;
		}
		Lorry lorry = sa.getLorry();
		lorry.emptyBin(this); 
		int currLocation = lorry.getLorryLocation();
		int currDestination;
		
		String status;
		if (sa.isDone()) {
			currDestination = 0;
			status = "No more bin to be serviced.";
		} else {
			currDestination = sa.getNextBinInQueue();	
			status = "Go to next bin.";
		}
		
		LorryDepartureEvent depart = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa);
		simulator.insert(depart);
		
		LOGGER.info("\tCurrent time: "+getEventTime()+" ("+timeToString()+")"+
				"\n\tCurrent Location: "+currLocation+
				"\n\tBefore emptying: bin weight = "+bin.currentWeight()+"	vol = "+bin.currentVol()+"	Lorry weight = "+lorry.getCurrentTrashWeight()+"	vol = "+lorry.getCurrentTrashVolume()+
				"\n\tAfter emptying:  bin weight = "+bin.currentWeight()+"	vol = "+bin.currentVol()+"	Lorry weight = "+lorry.getCurrentTrashWeight()+"	vol = "+lorry.getCurrentTrashVolume()+
				"\n\tInserted LorryDepartureEvent for areaIdx = "+sa.getAreaIdx()+" 	eventTime = "+getEventTime()+" ("+timeToString()+")"+
				"\n\tCurrent location = "+currLocation+"	Current destination =  "+currDestination+
				"\n\t"+status);

	}

}
