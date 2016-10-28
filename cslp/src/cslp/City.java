package cslp;

import java.util.ArrayList;
import java.util.PriorityQueue;

// receive events?

public class City {
	
	// area specifications
	private static short noAreas;
	private static ArrayList<ServiceArea> serviceAreas = new ArrayList<ServiceArea>();
	
	private static PriorityQueue<Event> pqueue = new PriorityQueue<Event>();
	
	// keeping track of time
	private static int currTime;
	private static float stopTime; // short or int or float?
	private static float warmUpTime; // short or int or float?
	
	public static void setCurrTime(int delay) {
		City.currTime += delay;
	}
	
	// assign lorry to each service area
	public static void initialiseCity() {
		for (ServiceArea sa : serviceAreas) {
			sa.assignLorry(new Lorry());
			sa.createBins();
		}
	}
	public static PriorityQueue<Event> getPQueue() {
		return pqueue;
	}
	public static void setNoAreas(short noAreas) {
		City.noAreas = noAreas;
	}
	public static void setServiceAreas(ArrayList<ServiceArea> serviceAreas) {
		City.serviceAreas = serviceAreas;
	}
	public static void setStopTime(float stopTime) {
		City.stopTime = stopTime;
	}
	public static void setWarmUpTime(float warmUpTime) {
		City.warmUpTime = warmUpTime;
	}
	public static int getTime() {
		return currTime;
	}
	public static float getStopTime() {
		return stopTime;
	}
	public static float getWarmUpTime() {
		return warmUpTime;
	}

}
