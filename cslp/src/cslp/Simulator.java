package cslp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;


/**
 * Simulator class
 *
 */
public class Simulator {

	private static final Logger LOGGER = Logger.getLogger(Simulator.class.getName());


	private HashMap<Short,ServiceArea> serviceAreas = new HashMap<Short,ServiceArea>();	// roadsLayout elements in seconds, serviceFreq in hour
	
	private PriorityQueue<AbstractEvent> events = new PriorityQueue<AbstractEvent>(); // stores upcoming events
	private int time;	// current simulation time

	public Simulator(Parser parser) {
		this.serviceAreas = parser.createServiceAreas(); // new service area instance
		this.events.clear();
		this.time = 0;
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
	
	/**
	 * Extracts all events from 'events' priority queue and execute them.
	 * Updates simulator time
	 */
	public void doAllEvents() {
//		LOGGER.info("Do All events from the event priority queue.");
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
//		LOGGER.info("---------------------------------------- Starts the simulator: initial disposal + bin service events. ----------------------------------------");
		for (ServiceArea sa : serviceAreas.values()) {	// generate an initial disposal event for each bin

			for (Bin bin : sa.getBins()) {
				DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
				this.events.add(disposalEventGenerator);
			}
			
			BinServiceEvent binServiceEventGenerator = new BinServiceEvent(sa.getServiceInterval(), sa); // first event at service interval
			this.events.add(binServiceEventGenerator);
		}
		doAllEvents();	// execute all events from priority queue
	}

	/**
	 * Last stage of the simulation is statistical analysis
	 */
	public void statsAnalysis() {
		// insert code here
		int noAreas = serviceAreas.size();
		
		System.out.println("---");
		int totalAvgTripDuration = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			int avgTripDuration = Math.round(sa.getAvgTripDuration()); // in second
			totalAvgTripDuration += avgTripDuration;
			String formatDuration = timeToString(avgTripDuration);
			String avgTripDurationString = "area "+sa.getAreaIdx()+": average trip duration "+formatDuration;
			System.out.println(avgTripDurationString);			
		}
		// for overall
		totalAvgTripDuration = Math.round(totalAvgTripDuration/noAreas); // in second
		String formatDuration = timeToString(totalAvgTripDuration);
		String totalAvgTripDurationString = "overall average trip duration "+formatDuration;
		System.out.println(totalAvgTripDurationString);

		float totalAvgNoTrips = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float avgNoTrips = sa.getAvgNoTripsPerSchedule();
			totalAvgNoTrips += avgNoTrips;
			String avgNoTripsString = "area "+sa.getAreaIdx()+": average no. trips "+String.format("%.3f",avgNoTrips);
			System.out.println(avgNoTripsString);
		}
		// overall here
		totalAvgNoTrips = totalAvgNoTrips/noAreas;
		String totalAvgNoTripsString = "overall average no. trips "+String.format("%.3f",totalAvgNoTrips);
		System.out.println(totalAvgNoTripsString);
		
