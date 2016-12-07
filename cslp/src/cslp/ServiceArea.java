package cslp;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * ServiceArea class stores information for a service area
 *
 */
public class ServiceArea extends ServiceAreaInfo {
	// at this stage, assuming there is always a path between nodes!!!!!!! 
	
	private int serviceInterval; // in second (time interval between scheduled bin servicing events
	private Bin[] bins = null;
	private Lorry lorry; // each service area has its own lorry
	
	// queue for the order of bins to be serviced....
	// but actually...... should just depart and arrive and set to collect or not???? 
	// or... keep this, but keep a separate binary array to indicate which bin to collect.
	private PriorityQueue<Integer> serviceQueue = new PriorityQueue<Integer>(); // should contains binIdx in servicing order...
	private BinServiceEvent binServiceEvent; // current bin service event...
	
	// for stats
	private ArrayList<Integer> tripDurations = new ArrayList<Integer>(); // each trip duration in second
	private ArrayList<Integer> noTrips = new ArrayList<Integer>(); // no of trips made by each bin service event
	private ArrayList<Float> tripEfficiencies = new ArrayList<Float>();
	private ArrayList<Float> volCollected = new ArrayList<Float>();
	private ArrayList<Float> overflowPercent = new ArrayList<Float>();
	
	public void addNoTrip(int noTrip) {
		this.noTrips.add(noTrip);
	}
	public void addTripDuration(int tripDuration) {
		this.tripDurations.add(tripDuration);
	}
	public void addTripEfficiency(float tripEfficiency) {
		this.tripEfficiencies.add(tripEfficiency);
	}
	public void addVolCollected(float collectedVol) {
		this.volCollected.add(collectedVol);
	}
	public void addOverflowPercent(float percentOverflow) {
		this.overflowPercent.add(percentOverflow);
	}
	public float getAvgTripDuration() { // in second
		int totalNoOfTrips = 0;
		for (int n : noTrips) {
			totalNoOfTrips += n;
		}
		int totalTripDurations = 0;
		for (int t : tripDurations) {
			totalTripDurations += t;
		}
		return totalTripDurations/totalNoOfTrips;
	}
	public float getAvgNoTripsPerSchedule() {
		int noOfSchedules = noTrips.size();
		int totalNoOfTrips = 0;
		for (int n : noTrips) {
			totalNoOfTrips += n;
		}
		return totalNoOfTrips/noOfSchedules;
	}
	public float getTripEfficiency() {
		int totalNoOfTrips = 0;
		for (int n : noTrips) {
			totalNoOfTrips += n;
		}
		float totalTripEfficiency = 0;
		for (float f : tripEfficiencies) {
			totalTripEfficiency += f;
		}
		return totalTripEfficiency/totalNoOfTrips;
	}
	public float getAvgVolCollected() {
		int totalNoOfTrips = 0;
		for (int n : noTrips) {
			totalNoOfTrips += n;
		}
		float totalVolCollected = 0;
		for (float v : volCollected) {
			totalVolCollected += v;
		}
		return totalVolCollected/totalNoOfTrips;
	}
	public float getAvgPercentageOverflow() {
		int noOfSchedules = noTrips.size();
		float totalPercentageOverflow = 0;
		for (float p : overflowPercent) {
			totalPercentageOverflow += p;
		}
		return totalPercentageOverflow/noOfSchedules;
	}
	
	public void printAll() {
		System.out.println("Trip Durations:");
		for (int t : tripDurations) {
			System.out.println(t);
		}
		System.out.println("No of Trips per schedule:");
		for (int t : noTrips) {
			System.out.println(t);
		}
		System.out.println("Trip efficiencies:");
		for (float t : tripEfficiencies) {
			System.out.println(t);
		}
		System.out.println("Volume collected:");
		for (float v : volCollected) {
			System.out.println(v);
		}
		System.out.println("percentage of overflow:");
		for (float o : overflowPercent) {
			System.out.println(o);
		}
	}
	
	
	
	// for checking...
	public int getPQsize() {
		return this.serviceQueue.size();
	}
	
	public ServiceArea(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		super(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
		
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
		this.bins = new Bin[noBins];
		for (int i = 0; i < noBins; i++) {
			this.bins[i] = new Bin(this, i+1);
		}
		this.lorry = new Lorry(this);
	}
	
	// change this to a sequence of lorry depart and arrival events
	// each time it execute, inserts new one to priority queue if condition meets?
	public void computePath() {
		// every time u compute path, clear out the previous path. for reshceduling
		if (serviceQueue.isEmpty()) {
			// This is a new bin service event
			// COMPUTE PATH NAIVE WAY
			for (Bin bin : bins) {
				if (bin.isExceedThreshold() || bin.isOverflow()) {
					serviceQueue.add(bin.getBinIdx());
				}
			}
			// COMPUTE PATH OPTION 1
			// get all overflow bins
			// compute path to collect all overflow bins
			// get all threshold bins
			// compute path to collect all threshold bins
			
			// COMPUTE PATH OPTION 2
			// get all overflow and threshold bins
			// compute shortest paths
		} else {
			// retain what is left ( or use newly overflowed bins?)
			// compute paths again?
			// serviceQueue.clear();
			// compute here... 
		}
	}
	
	public boolean isDone() {
		return this.serviceQueue.isEmpty();
	}
	
	public int getTimeBetweenLocations(int departLocation, int arriveLocation) {
		return getRoadsLayout()[departLocation][arriveLocation];
	}
	
	public void changeServiceFreq(float serviceFreq) {
		setServiceFreq(serviceFreq);
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
	}
	
	public float getOverflowPercent() {
		int noBins = getNoBins();
		int noOverflow = 0;
		for (Bin bin : bins) {
			if (bin.isOverflow()) {
				noOverflow += 1;
			}
		}
		float ratio = noOverflow/noBins;
		float percent = ratio * 100;
		return percent;
	}
	
	public Lorry getLorry() {
		return this.lorry;
	}
	public Bin[] getBins() {
		return this.bins;
	}
	public Bin getBin(int binIdx) {
		return this.bins[binIdx-1];
	}
	public int getServiceInterval() {
		return this.serviceInterval;
	}	
	public int getNextBinInQueue() {
		return this.serviceQueue.poll();
	}
	public void setBinServiceEvent(BinServiceEvent binServiceEvent) {
		this.binServiceEvent = binServiceEvent;
	}
	public BinServiceEvent getBinServiceEvent() {
		return this.binServiceEvent;
	}
}
