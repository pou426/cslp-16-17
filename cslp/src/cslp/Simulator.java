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

	private static short lorryVolume;
	private static int lorryMaxLoad;
	private static int binServiceTime;
	private static float binVolume;
	private static float disposalDistrRate; // expressed as avg. no. of disposal events per hour
	private static short disposalDistrShape;
	private static float bagVolume;
	private static float bagWeightMin;
	private static float bagWeightMax;
	private static short noAreas;
	private static HashMap<Short,ServiceArea> serviceAreas = new HashMap<Short,ServiceArea>();	// roadsLayout elements in seconds, serviceFreq in hour
	private static float stopTime; // in second
	private static float warmUpTime; // in second
	
	private static boolean isExperiment = false;
	private static ArrayList<Float> disposalDistrRateExp = new ArrayList<Float>();
	private static ArrayList<Short> disposalDistrShapeExp = new ArrayList<Short>();
	private static ArrayList<Float> serviceFreqExp = new ArrayList<Float>();
		
	/**
	 * Set all static variables in Simulator class from parsed input file
	 * @param parser
	 */
	public static void setParameters(Parser parser) {
		lorryVolume = parser.getLorryVolume();
		lorryMaxLoad = parser.getLorryMaxLoad();
		binServiceTime = parser.getBinServiceTime();
		binVolume = parser.getBinVolume();
		disposalDistrRate = parser.getDisposalDistrRate();
		disposalDistrShape = parser.getDisposalDistrShape();
		bagVolume = parser.getBagVolume();
		bagWeightMin = parser.getBagWeightMin();
		bagWeightMax = parser.getBagWeightMax();
		noAreas = parser.getNoAreas();
		serviceAreas = parser.getServiceAreas();
		stopTime = parser.getStopTime();
		warmUpTime = parser.getWarmUpTime();
		
		isExperiment = parser.isExperiment();
		disposalDistrRateExp = parser.getDisposalDistrRateExp();
		disposalDistrShapeExp = parser.getDisposalDistrShapeExp();
		serviceFreqExp = parser.getServiceFreqExp();
	}
	
	// Simulator implementation
	private PriorityQueue<AbstractEvent> events = new PriorityQueue<AbstractEvent>(); // for storing upcoming events
	private int time;	// current simulation time
	
	public void insert(AbstractEvent e) {	// insert event into queue
		this.events.add(e);
	}
	public int now() {
		return time;
	}

	/**
	 * extracts all events from 'events' priority queue and execute them
	 */
	public void doAllEvents() {	
		AbstractEvent e;
        while ((e = (AbstractEvent) events.poll()) != null) {
            time = e.getTime();
            e.execute(this);
        }
    }	
	
	/**
	 * Starts simulator
	 * If this is an experiment, several simulations will be run with different parameters.
	 */
	public void start() {
		if (isExperiment) {
			// at the moment, only one set of parameters will be run because no implementation for experiments yet
			if (disposalDistrRateExp.size() > 0)	disposalDistrRate = disposalDistrRateExp.get(0);
			if (disposalDistrShapeExp.size() > 0) 	disposalDistrShape = disposalDistrShapeExp.get(0);

			// reset attributes for experiments
			Random.setDisposalDistrRate(disposalDistrRate);
			Random.setDisposalDistrShape(disposalDistrShape);
		}
		for (ServiceArea sa : serviceAreas.values()) {	// generate an initial disposal event for each bin
			for (Bin bin : sa.getBins()) {
				DisposalEvent disposalEventGenerator = new DisposalEvent(Random.erlangk(), bin);
				this.events.add(disposalEventGenerator);
			}
		}
		doAllEvents();		// execute all events from priority queue
	}
	
	/**
	 * Last stage of the simulation is statistical analysis
	 */
	public void statsAnalysis() {
		// insert code here
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String filepath = args[0];
		
		Parser parser = new Parser();
		parser.runParser(filepath);	
		
		setParameters(parser);	// set relevant static variables in all classes
		
		Simulator citySimulator = new Simulator();
		citySimulator.start();
		citySimulator.statsAnalysis();
		// should maybe implement this into two parts for experiments and non experiment
		
    } 	
}