		float totalTripEfficiency = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float tripEfficiency = sa.getTripEfficiency();
			totalTripEfficiency += tripEfficiency;
			String tripEfficiencyString = "area "+sa.getAreaIdx()+": trip efficiency "+String.format("%.3f", tripEfficiency);
			System.out.println(tripEfficiencyString);
		}
		// overall here
		totalTripEfficiency = totalTripEfficiency/noAreas;
		String totalTripEfficiencyString = "overall trip efficiency "+String.format("%.3f",totalTripEfficiency);
		System.out.println(totalTripEfficiencyString);
		
		float totalAvgVolCollected = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float avgVolCollected = sa.getAvgVolCollected();
			totalAvgVolCollected += avgVolCollected;
			String avgVolCollectedString = "area "+sa.getAreaIdx()+": average volume collected "+String.format("%.3f",avgVolCollected);
			System.out.println(avgVolCollectedString);
		}
		// for overall
		totalAvgVolCollected = totalAvgVolCollected/noAreas;
		String totalAvgVolCollectedString = "overall average volume collected "+String.format("%.3f",totalAvgVolCollected);
		System.out.println(totalAvgVolCollectedString);
		
		float totalOverflowPercent = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float overflowPercent = sa.getAvgOverflowPercent();
			totalOverflowPercent += overflowPercent;
			String overflowPercentString = "area "+sa.getAreaIdx()+": percentage of bins overflowed "+String.format("%.3f", overflowPercent);
			System.out.println(overflowPercentString);
		}
		// for overall
		totalOverflowPercent = totalOverflowPercent/noAreas;
		String totalOverflowPercentString = "overall percentage of bins overflowed "+String.format("%.3f", totalOverflowPercent);
		System.out.println(totalOverflowPercentString);

		System.out.println("---");
	}
	
	public String timeToString(int secCount) { // for summary stats
		int minCount = 0;
		while (secCount >= 60) {
			secCount -= 60;
			minCount += 1;
		}
		String sec = String.format("%02d", secCount);
		String min = String.format("%02d", minCount);
		String result = min+":"+sec;
		return result;
	}
	
	// getters and setters
	public HashMap<Short,ServiceArea> getServiceAreas() {
		return this.serviceAreas;
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		String filepath = args[0];
		Parser parser = new Parser();
		
		parser.runParser(filepath);
//		parser.printAllInputs();
		boolean isExperiment = parser.isExperiment();
		
		if (!isExperiment) {
			// Random ddr and dds attributes already set in parser
			// service area already ready for running.
			Simulator citySimulator = new Simulator(parser);
			
			citySimulator.start();
			citySimulator.statsAnalysis();
		}
		
		if (isExperiment) {
			ArrayList<Float> disposalDistrRateExp = parser.getDisposalDistrRateExp();
			ArrayList<Short> disposalDistrShapeExp = parser.getDisposalDistrShapeExp();
			ArrayList<Float> serviceFreqExp = parser.getServiceFreqExp();
			boolean isDdrExp = true;
			boolean isDdsExp = true; 
			boolean isSfExp = true;
			if (disposalDistrRateExp.isEmpty()) isDdrExp = false;
			if (disposalDistrShapeExp.isEmpty()) isDdsExp = false;
			if (serviceFreqExp.isEmpty()) isSfExp = false;
			
//			Simulator citySimulatorExp = new Simulator(parser);
			
			// NOTE: parser already taken care of ddr and dds if any one of them is not experiment.
			if (isDdrExp) { // ddr is an experiment
				for (float ddr : disposalDistrRateExp) {
					Random.setDisposalDistrRate(ddr);
					if (isDdsExp) {
						for (short dds : disposalDistrShapeExp) {
							Random.setDisposalDistrShape(dds);
							if (isSfExp) {
								for (float sf : serviceFreqExp) {
									Simulator citySimulatorExp = new Simulator(parser);
									for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
										sa.changeServiceFreq(sf);
									}
									citySimulatorExp.start();
									citySimulatorExp.statsAnalysis();
								}
							} else {
								Simulator citySimulatorExp = new Simulator(parser);
								citySimulatorExp.start();
								citySimulatorExp.statsAnalysis();
							}
						}
					} else if (isSfExp) {
						for (float sf : serviceFreqExp) {
							Simulator citySimulatorExp = new Simulator(parser);
							for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
								sa.changeServiceFreq(sf);
							}
							citySimulatorExp.start();
							citySimulatorExp.statsAnalysis();
						}
					} else {
						Simulator citySimulatorExp = new Simulator(parser);
						citySimulatorExp.start();
						citySimulatorExp.statsAnalysis();
					}
				}
			} else if (isDdsExp) {
				for (short dds : disposalDistrShapeExp) {
					Random.setDisposalDistrShape(dds);
					if (isSfExp) {
						for (float sf : serviceFreqExp) {
							Simulator citySimulatorExp = new Simulator(parser);
							for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
								sa.changeServiceFreq(sf);
							}
							citySimulatorExp.start();
							citySimulatorExp.statsAnalysis();
						}
					} else {
						Simulator citySimulatorExp = new Simulator(parser);
						citySimulatorExp.start();
						citySimulatorExp.statsAnalysis();
					}
				}
			} else if (isSfExp) {
				for (float sf : serviceFreqExp) {
					Simulator citySimulatorExp = new Simulator(parser);
					for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
						sa.changeServiceFreq(sf);
					}
					citySimulatorExp.start();
					citySimulatorExp.statsAnalysis();
				}
			} else {
				LOGGER.info("Something is wrong. This is an experiment but no value. should never reach here.");
			}
		}
		
		
