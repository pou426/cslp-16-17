package cslp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * ServiceArea class stores information for a service area
 *
 */
public class ServiceArea extends ServiceAreaInfo {
	// at this stage, assuming there is always a path between nodes!!!!!!! 
	private static final Logger LOGGER = Logger.getLogger(ServiceArea.class.getName());

	private int serviceInterval; // in second (time interval between scheduled bin servicing events
	private Bin[] bins = null;
	private Lorry lorry; // each service area has its own lorry
	private int[][] minDistMatrix;
	
	// queue for the order of bins to be serviced....
	// but actually...... should just depart and arrive and set to collect or not???? 
	// or... keep this, but keep a separate binary array to indicate which bin to collect.
	private ArrayList<Integer> serviceQueue = new ArrayList<Integer>(); // should contains binIdx in servicing order...
	
	private BinServiceEvent binServiceEvent; // current bin service event...
	
	// summary statistics
	private ArrayList<Integer> tripDurations = new ArrayList<Integer>(); // each trip duration in second
	private ArrayList<Integer> noTrips = new ArrayList<Integer>(); // no of trips made by each bin service event
	private ArrayList<Float> tripEfficiencies = new ArrayList<Float>();
	private ArrayList<Float> volCollected = new ArrayList<Float>();
	private ArrayList<Float> overflowPercent = new ArrayList<Float>();
	
	// for checking...
	public int getServiceQueueSize() {
		return this.serviceQueue.size();
	}
	
	// for lorry exceed capacity.. need to insert this bin back into queue or else it would skip
	public void insertToQueue(int location) {
		serviceQueue.add(location);
	}
	
	public ServiceArea(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		super(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
		
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
		this.bins = new Bin[noBins];
		for (int i = 0; i < noBins; i++) {
			this.bins[i] = new Bin(this, i+1); // bin idx starts from 1
		}
		this.lorry = new Lorry(this);
		
		// using FloydWarshall method
		FloydWarshall floydWarshall = new FloydWarshall();
		this.minDistMatrix = floydWarshall.getMinDistMatrix(roadsLayout);
	}
	
	
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
		// TODO: check that totalNoOfTrips is not 0 !!! so no division by zero
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
	
	public float getAvgOverflowPercent() {
		int totalSchedules = overflowPercent.size();
		float totalOverflowPercent = 0;
		for (float o : overflowPercent) {
			totalOverflowPercent += o;
		}
		return totalOverflowPercent/totalSchedules;
	}
	
	public int[][] createRouteLayout(int[] requiredVertices) {
		int n = requiredVertices.length;
		int[][] routeLayout = new int[n][n];
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				routeLayout[row][col] = minDistMatrix[requiredVertices[row]][requiredVertices[col]];
			}
		}
		return routeLayout;
	}

	// for testing script
	public int[][] createRouteLayout(int[][] minDistMatrixTesting, int[] requiredVertices) {
		int n = requiredVertices.length;
		int[][] routeLayout = new int[n][n];
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				routeLayout[row][col] = minDistMatrixTesting[requiredVertices[row]][requiredVertices[col]];
			}
		}
		return routeLayout;
	}

	public int[] createRequiredVertices() {
		// gives an array of bin Idx in which that bin requires servicing
		int n = 0; 
		for (Bin bin : bins) {
			if (bin.isExceedThreshold()) {
				n++;
			}
		}
		if (n==0) {
			System.out.println("No bins to be serviced.");
			int[] result = {};
			return result;
		}
		int[] requiredVertices = new int[n+1]; // including depot at 0 for easy referencing
		requiredVertices[0] = 0; // depot
		int v = 1; // idx pointer for requiredVertices
		for (Bin bin : bins) {
			if (bin.isExceedThreshold()) {
				requiredVertices[v] = bin.getBinIdx();
				v++;
			}
		}
		return requiredVertices;
	}
	
	public void computePath() {
		if (serviceQueue.isEmpty()) {
			// This is a new bin service event
			
			// for sub graph
			// requiredVertices include depot at index 0 and all bins that requires servicing
			int[] requiredVertices = createRequiredVertices(); // for keeping track of the bin Idx in the route Layout matrix?
			
			if (requiredVertices.length==0) {
				return;
			}
			int[][] routeLayout = createRouteLayout(requiredVertices);
			// for checking
			System.out.print("requiredVertices: ");
			for (int v : requiredVertices) {
				System.out.print(v+" ");
			}
			System.out.println();

			// using knn method
			serviceQueue = new NearestNeighbour().getServiceQueue(routeLayout, requiredVertices);

		} else { // have bins left over. needs rescheduling.
			int len = serviceQueue.size()+1;
			int[] requiredVertices = new int[len];
			requiredVertices[0] = 0;
			for (int i = 1; i < len; i++) {
				requiredVertices[i] = serviceQueue.get(i-1);
			}
			// checking... 
			if (serviceQueue.size() != 0) {
				LOGGER.severe("Should not reach this state.");
			}
			serviceQueue.clear(); // to be safe... 
			
			Arrays.sort(requiredVertices);
			int[][] routeLayout = createRouteLayout(requiredVertices);
			
			serviceQueue = new NearestNeighbour().getServiceQueue(routeLayout, requiredVertices);
		}
	}
	
	public boolean isDone() {
		return this.serviceQueue.isEmpty();
	}
	
	public int getTimeBetweenLocations(int departLocation, int arriveLocation) {
		//return getRoadsLayout()[departLocation][arriveLocation];
		return minDistMatrix[departLocation][arriveLocation];
	}
	
	public void changeServiceFreq(float serviceFreq) {
		setServiceFreq(serviceFreq);
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
	}
	
	public float computeOverflowPercent() {
		int noBins = getNoBins();
		int noOverflow = 0;
		for (Bin bin : bins) {
			if (bin.isOverflow()) {
				noOverflow += 1;
			}
		}
		// TODO: check noBins != 0 or else divison by zero
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
		int result = this.serviceQueue.get(0);
		this.serviceQueue.remove(0);
		return result;
	}
	public void setBinServiceEvent(BinServiceEvent binServiceEvent) {
		this.binServiceEvent = binServiceEvent;
	}
	public BinServiceEvent getBinServiceEvent() {
		return this.binServiceEvent;
	}
}
