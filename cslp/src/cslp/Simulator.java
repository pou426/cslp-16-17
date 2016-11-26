package cslp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Simulator class
 *
 */
public class Simulator {

	private HashMap<Short,ServiceArea> serviceAreas = new HashMap<Short,ServiceArea>();	// roadsLayout elements in seconds, serviceFreq in hour
	
	// experiments
	private static boolean isExperiment = false;
	private static ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>();
	private static ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>();
	private static ArrayList<Float> serviceFreqExp = new ArrayList<Float>();

	// simulation
	private PriorityQueue<AbstractEvent> events = new PriorityQueue<AbstractEvent>(); // stores upcoming events
	private int time;	// current simulation time

	public Simulator(Parser parser) {
		this.serviceAreas = parser.initialiseServiceAreas();
		this.events.clear();
		this.time = 0;
	}
	
	public HashMap<Short,ServiceArea> getServiceAreas() {
		return this.serviceAreas;
	}
	
	/**
	 * Insert an event into the simulator's priority queue 'events'
	 *
	 * @param e		an AbstractEvent instance
	 */
	public void insert(AbstractEvent e) {	// insert event into events queue
		this.events.add(e);
	}

	/**
	 * @return int		current simulation time
	 */
	public int now() {
		return time;
	}

	/**For Checking**/
	public PriorityQueue<AbstractEvent> getEvents() {
		return events;
	}
	
	/**
	 * extracts all events from 'events' priority queue and execute them
	 * update simulator time
	 */
	public void doAllEvents() {
		AbstractEvent e;
        while ((e = (AbstractEvent) events.poll()) != null) {
            time = e.getEventTime();
            e.execute(this);
        }
    }

	/**
	 * Starts simulator
	 * If this is an experiment, several simulations will be run with different parameters.
	 */
	public void start() {
		for (ServiceArea sa : serviceAreas.values()) {	// generate an initial disposal event for each bin
			for (Bin bin : sa.getBins()) {
				DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
				this.events.add(disposalEventGenerator);
			}
			BinServiceEvent binServiceEventGenerator = new BinServiceEvent(sa.getServiceInterval(),sa); // first event at service interval
			this.events.add(binServiceEventGenerator);
		}
		doAllEvents();	// execute all events from priority queue
	}

	/**
	 * Last stage of the simulation is statistical analysis
	 */
	public void statsAnalysis() {
		// insert code here
	}
	
