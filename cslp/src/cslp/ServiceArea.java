package cslp;

import java.util.Arrays;

public class ServiceArea {
	public short areaIdx; // unit8_t
	public float serviceFreq;
	public float thresholdVal;
	public int noBins; // unit16_t
	public short[][] roadsLayout; // int8_t
	
	public ServiceArea(short areaIdx, float serviceFreq,
			float thresholdVal, int noBins, short[][] roadsLayout) {
		this.areaIdx = areaIdx;
		this.serviceFreq = serviceFreq;
		this.thresholdVal = thresholdVal;
		this.noBins = noBins;
		this.roadsLayout = roadsLayout;
	}
	
	public void print() {
		String s = "areaIdx= "+String.valueOf(areaIdx)+" serviceFreq= "+String.valueOf(serviceFreq)+
				" thresholdVal= "+String.valueOf(thresholdVal)+" noBins= "+String.valueOf(noBins);
		System.out.println(s);
		System.out.println("roadsLayout");
		System.out.println(Arrays.deepToString(roadsLayout));
	}
	
}
