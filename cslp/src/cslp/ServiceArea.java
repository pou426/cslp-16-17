package cslp;

/**
 * ServiceArea class stores information for a service area
 *
 */
public class ServiceArea extends ServiceAreaInfo {
	
	private int serviceInterval; // in second (time interval between scheduled bin servicing events
	private Bin[] bins = null;
	private Lorry lorry; // each service area has its own lorry
	
	public ServiceArea(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		
		super(areaIdx, serviceFreq, thresholdVal, noBins, roadsLayout);
		
		this.serviceInterval = Math.round((1/serviceFreq)*60*60); // in second
		Bin[] bins = new Bin[noBins];
		for (int i = 0; i < noBins; i++) {
			bins[i] = new Bin(this, i+1);
		}
		this.bins = bins;
		this.lorry = new Lorry(this);
	}
	
	public void computePath() {
		// compute best path here 
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
}
