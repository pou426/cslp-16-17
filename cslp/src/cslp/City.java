package cslp;

import java.util.ArrayList;
import java.util.PriorityQueue;

// receive events?

public class City {
	
		protected static short noAreas;
		protected static ArrayList<ServiceArea> serviceAreas = new ArrayList<ServiceArea>();
		protected static PriorityQueue<Event> pq = new PriorityQueue<Event>();
		
		// keep track of time
		protected int currTime;
		protected static float stopTime; // short or int or float?
		protected static float warmUpTime; // short or int or float?

		public City() {
			this.currTime = 0;
		}
		
		public void setTime(int delay) {
			this.currTime += delay;
		}
		
		public int getTime() {
			return currTime;
		}
}
