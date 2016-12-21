package cslp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Simulator {

	public static final Logger LOGGER = Logger.getLogger(Simulator.class.getName());
	
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
				if (disposalEventGenerator.getEventTime() < AbstractEvent.getStopTime()) {
					this.events.add(disposalEventGenerator);
				} else {
					LOGGER.warning("\tInitial disposal event exceeds stop time. areaIdx = "+sa.getAreaIdx());
				}
			}
			
			BinServiceEvent binServiceEventGenerator = new BinServiceEvent(sa.getServiceInterval(), sa); // first event at service interval
			if (binServiceEventGenerator.getEventTime() < AbstractEvent.getStopTime()) {
				this.events.add(binServiceEventGenerator);
			} else {
				LOGGER.warning("\tInitial bin service event exceeds stop time. areaIdx = "+sa.getAreaIdx());
			}
		}
		doAllEvents();	// execute all events from priority queue
	}

	/**
	 * Last stage of the simulation is statistical analysis
	 * Outputs summary statistics.
	 * @throws FileNotFoundException 
	 * 
	 */
	public void statsAnalysis(PrintStream ps) {
		// output to a text file
		
		int noAreas = serviceAreas.size();

		int total_avg_trip_duration = 0;
		float total_avg_no_trips = 0;
		float total_trip_efficiency = 0;
		float total_avg_vol_collected = 0;
		float total_overflow_percent = 0;
		
		String avg_trip_duration_str = "";
		String avg_no_trips_str = "";
		String trip_efficiency_str = "";
		String avg_vol_collected_str = "";
		String overflow_percent_str = "";
		
		System.out.println("---");
		ps.println("---");
		int count = 0;
		for (ServiceArea sa : serviceAreas.values()) {
			count++;
			
			// average trip duration
			int avg_trip_duration = Math.round(sa.getAvgTripDuration()); // in second
			total_avg_trip_duration += avg_trip_duration;
			avg_trip_duration_str += "area "+sa.getAreaIdx()+": average trip duration "+timeToString(avg_trip_duration);
			
			// average no of trips
			float avg_no_trips = sa.getAvgNoTripsPerSchedule();
			total_avg_no_trips += avg_no_trips;
			avg_no_trips_str += "area "+sa.getAreaIdx()+": average no. trips "+String.format("%.3f",avg_no_trips);
			
			// trip efficiency
			float trip_efficiency = sa.getTripEfficiency();
			total_trip_efficiency += trip_efficiency;
			trip_efficiency_str += "area "+sa.getAreaIdx()+": trip efficiency "+String.format("%.3f", trip_efficiency);
 
			// average volume collected
			float avg_vol_collected = sa.getAvgVolCollected();
			total_avg_vol_collected += avg_vol_collected;
			avg_vol_collected_str += "area "+sa.getAreaIdx()+": average volume collected "+String.format("%.3f",avg_vol_collected);

			// total overflow percent
			float overflow_percent = sa.getAvgPercentageOverflow();
			total_overflow_percent += overflow_percent;
			overflow_percent_str += "area "+sa.getAreaIdx()+": percentage of bins overflowed "+String.format("%.3f", overflow_percent);

			if (count < noAreas) {
				avg_trip_duration_str += "\n";
				avg_no_trips_str += "\n";
				trip_efficiency_str += "\n";
				avg_vol_collected_str += "\n";
				overflow_percent_str += "\n";
			}
			
		}
		System.out.println(avg_trip_duration_str);
		ps.println(avg_trip_duration_str);
		// overall average trip duration
		total_avg_trip_duration = Math.round(total_avg_trip_duration/noAreas); // in seconds
		String total_avg_trip_duration_str = "overall average trip duration "+timeToString(total_avg_trip_duration);
		System.out.println(total_avg_trip_duration_str);
		ps.println(total_avg_trip_duration_str);
		
		System.out.println(avg_no_trips_str);
		ps.println(avg_no_trips_str);
		// overall average no of trips
		total_avg_no_trips = total_avg_no_trips/noAreas;
		String total_avg_no_trips_str = "overall average no. trips "+String.format("%.3f",total_avg_no_trips);
		System.out.println(total_avg_no_trips_str);
		ps.println(total_avg_no_trips_str);

		System.out.println(trip_efficiency_str);
		ps.println(trip_efficiency_str);
		// overall trip efficiency
		total_trip_efficiency = total_trip_efficiency/noAreas;
		String total_trip_efficiency_str = "overall trip efficiency "+String.format("%.3f",total_trip_efficiency);
		System.out.println(total_trip_efficiency_str);
		ps.println(total_trip_efficiency_str);

		System.out.println(avg_vol_collected_str);
		ps.println(avg_vol_collected_str);
		// overall total average collected 
		total_avg_vol_collected = total_avg_vol_collected/noAreas;
		String total_avg_vol_collected_str = "overall average volume collected "+String.format("%.3f",total_avg_vol_collected);
		System.out.println(total_avg_vol_collected_str);
		ps.println(total_avg_vol_collected_str);

		System.out.println(overflow_percent_str);
		ps.println(overflow_percent_str);
		// overall total overflow percent
		total_overflow_percent = total_overflow_percent/noAreas;
		String total_overflow_percent_str = "overall percentage of bins overflowed "+String.format("%.3f", total_overflow_percent);
		System.out.println(total_overflow_percent_str);
		ps.println(total_overflow_percent_str);

		System.out.println("---");
		ps.println("---");
	}
	
	/** 
	 * method to convert time into day:hour:min:sec format
	 * 
	 * @return String	the time in which current event happens in DD:HH:MM:SS format
	 */
	public String timeToString(int timeInSec) {
		int secCount = timeInSec;
		int minCount = 0;
		int hrCount = 0;
		int dayCount = 0;
		while (secCount >= 60) {
			secCount -= 60;
			minCount += 1;
		}
		while (minCount >= 60) {
			minCount -= 60;
			hrCount += 1;
		}
		while (hrCount >= 24) {
			hrCount -= 24;
			dayCount += 1;
		}
		String result = String.format("%02d", dayCount)+":"+String.format("%02d", hrCount)+
				":"+String.format("%02d", minCount)+":"+String.format("%02d", secCount);
		return result;
	}
	
	public HashMap<Short,ServiceArea> getServiceAreas() {
		return this.serviceAreas;
	}
	
	/** 
	 * Enable or disable Logger printing to console
	 * 
	 * @param int		turn off if i == 0, on otherwise
	 */
	public static void switchLogger(int i) {
		if (i==0) {
			Simulator.LOGGER.setLevel(Level.OFF);
//			ServiceAreaInfo.LOGGER.setLevel(Level.ALL);
			ServiceArea.LOGGER.setLevel(Level.OFF);
//			Random.LOGGER.setLevel(Level.ALL);
			Parser.LOGGER.setLevel(Level.OFF);
			NearestNeighbour.LOGGER.setLevel(Level.OFF);
			LorryEmptiedEvent.LOGGER.setLevel(Level.OFF);
			LorryDepartureEvent.LOGGER.setLevel(Level.OFF);
			LorryArrivalEvent.LOGGER.setLevel(Level.OFF);
			Lorry.LOGGER.setLevel(Level.OFF);
			FloydWarshall.LOGGER.setLevel(Level.OFF);
//			Error.LOGGER.setLevel(Level.ALL);
//			DisposalEvent.LOGGER.setLevel(Level.ALL);
			BruteForce.LOGGER.setLevel(Level.OFF);
			BinServiceEvent.LOGGER.setLevel(Level.OFF);
			BinEmptiedEvent.LOGGER.setLevel(Level.OFF);
			Bin.LOGGER.setLevel(Level.OFF);
//			Bag.LOGGER.setLevel(Level.ALL);
//			AbstractEvent.LOGGER.setLevel(Level.ALL);
		} else {
			Simulator.LOGGER.setLevel(Level.ALL);
//			ServiceAreaInfo.LOGGER.setLevel(Level.ALL);
			ServiceArea.LOGGER.setLevel(Level.ALL);
//			Random.LOGGER.setLevel(Level.ALL);
			Parser.LOGGER.setLevel(Level.ALL);
			NearestNeighbour.LOGGER.setLevel(Level.ALL);
			LorryEmptiedEvent.LOGGER.setLevel(Level.ALL);
			LorryDepartureEvent.LOGGER.setLevel(Level.ALL);
			LorryArrivalEvent.LOGGER.setLevel(Level.ALL);
			Lorry.LOGGER.setLevel(Level.ALL);
			FloydWarshall.LOGGER.setLevel(Level.ALL);
//			Error.LOGGER.setLevel(Level.ALL);
//			DisposalEvent.LOGGER.setLevel(Level.ALL);
			BruteForce.LOGGER.setLevel(Level.ALL);
			BinServiceEvent.LOGGER.setLevel(Level.ALL);
			BinEmptiedEvent.LOGGER.setLevel(Level.ALL);
			Bin.LOGGER.setLevel(Level.ALL);
//			Bag.LOGGER.setLevel(Level.ALL);
//			AbstractEvent.LOGGER.setLevel(Level.ALL);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
				
		String filepath = args[0];
		
		switchLogger(0); // Logger default is off
		if ((args.length > 1) && args[1].equals("yes"))	{
			switchLogger(1);
		}
		
		Parser parser = new Parser();
		parser.runParser(filepath);
//		parser.printAllInputs();
		boolean isExperiment = parser.isExperiment();
		
		File file = new File("./output_files/output.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
//		System.setOut(ps); // redirect to output.txt file
		
		// ================================== NOT EXPERIMENT ==================================
		if (!isExperiment) {	// Random ddr and dds attributes already set in parser
			Simulator citySimulator = new Simulator(parser);

			citySimulator.start();
			citySimulator.statsAnalysis(ps);
		}
		
		
		// =================================== EXPERIMENT ======================================
		if (isExperiment) {
			ArrayList<Float> disposalDistrRateExp = parser.getDisposalDistrRateExp();
			ArrayList<Short> disposalDistrShapeExp = parser.getDisposalDistrShapeExp();
			ArrayList<Float> serviceFreqExp = parser.getServiceFreqExp();
			boolean isDdrExp = !(disposalDistrRateExp.isEmpty());
			boolean isDdsExp = !(disposalDistrShapeExp.isEmpty()); 
			boolean isSfExp = !(serviceFreqExp.isEmpty());
			
			int expNo = 1;
			
			if (isDdrExp && isDdsExp && isSfExp) {
				for (float ddr : disposalDistrRateExp) {
					Random.setDisposalDistrRate(ddr);
					for (short dds : disposalDistrShapeExp) {
						Random.setDisposalDistrShape(dds);
						for (float sf : serviceFreqExp) {
							Simulator citySimulatorExp = new Simulator(parser);
							for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
								sa.changeServiceFreq(sf);
							}
							citySimulatorExp.start();
							String expString = "Experiment #"+expNo+": disposalDistrRate "+ddr+" disposalDistrShape "+dds+" serviceFreq "+sf;
							System.out.println(expString);
							ps.println(expString);
							expNo++;
							citySimulatorExp.statsAnalysis(ps);
						}
					}
				}
			} else if (isDdrExp && isDdsExp) {
				for (float ddr : disposalDistrRateExp) {
					Random.setDisposalDistrRate(ddr);
					for (short dds : disposalDistrShapeExp) {
						Random.setDisposalDistrShape(dds);
						Simulator citySimulatorExp = new Simulator(parser);
						citySimulatorExp.start();
						String expString = "Experiment #"+expNo+": disposalDistrRate "+ddr+" disposalDistrShape "+dds;
						System.out.println(expString);
						ps.println(expString);
						expNo++;
						citySimulatorExp.statsAnalysis(ps);
					}
				}
			} else if (isDdrExp && isSfExp) {
				for (float ddr : disposalDistrRateExp) {
					Random.setDisposalDistrRate(ddr);
					for (float sf : serviceFreqExp) {
						Simulator citySimulatorExp = new Simulator(parser);
						for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
							sa.changeServiceFreq(sf);
						}
						citySimulatorExp.start();
						String expString = "Experiment #"+expNo+": disposalDistrRate "+ddr+" serviceFreq "+sf;
						System.out.println(expString);
						ps.println(expString);
						expNo++;
						citySimulatorExp.statsAnalysis(ps);
					}
				}
			} else if (isDdrExp) {
				for (float ddr : disposalDistrRateExp) {
					Random.setDisposalDistrRate(ddr);
					Simulator citySimulatorExp = new Simulator(parser);
					citySimulatorExp.start();
					String expString = "Experiment #"+expNo+": disposalDistrRate "+ddr;
					System.out.println(expString);
					ps.println(expString);
					expNo++;
					citySimulatorExp.statsAnalysis(ps);
				}
			} else if (isDdsExp && isSfExp) {
				for (short dds : disposalDistrShapeExp) {
					Random.setDisposalDistrShape(dds);
					for (float sf : serviceFreqExp) {
						Simulator citySimulatorExp = new Simulator(parser);
						for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
							sa.changeServiceFreq(sf);
						}
						citySimulatorExp.start();
						String expString = "Experiment #"+expNo+": disposalDistrShape "+dds+" serviceFreq "+sf;
						System.out.println(expString);
						ps.println(expString);
						expNo++;
						citySimulatorExp.statsAnalysis(ps);
					}
				}
			} else if (isDdsExp) {
				for (short dds : disposalDistrShapeExp) {
					Random.setDisposalDistrShape(dds);
					Simulator citySimulatorExp = new Simulator(parser);
					citySimulatorExp.start();
					String expString = "Experiment #"+expNo+": disposalDistrShape "+dds;
					System.out.println(expString);
					ps.println(expString);
					expNo++;
					citySimulatorExp.statsAnalysis(ps);
				}
			} else if (isSfExp) {
				for (float sf : serviceFreqExp) {
					Simulator citySimulatorExp = new Simulator(parser);
					for (ServiceArea sa : citySimulatorExp.serviceAreas.values()) {
						sa.changeServiceFreq(sf);
					}
					citySimulatorExp.start();
					String expString = "Experiment #"+expNo+": serviceFreq "+sf;
					System.out.println(expString);
					ps.println(expString);
					expNo++;
					citySimulatorExp.statsAnalysis(ps);
				}
			} else {
				LOGGER.info("This is an experiment but no value. Should not reach this state.");
			}
		}
	}
}

			
			
			
			