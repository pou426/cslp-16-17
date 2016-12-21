package cslp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;


public class BruteForce {

	public static final Logger LOGGER = Logger.getLogger(BruteForce.class.getName());

	private int minDuration;
	private int[] shortestPath; // shortest path does not include depots at the beginning and end 
	
	/**
	 * Copy and modify code to generate all possible permutation for a given array
	 * Source code information:
	 * Title: Permute_All_List_Numbers source code
	 * Author: Sanfoundry
	 * Availability: http://www.sanfoundry.com/java-program-generate-all-possible-combinations-given-list-numbers/
	 * 
	 * @param a		array to be permutated
	 * @param k		always initialised as 0
	 * @param routeLayout	sub matrix of roadsLayout
	 * 
	 */
	public void permute(int[] a, int k, int[][] routeLayout) {
		if (k == a.length) {
			int duration = 0;
			int[] temp = new int[a.length];
			
			int idx1 = 0;
			int idx2;
			
			for (int i = 0; i < a.length; i++) {
				temp[i] = a[i];
				idx2 = a[i];
				duration += routeLayout[idx1][idx2];
				idx1 = idx2;
			}
			idx2 = 0;
			duration += routeLayout[idx1][idx2];
			
			if (duration < this.minDuration) {
				this.minDuration = duration;
				this.shortestPath = temp;
			}
			
		} else {
			for (int i = k; i < a.length; i++) {
				int temp = a[k];
				a[k] = a[i];
				a[i] = temp;
				
				permute(a, k+1, routeLayout);
				
				temp = a[k];
				a[k] = a[i];
				a[i] = temp;
			}
		}
	}
	
	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
		
		int len = routeLayout.length - 1;
		int[] indices = new int[len];
		for (int i = 0; i < len; i++) {
			indices[i] = i+1;
		}
		
		this.minDuration = Integer.MAX_VALUE;
		this.shortestPath = new int[len];
		
		permute(indices, 0, routeLayout);

		ArrayList<Integer> serviceQueue = new ArrayList<>();
		String routeString = "Route: 0 -> ";
		String serviceQueueString = "serviceQueue: ";
		for (int s : shortestPath) {
			int binIdx = requiredVertices[s];
			System.out.println(s);
			serviceQueue.add(binIdx);
			routeString+=Integer.toString(s)+" -> ";
			serviceQueueString += Integer.toString(binIdx)+" -> ";
		}
		routeString+="END";
		serviceQueueString += "END";
		LOGGER.info(routeString+" \t Total duration = "+minDuration);
		LOGGER.info(serviceQueueString);
		
		return serviceQueue;
		
	}
	
}

//
//package cslp;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.logging.Logger;
//
//public class BruteForce {
//
//	public static final Logger LOGGER = Logger.getLogger(BruteForce.class.getName());
//
//	private int minDuration;
//	private int[] shortestPath; // shortest path does not include depots at the beginning and end 
//	
//	public BruteForce() {
//		this.minDuration = Integer.MAX_VALUE;
//	}
//	
//	/**
//	 * This method assumes that there is always a path between two locations
//	 * @param routeLayout	sub matrix of roadsLayout with only relevant locations (bin only)
//	 * @return int[]		an aaray of route for best performance
//	 */
//	public int[] getRoute(int[][] routeLayout) {
//		
//		// routeLayout contains from 0 to n for n number of bins
//		// an array of indices for bins ONLY. i.e. 1, 2, ... , n
//		int len = routeLayout.length - 1;
//		int[] indices = new int[len];
//		for (int i = 0; i < len; i++) {
//			indices[i] = i+1;
//		}
//		
//		this.shortestPath = new int[len];
//		this.minDuration = Integer.MAX_VALUE;
//		permute(indices, 0, routeLayout);
//		
//		updateShortestPath();
//		
//		String routeString = "Route: ";
//		for (int s : shortestPath) {
//			routeString+=Integer.toString(s)+" -> ";
//		}
//		routeString+="END";
//		LOGGER.info(routeString+" \t Total duration = "+minDuration);
//		
//		return this.shortestPath;
//	}
//	
//	// updates attribute to include depot at beginning and end
//	public void updateShortestPath() {
//		int len = this.shortestPath.length;
//		int copyLen = len + 2;
//		int[] copy = new int[copyLen];
//		copy[0] = 0;
//		copy[copyLen-1] = 0;
//		for (int i = 1; i < copyLen-1; i++) {
//			copy[i] = this.shortestPath[i-1];
//		}
//		this.shortestPath = copy;
//	}
//
//	/**
//	 * Copy and modify code to generate all possible permutation for a given array
//	 * Source code information:
//	 * Title: Permute_All_List_Numbers source code
//	 * Author: Sanfoundry
//	 * Availability: http://www.sanfoundry.com/java-program-generate-all-possible-combinations-given-list-numbers/
//	 * 
//	 * @param a		array to be permutated
//	 * @param k		always initialised as 0
//	 * @param routeLayout	sub matrix of roadsLayout
//	 * 
//	 */
//	public void permute(int[] a, int k, int[][] routeLayout) {
//		if (k == a.length) {
//			System.out.println("current minDuration = "+this.minDuration+" path = "+Arrays.toString(this.shortestPath));
//			System.out.println("new permutation = "+Arrays.toString(a));
//			
//			int duration = 0;
//			
//			int idx1 = 0;
//			int idx2;
//			
//			for (int i = 0; i < a.length; i++) {
//				idx2 = a[i];
//				duration += routeLayout[idx1][idx2];
//				idx1 = idx2;
//			}
//			idx2 = 0;
//			duration += routeLayout[idx1][idx2];
//			System.out.println("new duration = "+duration);
//			if (duration < this.minDuration) {
//				this.minDuration = duration;
//				for(int aa = 0; aa < a.length; aa++) {
//					this.shortestPath[aa] = a[aa];
//				}
////				this.shortestPath = a;
//				System.out.println("yes, update. current minDuration = "+this.minDuration+" path = "+Arrays.toString(this.shortestPath));
//			} else {
//				System.out.println("no dont update. current minDuration = "+this.minDuration+" path = "+Arrays.toString(this.shortestPath));
//			}
//
//		} else {
//			for (int i = k; i < a.length; i++) {
//				int temp = a[k];
//				a[k] = a[i];
//				a[i] = temp;
//				
//				permute(a, k+1, routeLayout);
//				
//				temp = a[k];
//				a[k] = a[i];
//				a[i] = temp;
//			}
//		}
//	}
//	
//	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
//		
//		getRoute(routeLayout); // this updates the attribute
//		
//		ArrayList<Integer> serviceQueue = new ArrayList<>();
//		String serviceQueueString = "serviceQueue: ";
//		for (int i = 1; i < this.shortestPath.length-1; i++) {
//			int subGraphLocation = this.shortestPath[i];
//			int location = requiredVertices[subGraphLocation];
//			serviceQueue.add(location);
//			serviceQueueString += Integer.toString(location)+" -> ";
//		}
//		serviceQueueString += "END";
//		LOGGER.info(serviceQueueString);
//		return serviceQueue;
//		
//	}
//	
//}