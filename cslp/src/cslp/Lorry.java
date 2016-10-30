package cslp;

public class Lorry {
	
	// put a boolean so that the static parameters can only be set once?
	protected static short lorryVolume; 
	protected static int lorryMaxLoad; 
	protected static int binServiceTime;
	
	protected float currentTrashVolume;
	
	public Lorry() {
		this.currentTrashVolume = 0;
	}
	
	public void emptyBin() {
		this.currentTrashVolume += Bin.getBinVolume();
	}
	
	// getters and setters
	public static short getLorryVolume() {
		return lorryVolume;
	}
	public static int getLorryMaxLoad() {
		return lorryMaxLoad;
	}	
	public static int getBinServiceTime() {
		return binServiceTime;
	}
	public static void setLorryVolume(short lorryVolume) {
		Lorry.lorryVolume = lorryVolume;
	}	
	public static void setLorryMaxLoad(int lorryMaxLoad) {
		Lorry.lorryMaxLoad = lorryMaxLoad;
	}
	public static void setBinServiceTime(int binServiceTime) {
		Lorry.binServiceTime = binServiceTime;
	}
	
	
}
