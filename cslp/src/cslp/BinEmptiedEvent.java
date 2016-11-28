package cslp;

public class BinEmptiedEvent extends AbstractEvent {

	private ServiceArea sa;
	private Lorry lorry;
	private Bin bin; // as a location
	private BinServiceEvent binServiceEvent;
	
	public BinEmptiedEvent(int eventTime, ServiceArea sa, Bin bin, BinServiceEvent binServiceEvent) {
		schedule(eventTime);
		this.sa = sa;
		this.lorry = sa.getLorry();
		this.bin = bin;
		this.binServiceEvent = binServiceEvent;
	}
	
	@Override
	public void execute(Simulator simulator) {
		System.out.println("LOGGING INFO : Executing BinEmptiedEvent for areaIdx : "+sa.getAreaIdx());
		System.out.println("before emptying..."); // for checking
		bin.printStatus(); // for checking
		// lorry.printStatus(); // for checking
		lorry.emptyBin(bin); 
		System.out.println("after emptying..."); // for checking
		bin.printStatus(); // for checking
		// lorry.printStatus(); // for checking
		int currLocation = lorry.getLorryLocation();
		if (sa.isDone()) {
			System.out.println("LOGGING INFO : Done servicing for areaIdx : "+sa.getAreaIdx());
			LorryDepartureEvent goHome = new LorryDepartureEvent(getEventTime(), currLocation, 0, sa, binServiceEvent);
			simulator.insert(goHome);
			System.out.println("LOGGING INFO : Inserted a new lorry departure event for areaIdx : "+sa.getAreaIdx());
			System.out.println("current location : "+currLocation+" current destination : 0");
			return;
		}
		int currDestination = sa.getNextBinInQueue();
		// no need to check time because binEmptied = departEvent..
		LorryDepartureEvent goToNextBin = new LorryDepartureEvent(getEventTime(), currLocation, currDestination, sa, binServiceEvent);
		simulator.insert(goToNextBin);
		System.out.println("LOGGING INFO : Inserted a departure event for areaIdx : "+sa.getAreaIdx());
		System.out.println("current Location : "+currLocation+" current destination: "+currDestination);
	}

	
}
