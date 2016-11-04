package cslp;

import java.util.Arrays;

/**
 * ServiceArea class stores information for a service area
 * @author home
 *
 */
public class ServiceArea {
	
	private short areaIdx; 
	private float serviceFreq;
	private float thresholdVal;
	private int noBins;
	private short[][] roadsLayout = null; 
	private Bin[] bins = new Bin[250]; // in reality the number of bins should be < 250
	private Lorry lorry = null; // each service area has its own lorry
	
	public ServiceArea(short areaIdx, float serviceFreq,
			float thresholdVal, int noBins, short[][] roadsLayout) {
		this.areaIdx = areaIdx;
		this.serviceFreq = serviceFreq;
		this.thresholdVal = thresholdVal;
		this.noBins = noBins;
		this.roadsLayout = roadsLayout;
	}
	
	public void setLorry(Lorry lorry) {
		this.lorry = lorry;
	}
	public void setBins() {
		for (int i = 0; i < noBins; i++) {
			this.bins[i] = new Bin(areaIdx, i+1, thresholdVal);
		}
	}
	public Bin[] getBins() {
		Bin[] availableBins = new Bin[noBins];
		for (int i = 0; i < noBins; i++) {
			availableBins[i] = bins[i];
		}
		return availableBins;
	}
	public short getAreaIdx() {
		return areaIdx;
	}
	public float getServiceFreq() {
		return this.serviceFreq;
	}

	// print service area information for checking
	public String toString() {
		String a = "areaIdx = "+String.valueOf(areaIdx)+" serviceFreq = "+String.valueOf(serviceFreq)
				+" thresholdVal = "+String.valueOf(thresholdVal)+" noBins = "+String.valueOf(noBins);
		String b = "\nroadsLayout = "+Arrays.deepToString(roadsLayout);
		String result = a+b;
		return result;
	}
	
}
