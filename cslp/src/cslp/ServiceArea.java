package cslp;

public class ServiceArea {
	public int areaIdx; // unit8_t
	// no. of trips per hour 
	public double serviceFreq;
	// a value between 0 and 1
	// occupancy threshold triggers waste collection
	public double thresholdVal;
	// Number of bins in this area
	public int noBins; // unit16_t
	// matrix representation of the duration in mins between two locations in an area
	// (0,0) corresponds to the depot
	// while elements (i,j) are zero when i=j
	// use -1 to indicate there is no direct link between two locations
	// we use directed graphs i.e. there may only be a one way link between two vertices
	public int[][] roadsLayout; // int8_t
	
	public ServiceArea(int areaIdx, double serviceFreq,
			double thresholdVal, int noBins, int[][] roadsLayout) {
		this.areaIdx = areaIdx;
		this.serviceFreq = serviceFreq;
		this.thresholdVal = thresholdVal;
		this.noBins = noBins;
		this.roadsLayout = roadsLayout;
	}
	
}
