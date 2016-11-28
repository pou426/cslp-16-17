package cslp;

import sun.misc.Perf.GetPerfAction;

public class LorryArrivalEvent extends AbstractEvent {

	private ServiceArea sa;
	private Bin bin;
	private int currLocation;
	private BinServiceEvent binServiceEvent;
	
	public LorryArrivalEvent(int eventTime, int arriveAt, ServiceArea sa, BinServiceEvent binServiceEvent) {
		schedule(eventTime);
		this.currLocation = arriveAt;
		this.sa = sa;
		if (currLocation != 0) {
			this.bin = sa.getBins()[arriveAt-1];
		} else {
			System.out.println("---- LorryArrivalEvent destination is depot ----");
		}
		this.binServiceEvent = binServiceEvent;
	}
	@Override
	public void execute(Simulator simulator) {
		// TODO Auto-generated method stub
		System.out.println("LOGGING INFO : Executing LorryArrivalEvent for areaIdx : "+sa.getAreaIdx()+" arriving at : "+currLocation);
		System.out.println("Current time : "+getEventTime()+ " in time format: "+timeToString());
		if (!(getEventTime() < getStopTime())) {
			System.out.println("BOO YOU WHORE!");
			return;
		}
		Lorry lorry = sa.getLorry();
		lorry.setLorryLocation(currLocation); // move lorry 
		if (currLocation != 0) { // not at depot right now
			int timeToEmptyBin = lorry.getBinServiceTime();
			int binEmptiedTime = getEventTime() + timeToEmptyBin; // new time for bin emptied event
			if (binEmptiedTime < getStopTime()) {
				// do checkings here for the bin and lorry capacity..... 
				float binVol = bin.getWasteVolume()/2;
				float binWeight = bin.getWasteWeight();
				float remainingVol = Lorry.getLorryVolume() - lorry.getCurrentTrashVolume();
				float remainingWeight = Lorry.getLorryMaxLoad() - lorry.getCurrentTrashWeight();
				// or should checking be done after emptying a bin?????
				if (binVol > remainingVol) {
					System.out.println("-------------------------------------------------------OOOOOOOHHHHHHH NOOOOOOOOOO----------------------------------");
					System.out.println("Lorry does not have enough volume to empty bin");
					System.out.println("lorry current remaining volume : "+remainingVol+" current remaining weight : "+remainingWeight);
					System.out.println("Bin current volume (compressed) :"+binVol+" current weight : "+binWeight);
					// rescedule here........
					// should be the same event! 
					LorryDepartureEvent lorryDepartureEvent = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa, binServiceEvent);
					simulator.insert(lorryDepartureEvent);
				} else if (binWeight > remainingWeight) {
					System.out.println("------------------------------------------------------OOOOOOOHHHHHHH NOOOOOOOOOO----------------------------------");
					System.out.println("Lorry does not have enough weight to empty bin");
					System.out.println("lorry current remaining volume : "+remainingVol+" current remaining weight : "+remainingWeight);
					System.out.println("Bin current volume (compressed) :"+binVol+" current weight : "+binWeight);
					// reschedule here.........
					// should be the same event! 
					LorryDepartureEvent lorryDepartureEvent = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa, binServiceEvent);
					simulator.insert(lorryDepartureEvent);
				} else {
	 				BinEmptiedEvent binEmptiedEvent = new BinEmptiedEvent(binEmptiedTime, sa, bin, binServiceEvent);
					simulator.insert(binEmptiedEvent);	
				}
			} // else return? 
		} else if (currLocation == 0) {
			// empty lorry event here...
			System.out.println("LOGGING INFO : [LorryArrivalEvent] arrived at depot for areaIdx = "+sa.getAreaIdx());
			System.out.println("[LorryArrivalEvent] going to empty lorry.");
			int timeToEmptyLorry = lorry.getBinServiceTime() * 5; // 5 times as long as bin service time
			int lorryEmptiedTime = getEventTime() + timeToEmptyLorry;
			if (lorryEmptiedTime < getStopTime()) {
				LorryEmptiedEvent lorryEmptiedEvent = new LorryEmptiedEvent(lorryEmptiedTime, sa, lorry, binServiceEvent);
				simulator.insert(lorryEmptiedEvent);
			}
		}
	}

}
