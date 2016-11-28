package cslp;

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
	private PriorityQueue<Integer> serviceQueue = new PriorityQueue<Integer>(); // should contains bin Idx in servicing order...
	private BinServiceEvent binServiceEvent; // current bin service event...
	
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
		serviceQueue.clear();
		// naive way : insert order of binIdx into prioriyt queue...
		// DOES NOT CONTAIN INITIAL LOCATION AND FINISHING LOCATION!!!!!!!!!!!
		// compute the path smartly please. i hate stupid people.
		System.out.println("LOGGING INFO: ----------------- COMPUTING PATH -- for service area : "+getAreaIdx()+" -----------------");
		for (int i = 0; i < this.bins.length; i++) {
			serviceQueue.add(bins[i].getBinIdx());
		}
	}
	
	public boolean isDone() {
		return this.serviceQueue.isEmpty();
	}
	
	public int getTimeBetweenLocations(int departLocation, int arriveLocation) {
		return getRoadsLayout()[departLocation][arriveLocation];
	}
	
	public Lorry getLorry() {
		return this.lorry;
	}
	public Bin[] getBins() {
		return this.bins;
	}
	public int getServiceInterval() {
		return this.serviceInterval;
	}	
	public int getNextBinInQueue() {
		return this.serviceQueue.poll();
	}
}
