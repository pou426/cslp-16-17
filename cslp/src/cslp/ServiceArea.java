package cslp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServiceArea class stores information for a service area
 *
 */
public class ServiceArea extends ServiceAreaInfo {

	public static final Logger LOGGER = Logger.getLogger(ServiceArea.class.getName());

	private int serviceInterval; // in second (time interval between scheduled bin servicing events
	private Bin[] bins = null;
	private Lorry lorry; // each service area has its own lorry
	
	private int[][] minDistMatrix; // shortest path between all locations 
	private ArrayList<Integer> serviceQueue = new ArrayList<Integer>();
	private BinServiceEvent binServiceEvent; // current bin service event
	
	// STATISTICS
	private ArrayList<Integer> noTrips = new ArrayList<Integer>(); // no of trips made by each bin service event
	private ArrayList<Integer> tripDurations = new ArrayList<Integer>(); // each trip duration in second
	private ArrayList<Float> tripEfficiencies = new ArrayList<Float>();
	private ArrayList<Float> volCollected = new ArrayList<Float>();
	private ArrayList<Float> overflowPercent = new ArrayList<Float>();
	
	public int getServiceQueueSize() {
		return this.serviceQueue.size();
	}
	
	public void insertToQueue(int location) {
		serviceQueue.add(location);
	}
	
