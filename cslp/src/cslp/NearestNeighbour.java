package cslp;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Applying this method on a matrix after running the shortest path algorithm
 * would always work given that the graph provided is reasonable.
 * 
 */
public class NearestNeighbour {
	
	public static final Logger LOGGER = Logger.getLogger(NearestNeighbour.class.getName());

	private int minDuration; // attribute saved for convenience, such as comparing shortest path algorithms
//	private ArrayList<Integer> serviceQueue = new ArrayList<Integer>();
	
	public int getMinDuration() {
		return this.minDuration;
	}
//	public ArrayList<Integer> getShortestPath() {
//		return this.serviceQueue;
//	}
	
	/**
	 * Computes a graph traversal path with a compute matrix 'routeLayout', which is a matrix with
	 * selected bins. requiredVertices indicates the bin indices the routeLayout represents.
	 * 
	 * @param routeLayout			A shortest path matrix
	 * @param requiredVertices		Contains 0 (depot) and bin locations
	 * @return ArrayList<Integer> 	A service queue
	 */
	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
		boolean[] visited = new boolean[routeLayout.length-1];
		for (boolean b : visited) {
			b = false;
		}
		int totalDuration = 0;
		boolean found = false;
		int currLocation = 0;
		
		ArrayList<Integer> serviceQueue = new ArrayList<Integer>();
		
		int min = Integer.MAX_VALUE;
		int nn = -1; // neighbour

		String routeString = "Route: 0 -> ";
		String serviceQueueString = "serviceQueue: ";

		while (!found) {
			for (int i = 1; i < routeLayout.length; i++) {
				if ((i != currLocation) && (!visited[i-1]) && (routeLayout[currLocation][i] < min)) {
					min = routeLayout[currLocation][i];
					nn = i;
				}
			}
			if (nn == -1) {
				nn = 0;
				found = true;
				totalDuration += routeLayout[currLocation][0];
				routeString+="END";
				serviceQueueString+="END";
			} else {
				int binIdx = requiredVertices[nn];
				serviceQueue.add(binIdx);
				currLocation = nn;
				visited[nn-1] = true;
				totalDuration += min;
				routeString += Integer.toString(nn)+" -> ";
				serviceQueueString += Integer.toString(binIdx)+" -> ";
			}
			min = Integer.MAX_VALUE;
			nn = -1;
		}
		LOGGER.info(routeString+" \t Total duration = "+totalDuration);
		LOGGER.info(serviceQueueString);
		
		this.minDuration = totalDuration;
		return serviceQueue;
	}
}
