package cslp;

import java.util.Arrays;

public class ServiceArea {
	private short areaIdx; // unit8_t
	private float serviceFreq;
	private float thresholdVal;
	private int noBins; // unit16_t
	private short[][] roadsLayout; // int8_t
	private Bin[] bins = new Bin[65535];
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
		this.lorry = lorry;
	}
	
	public void createBins(Bin bin) {
		for (int i = 0; i < noBins; i++) {
			bins[i] = bin;
		}
	}
	
	public void print() {
		String s = "areaIdx= "+String.valueOf(areaIdx)+" serviceFreq= "+String.valueOf(serviceFreq)+
				" thresholdVal= "+String.valueOf(thresholdVal)+" noBins= "+String.valueOf(noBins);
		System.out.println(s);
		System.out.println("roadsLayout");
		System.out.println(Arrays.deepToString(roadsLayout));
	}
	
	/*
	public Bin[] getBins() {
		return bins;
	}*/
	
}