	public ServiceArea(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		super(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
		this.bins = new Bin[noBins];
		for (int i = 0; i < noBins; i++) {
			this.bins[i] = new Bin(this, i+1); // bin index starts from 1
		}
		this.lorry = new Lorry(this);
		
		// using FloydWarshall method
		FloydWarshall floydWarshall = new FloydWarshall();
		this.minDistMatrix = floydWarshall.getMinDistMatrix(roadsLayout);
		
		String minDistStr = floydWarshall.matrixToString(minDistMatrix);
		LOGGER.info("\tareaIdx = "+getAreaIdx()+" \n"+minDistStr);
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
	
	private int getTotalNoOfTrips() {
		int totalNoOfTrips = 0;
		for (int n : noTrips) {
			totalNoOfTrips += n;
		}
		return totalNoOfTrips;
	}
	
	/**
	 * @return float	average trip duration in second
	 */
	public float getAvgTripDuration() { // in second
		int totalNoOfTrips = getTotalNoOfTrips();
		if (totalNoOfTrips == 0) {
			return 0;
		}
		int totalTripDurations = 0;
		for (int t : tripDurations) {
			totalTripDurations += t;
		}
		return totalTripDurations/totalNoOfTrips;
	}
	
	public float getAvgNoTripsPerSchedule() {
		int noOfSchedules = overflowPercent.size();
		if (noOfSchedules == 0)	{
			return 0;
		}
 		int totalNoOfTrips = getTotalNoOfTrips();
		return totalNoOfTrips/noOfSchedules;
	}
	
	public float getTripEfficiency() {
		int totalNoOfTrips = getTotalNoOfTrips();
		if (totalNoOfTrips == 0) {
			return 0;
		}
		float totalTripEfficiency = 0;
		for (float f : tripEfficiencies) {
			totalTripEfficiency += f;
		}
		return totalTripEfficiency/totalNoOfTrips;
	}
	
	public float getAvgVolCollected() {
		int totalNoOfTrips = getTotalNoOfTrips();
		if (totalNoOfTrips == 0) {
			return 0;
		}
		float totalVolCollected = 0;
		for (float v : volCollected) {
			totalVolCollected += v;
		}
		return totalVolCollected/totalNoOfTrips;
	}
	
	public float getAvgPercentageOverflow() {
		int noOfSchedules = overflowPercent.size();
		if (noOfSchedules == 0) {
			return 0;
		}
		float totalPercentageOverflow = 0;
		for (float p : overflowPercent) {
			totalPercentageOverflow += p;
		}
		return totalPercentageOverflow/noOfSchedules;
	}
	
	/**
	 * Creates a sub matrix with information only from the bins that requires servicing
	 * 
	 * @param requiredVertices		an array of bin locations and depot
	 * @return int[][]				a sub matrix with relevant information
	 */
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

	/**
	 * FOR TESTING SCRIPT: /test/ServiceAreaTest.java
	 * 
	 * @param minDistMatrixTesting
	 * @param requiredVertices
	 */
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

	/**
	 * If a bin has exceeded its threshold value, it should be serviced
	 * 
	 * @return int[] 	an array with depot location and binIdx that requires servicing
	 */
	public int[] createRequiredVertices() {
		int n = 0; 
		for (Bin bin : bins) {
			if (bin.isExceedThreshold()) {
				n++;
			}
		}
		if (n == 0) {
			int[] requiredVertices = {};
			return requiredVertices;
		}
		int[] requiredVertices = new int[n+1]; // includes depot at 0 for easy referencing
		requiredVertices[0] = 0; // depot
		String requiredVerticesString = "required vertices: 0\t";
		int v = 1; // pointer
		for (Bin bin : bins) {
			if (bin.isExceedThreshold()) {
				requiredVertices[v] = bin.getBinIdx();
				requiredVerticesString += Integer.toString(requiredVertices[v])+"\t";
				v++;
			}
		}
		LOGGER.info(requiredVerticesString);
		return requiredVertices;
	}
	
	/**
	 * Computes path with Nearest Neighbour method.
	 * A new complete scheduling occurs if the service queue is empty.
	 * A rescheduling occurs if the service queue still contains bin index.
	 */
	public void computePath() {
		int bfThreshold = 10; // threshold of the number of bins to determine which method to use.

		if (isDone()) { // new servicing event
			int[] requiredVertices = createRequiredVertices();
			
			if (requiredVertices.length==0) {
				return;
			}
			int[][] routeLayout = createRouteLayout(requiredVertices);
			
			if (routeLayout.length <= bfThreshold) {
				// using BruteForce method
				serviceQueue = new BruteForce().getServiceQueue(routeLayout, requiredVertices);
			} else {
				// using NN method
				serviceQueue = new NearestNeighbour().getServiceQueue(routeLayout, requiredVertices);
			}
			return;
			
		} else { // rescheduling event
			int len = serviceQueue.size()+1;
			int[] requiredVertices = new int[len];
			requiredVertices[0] = 0;
			for (int i = 1; i < len; i++) {
				requiredVertices[i] = serviceQueue.get(i-1);
			}
			
			Arrays.sort(requiredVertices);
			int[][] routeLayout = createRouteLayout(requiredVertices);
			
			if (routeLayout.length <= bfThreshold) {
				// using BruteForce method
				serviceQueue = new BruteForce().getServiceQueue(routeLayout, requiredVertices);
			} else {
				// using NN method
				serviceQueue = new NearestNeighbour().getServiceQueue(routeLayout, requiredVertices);
			}
			return;
		}
	}
	
	/**
	 * Checks whether there is still bin left to be serviced
	 * 
	 * @return boolean		True if service queue is empty
	 */
	public boolean isDone() {
		return this.serviceQueue.isEmpty();
	}
	
	/**
	 * Gives the shortest path duration from one location to another. Direct or indirect path.
	 * 
	 * @param departLocation	location lorry departs from 
	 * @param arriveLocation	location lorry arrives at
	 * @return duration in seconds
	 */
	public int getTimeBetweenLocations(int departLocation, int arriveLocation) {
		return minDistMatrix[departLocation][arriveLocation];
	}
	
	/**
	 * Updates service frequency and service interval attribute 
	 * 
	 * @param serviceFreq
	 */
	public void changeServiceFreq(float serviceFreq) {
		setServiceFreq(serviceFreq);
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
	}
	
	/**
	 * Gets the percentage of overflowed bins in this service area
	 * @return float	percentage overflowed
	 */
	public float computeOverflowPercent() {
		int noBins = getNoBins();
		if (noBins == 0) {
			return 0;
		}
		int noOverflow = 0;
		for (Bin bin : bins) {
			if (bin.isOverflow()) {
				noOverflow += 1;
			}
		}
		return ((noOverflow/noBins)*100);
	}
	
	/**
	 * Gets the next bin in the path (service queue)
	 * @return int		Bin index
	 */
	public int getNextBinInQueue() {
		int result = this.serviceQueue.get(0);
		this.serviceQueue.remove(0);
		return result;
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
	public void setBinServiceEvent(BinServiceEvent binServiceEvent) {
		this.binServiceEvent = binServiceEvent;
	}
	public BinServiceEvent getBinServiceEvent() {
		return this.binServiceEvent;
	}
}
