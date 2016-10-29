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
	
	public void assignLorry(Lorry lorry) {
		lorry = lorry;
	}
	
	public void createBins() {
		for (int i = 0; i < noBins; i++) {
			bins[i] = new Bin(areaIdx, i);
		}
	}
	
	public void print() {
		String s = "areaIdx= "+String.valueOf(areaIdx)+" serviceFreq= "+String.valueOf(serviceFreq)+
				" thresholdVal= "+String.valueOf(thresholdVal)+" noBins= "+String.valueOf(noBins);
		System.out.println(s);
		System.out.println("roadsLayout");
		System.out.println(Arrays.deepToString(roadsLayout));
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
}