	// setters
	public static void setIsExperiment(boolean isExperiment) {
		Simulator.isExperiment = isExperiment;
	}
	public static void setDisposalDistrRateExp(ArrayList<Float> disposalDistrRateExp) {
		Simulator.disposalDistrRateExp = disposalDistrRateExp;
	}
	public static void setDisposalDistrShapeExp(ArrayList<Short> disposalDistrShapeExp) {
		Simulator.disposalDistrShapeExp = disposalDistrShapeExp;
	}
	public static void setServiceFreqExp(ArrayList<Float> serviceFreqExp) {
		Simulator.serviceFreqExp = serviceFreqExp;
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		String filepath = args[0];
		Parser parser = new Parser();
		parser.runParser(filepath);
		parser.printAllInputs();
		
		HashMap<Short,ServiceArea> sas = parser.getServiceAreas();
		System.out.println("----------------------ALL BIN STATUS--------------------");
		for (ServiceArea sa : sas.values()) {
			Bin[] allBins = sa.getBins();
			for (Bin b : allBins) {
				b.printStatus();
			}
		}
		Simulator.setParameters(parser);	// set relevant static variables in this class
		

		if (args.length > 1) {
			Simulator citySimulator = new Simulator(parser);
			
			if (args[1].equals("a")) {
				// checks Erlang K here
				// collect a bunch of erlangK values with different rate and shape and plot graph
				System.out.println("--------------------------CHECKING ERLANGK-------------------------------");
				System.out.println("Example of Erlang K value (in minute): "+Random.erlangk()/60);	
				System.out.println("Average of Erlang K value (in minute): "+Random.meanErlangK()/60);
				int erlangk = 0;
				for (int i = 0; i < 100; i++)	erlangk += Random.erlangk();
				erlangk = erlangk/100;
				System.out.println("Average of 100 Erlang K values (in minute): "+erlangk/60);
			}
			if (args[2].equals("a")) {
				System.out.println("--------------------------CHECKING DISPOSAL EVENTS-------------------------------");
				System.out.println("No. of service areas: "+citySimulator.getServiceAreas().size());
				System.out.println("Current service areas info:");
				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
					System.out.println(sa.toString());
				}
				System.out.println("Generate initial disposal event for each bin:");
				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
					Bin[] bins = sa.getBins();
					for (Bin bin : bins) {
						DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
						citySimulator.insert(disposalEventGenerator);
						System.out.println("Generate disposal event for areaIdx = "+bin.getAreaIdx()+" and binIdx = "+bin.getBinIdx());
					}
				}
				System.out.println("No. of disposal events generated: "+citySimulator.getEvents().size());
				while (!citySimulator.getEvents().isEmpty()) {
					System.out.println(citySimulator.getEvents().poll().toString());
				}
				System.out.println("Initial generation ok! generate new events again.\n");
				System.out.println("   ***   ***      ***   ***      ***   ***   ");
				System.out.println("  ***** *****    ***** *****    ***** *****  ");
				System.out.println("  ***********    ***********    ***********  ");
				System.out.println("   *********      *********      *********   ");
				System.out.println("    *******        *******        *******    ");
				System.out.println("     *****          *****          *****     ");
				System.out.println("      ***            ***            ***      ");
				System.out.println("       *              *              *       ");				
				
				System.out.println("\n---------ALl diposal events generated for simulation----------");
				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
					Bin[] bins = sa.getBins();
					for (Bin bin : bins) {
						DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
						citySimulator.insert(disposalEventGenerator);	
					}
				}
				citySimulator.doAllEvents();
				System.out.println("----------------------Disposal events finish checking--------------------\n");
			}
		} 
		
		System.out.println("               *                     *                 ");
		System.out.println("              ***                   ***                ");
		System.out.println("             *******              ******               ");
		System.out.println("            *****************************              ");
		System.out.println("            *****************************              ");
		System.out.println("         ************************************          ");
		System.out.println("           *******************************             ");
		System.out.println("         ************************************          ");
		System.out.println("            *****************************              ");
		System.out.println("             ***************************               ");
		System.out.println("              *************************                ");

		
		// reset everything... 
		parser.runParser(filepath);
		setParameters(parser);
		parser.initialiseCity();
		HashMap<Short,ServiceArea> sas1 = parser.getServiceAreas();
		System.out.println("----------------------ALL BIN STATUS--------------------");
		for (ServiceArea sa : sas1.values()) {
			Bin[] allBins = sa.getBins();
			for (Bin b : allBins) {
				b.printStatus();
			}
		}

		Simulator citySimulator1 = new Simulator(parser);
		System.out.println("\n-----------------------------Restart now!!---------------------------------"
				+ "\ncurrent no of events in queue: "+citySimulator1.events.size());
		
		// not for testing
		if (isExperiment) {
			// at the moment, only one set of parameters will be run because no implementation for experiments yet
//				if (disposalDistrRateExp.size() > 0) {
//					float disposalDistrRate = disposalDistrRateExp.get(0);
//					Random.setDisposalDistrRate(disposalDistrRate);				
//				}
//				if (disposalDistrShapeExp.size() > 0) {
//					short disposalDistrShape = disposalDistrShapeExp.get(0);
//					Random.setDisposalDistrShape(disposalDistrShape);
//				}
		}

		System.out.println("Now, create initial disposal events and inital bin servicing events.");
		citySimulator1.start();
		citySimulator1.statsAnalysis();

	}
	
}
