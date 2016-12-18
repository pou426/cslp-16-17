package cslp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;

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
		LOGGER.info("Do All events from the event priority queue.");
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
			
			BinServiceEvent binServiceEventGenerator = new BinServiceEvent(sa.getServiceInterval(), sa); // first event at service interval
			this.events.add(binServiceEventGenerator);
		}
		doAllEvents();	// execute all events from priority queue
	}

	/**
	 * Last stage of the simulation is statistical analysis
	 * Outputs summary statistics.
	 * 
	 */
	public void statsAnalysis() {
		int noAreas = serviceAreas.size();
		
		System.out.println("---");
		// ============================== average trip duration ==============================
		int totalAvgTripDuration = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			int avgTripDuration = Math.round(sa.getAvgTripDuration()); // in second
			totalAvgTripDuration += avgTripDuration;
			String avgTripDurationString = "area "+sa.getAreaIdx()+": average trip duration "+timeToString(avgTripDuration);
			System.out.println(avgTripDurationString);			
		}
		// overall
		totalAvgTripDuration = Math.round(totalAvgTripDuration/noAreas); // in second
		String totalAvgTripDurationString = "overall average trip duration "+timeToString(totalAvgTripDuration);
		System.out.println(totalAvgTripDurationString);

		// ============================== no. of trips ==============================
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
		
		// ============================== trip efficiency ==============================
		float totalTripEfficiency = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float tripEfficiency = sa.getTripEfficiency();
			totalTripEfficiency += tripEfficiency;
			String tripEfficiencyString = "area "+sa.getAreaIdx()+": trip efficiency "+String.format("%.3f", tripEfficiency);
			System.out.println(tripEfficiencyString);
		}
		// overall 
		totalTripEfficiency = totalTripEfficiency/noAreas;
		String totalTripEfficiencyString = "overall trip efficiency "+String.format("%.3f",totalTripEfficiency);
		System.out.println(totalTripEfficiencyString);
		
		// ============================== average volume collected ==============================
		float totalAvgVolCollected = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float avgVolCollected = sa.getAvgVolCollected();
			totalAvgVolCollected += avgVolCollected;
			String avgVolCollectedString = "area "+sa.getAreaIdx()+": average volume collected "+String.format("%.3f",avgVolCollected);
			System.out.println(avgVolCollectedString);
		}
		// overall
		totalAvgVolCollected = totalAvgVolCollected/noAreas;
		String totalAvgVolCollectedString = "overall average volume collected "+String.format("%.3f",totalAvgVolCollected);
		System.out.println(totalAvgVolCollectedString);
		
		// ============================== overflow percentage ==============================
		float totalOverflowPercent = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			float overflowPercent = sa.getAvgPercentageOverflow();
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
	
	/**
	 * Return time in MM:SS format
	 * @param secCount		time in second
	 * @return
	 */
	public String timeToString(int timeInSec) { // for summary stats
		int secCount = timeInSec;
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
	
	public HashMap<Short,ServiceArea> getServiceAreas() {
		return this.serviceAreas;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String filepath = args[0];
		Parser parser = new Parser();
		
		parser.runParser(filepath);
//		parser.printAllInputs();
		boolean isExperiment = parser.isExperiment();
		
		
		// ================================== NOT EXPERIMENT ==================================
		if (!isExperiment) {
			// Random ddr and dds attributes already set in parser
			// service area ready for running
			Simulator citySimulator = new Simulator(parser);
			
			citySimulator.start();
			citySimulator.statsAnalysis();
		}
		
		
		
		
		
		// =================================== EXPERIMENT =================================
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
				LOGGER.info("This is an experiment but no value. should never reach here.");
			}
		}
	}
}
