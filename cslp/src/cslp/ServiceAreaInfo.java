package cslp;

public class ServiceAreaInfo {

	private short areaIdx;
	private float serviceFreq;
	private float thresholdVal;
	private int noBins;
	private int[][] roadsLayout = null;
	
	public ServiceAreaInfo(short areaIdx, float serviceFreq, float thresholdVal, 
			int noBins, int[][] roadsLayout) {
		this.areaIdx = areaIdx;
		this.serviceFreq = serviceFreq;
		this.thresholdVal = thresholdVal;
		this.noBins = noBins;
		this.roadsLayout = roadsLayout;
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

	public short getAreaIdx() {
		return this.areaIdx;
	}
	
	public float getServiceFreq() {
		return this.serviceFreq;
	}
	
	public float getThresholdVal() {
		return this.thresholdVal;
	}
	
	public int getNoBins() {
		return this.noBins;
	}
	
	public int[][] getRoadsLayout() {
		return this.roadsLayout;
	}
	
}
