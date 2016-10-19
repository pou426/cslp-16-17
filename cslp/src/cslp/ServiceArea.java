package cslp;

import java.util.Arrays;

public class ServiceArea {
	private static short areaIdx; // unit8_t
	private static float serviceFreq;
	private static float thresholdVal;
	private static int noBins; // unit16_t
	private static short[][] roadsLayout; // int8_t
	private static Bin[] bins = new Bin[65535];
	private static Lorry lorry;
	
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
