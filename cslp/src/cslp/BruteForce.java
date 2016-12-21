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