package cslp;

import java.util.Arrays;

public class ServiceArea {
	
	private short areaIdx; 
	private float serviceFreq;
	private float thresholdVal;
	private int noBins;
	private short[][] roadsLayout = null; 
	private Bin[] bins = new Bin[100]; // change to smaller number or arraylist
	private Lorry lorry = null;
	
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

	public String toString() {
		String a = "areaIdx = "+String.valueOf(areaIdx)+" serviceFreq = "+String.valueOf(serviceFreq)
				+" thresholdVal = "+String.valueOf(thresholdVal)+" noBins = "+String.valueOf(noBins);
		String b = "\nroadsLayout = "+Arrays.deepToString(roadsLayout);
		String result = a+b;
		return result;
	}
	
}