//		System.out.println("LOGGING INFO: ---------------- FOR NON EXPERIMENT ----------------");
//		float ddr = parser.getDisposalDistrRate();
//		short dds = parser.getDisposalDistrShape();
//		Simulator citySimulator = new Simulator(parser, ddr, dds);
//		Random random = new Random(ddr, dds);
//		System.out.println("----------------------ALL BIN STATUS--------------------");
//		for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
//			Bin[] allBins = sa.getBins();
//			for (Bin b : allBins) {
//				b.printStatus();
//			}
//		}
//		
//		if (args.length > 1) {
//			
//			if (args[1].equals("a")) {
//				// checks Erlang K here
//				// collect a bunch of erlangK values with different rate and shape and plot graph
//				System.out.println("--------------------------CHECKING ERLANGK-------------------------------");
//				System.out.println("Example of Erlang K value (in minute): "+random.erlangk()/60);	
//				System.out.println("Average of Erlang K value (in minute): "+random.meanErlangK()/60);
//				int erlangk = 0;
//				for (int i = 0; i < 100; i++)	erlangk += random.erlangk();
//				erlangk = erlangk/100;
//				System.out.println("Average of 100 Erlang K values (in minute): "+erlangk/60);
//			}
//			if (args[2].equals("a")) {
//				System.out.println("--------------------------CHECKING DISPOSAL EVENTS-------------------------------");
//				System.out.println("No. of service areas: "+citySimulator.getServiceAreas().size());
//				System.out.println("Current service areas info:");
//				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
//					System.out.println(sa.toString());
//				}
//				System.out.println("Generate initial disposal event for each bin:");
//				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
//					Bin[] bins = sa.getBins();
//					for (Bin bin : bins) {
//						DisposalEvent disposalEventGenerator = new DisposalEvent(random.erlangk(), bin, random);
//						citySimulator.insert(disposalEventGenerator);
//						System.out.println("Generate disposal event for areaIdx = "+bin.getAreaIdx()+" and binIdx = "+bin.getBinIdx());
//					}
//				}
//				System.out.println("No. of disposal events generated: "+citySimulator.events.size());
//				while (!citySimulator.events.isEmpty()) {
//					System.out.println(citySimulator.events.poll().toString());
//				}
//				System.out.println("Initial generation ok! generate new events again.\n");
//				System.out.println("   ***   ***      ***   ***      ***   ***   ");
//				System.out.println("  ***** *****    ***** *****    ***** *****  ");
//				System.out.println("  ***********    ***********    ***********  ");
//				System.out.println("   *********      *********      *********   ");
//				System.out.println("    *******        *******        *******    ");
//				System.out.println("     *****          *****          *****     ");
//				System.out.println("      ***            ***            ***      ");
//				System.out.println("       *              *              *       ");				
//				
//				System.out.println("\n---------ALl diposal events generated for simulation----------");
//				for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
//					Bin[] bins = sa.getBins();
//					for (Bin bin : bins) {
//						DisposalEvent disposalEventGenerator = new DisposalEvent(random.erlangk(), bin, random);
//						citySimulator.insert(disposalEventGenerator);	
//					}
//				}
//				citySimulator.doAllEvents();
//				System.out.println("----------------------Disposal events finish checking--------------------\n");
//			}
//		} 
//
//		System.out.println("		..▓▓..▓▓");
//		System.out.println("		..▓▓......▓▓");
//		System.out.println("		..▓▓......▓▓..................▓▓▓▓");
//		System.out.println("		..▓▓......▓▓..............▓▓......▓▓▓▓");
//		System.out.println("		..▓▓....▓▓..............▓......▓▓......▓▓");
//		System.out.println("		....▓▓....▓............▓....▓▓....▓▓▓....▓▓");
//		System.out.println("		......▓▓....▓........▓....▓▓..........▓▓....▓");
//		System.out.println("		........▓▓..▓▓....▓▓..▓▓................▓▓");
//		System.out.println("		........▓▓......▓▓....▓▓");
//		System.out.println("		.......▓......................▓");
//		System.out.println("		.....▓.........................▓");
//		System.out.println("		....▓......^..........^......▓");
//		System.out.println("		....▓............♥.............▓");
//		System.out.println("		....▓..........................▓");
//		System.out.println("		......▓..........ٮ..........▓");
//		System.out.println("		..........▓▓..........▓▓ \n");
//
//		
//		// check citySimulator status
//		System.out.println("---------------------- ALL BIN STATUS for sim 1 --------------------");
//		for (ServiceArea sa : citySimulator.getServiceAreas().values()) {
//			Bin[] allBins = sa.getBins();
//			for (Bin b : allBins) {
//				b.printStatus();
//			}
//		}
//		
//		System.out.println("LOGGING INFO: -------------- creating new simulator... -----------------");
//		// create new simulator
//		Simulator citySimulator1 = new Simulator(parser, ddr, dds);
//		System.out.println("---------------------- ALL BIN STATUS for new sim ----------------------");
//		for (ServiceArea sa : citySimulator1.getServiceAreas().values()) {
//			Bin[] allBins = sa.getBins();
//			for (Bin b : allBins) {
//				b.printStatus();
//			}
//		}
//
//		System.out.println("\n-----------------------------Restart now!!---------------------------------"
//				+ "\ncurrent no of events in queue: "+citySimulator1.events.size());
//		
//		// not for testing
////		if (isExperiment) {
//			// at the moment, only one set of parameters will be run because no implementation for experiments yet
////				if (disposalDistrRateExp.size() > 0) {
////					float disposalDistrRate = disposalDistrRateExp.get(0);
////					Random.setDisposalDistrRate(disposalDistrRate);				
////				}
////				if (disposalDistrShapeExp.size() > 0) {
////					short disposalDistrShape = disposalDistrShapeExp.get(0);
////					Random.setDisposalDistrShape(disposalDistrShape);
////				}
////		}
//
//		System.out.println("Now, create initial disposal events and inital bin servicing events.");
//		citySimulator1.start();
//		citySimulator1.statsAnalysis();

	}
	
}
