package cslp;

import java.util.Arrays;

/**
 * ServiceArea class stores information for a service area
 *
 */
public class ServiceArea {
	
	private short areaIdx; 
	private float serviceFreq; // eg. serviceFreq 0.0625 means 0.0625 trips per hour
	private float thresholdVal;
	private int noBins;
	private int[][] roadsLayout = null; 
	private Bin[] bins = null;
	private Lorry lorry; // each service area has its own lorry
	
	public ServiceArea(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		this.areaIdx = areaIdx;
		this.serviceFreq = serviceFreq;
		this.thresholdVal = thresholdVal;
		this.noBins = noBins;
		this.roadsLayout = roadsLayout;
		Bin[] bins = new Bin[noBins];
		this.bins = bins;
		for (int i = 0; i < noBins; i++) {		// create bins
			this.bins[i] = new Bin(areaIdx, i+1, thresholdVal);
		}
		this.lorry = null;
	}
	
	public void setLorry(Lorry lorry) {
		this.lorry = lorry;
	}
	
	/**
	 * return a list of bins in this service area
	 * 
	 * @return Bin[]	an array of all bins in that service area
	 */
	public Bin[] getBins() {
		return bins;
	}
	public short getAreaIdx() {
		return areaIdx;
	}
	public float getServiceFreq() {
		return this.serviceFreq;
	}
	public float getThresholdVal() {
		return this.thresholdVal;
	}

	/** 
	 * print service area information for checking
	 */
	public String toString() {
		String a = "areaIdx = "+String.valueOf(areaIdx)+" serviceFreq (in hour) = "+String.valueOf(serviceFreq)
				+" thresholdVal = "+String.valueOf(thresholdVal)+" noBins = "+String.valueOf(noBins);
		String b = "\nroadsLayout (in sec) = ";
		String c = "\n";
		for (int i = 0; i < roadsLayout.length; i++) {
			for (int j = 0; j < roadsLayout[0].length; j++) {
				c = c + roadsLayout[i][j] + " ";
			}
			if (!(i==roadsLayout.length-1)) c = c + "\n";
		}
		String result = a+b+c;
		return result;
	}
	
}
