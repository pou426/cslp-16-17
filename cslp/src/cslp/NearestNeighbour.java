package cslp;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Applying this method on a matrix after running the shortest path algorithm
 * would always work given that the graph provided is reasonable.
 * 
 */
public class NearestNeighbour {
	
	private static final Logger LOGGER = Logger.getLogger(NearestNeighbour.class.getName());

	/**
	 * This method returns a route from the routeLayout matrix with 0's at beginning and end
	 * 
	 * @param routeLayout	a sub matrix of the original roadsLayout matrix, containing only bins that require servicing
	 * @return int[]		an array of servicing route in order
	 */
	public int[] getRoute(int[][] routeLayout) {
		
		int routeLen = routeLayout.length+1;
		int[] route = new int[routeLen];
		int len = routeLayout.length;
		boolean[] visited = new boolean[len];
		for (boolean b : visited) {
			b = false;
		}
		visited[0] = true;
		
		int totalDuration = 0;
		
		boolean keepGoing = true;
		
		int currLocation = 0; // always start at depot
		int idx = 0;

		route[idx] = currLocation;
		idx++;
		
		int minDist = Integer.MAX_VALUE;
		int nn = -1; // nearest neighbour
		
		while (keepGoing) {
			for (int i = 0; i < len; i++) {
				if ((i!=currLocation) && (i!=0) && !visited[i]) {
					if (routeLayout[currLocation][i] < minDist) {
						minDist = routeLayout[currLocation][i];
						nn = i;
					}
				}
			}
			if (nn == -1) { 
				nn = 0;
				route[idx] = nn;
				keepGoing = false;
				totalDuration += routeLayout[currLocation][0];
			} else {
				route[idx] = nn;
				currLocation = nn;
				visited[nn] = true;
				totalDuration += minDist;
			}
			minDist = Integer.MAX_VALUE;
			nn = -1;
			idx++;
		}
		
		String routeString = "Route: ";
		for (int r : route) {
			routeString+=Integer.toString(r)+" -> ";
		}
		routeString+="END";
		LOGGER.info(routeString+"\tTotal duration = "+totalDuration);
		return route; // index in the subgraph form
	}
	
	/**
	 * Creates an Arraylist of service queue in terms of actual binIdx from routeLayout 
	 * and the array of binIdx
	 * 
	 * @param routeLayout				sub graph of roadsLayout
	 * @param requiredVertices			array of required vertices with depot and bin locations as elements
	 * @return	ArrayList<Integer> 		service queue with route for graph traversal
	 */
	public ArrayList<Integer> getServiceQueue(int[][] routeLayout, int[] requiredVertices) {
		int[] route = getRoute(routeLayout);
		
		ArrayList<Integer> serviceQueue = new ArrayList<>();
		String serviceQueueString = "serviceQueue: ";
		for (int i = 1; i < route.length-1; i++) {
			int subGraphLocation = route[i];
			int location = requiredVertices[subGraphLocation];
			serviceQueue.add(location);
			serviceQueueString += Integer.toString(location)+" -> ";
		}
		serviceQueueString+="END";
		LOGGER.info(serviceQueueString);
		return serviceQueue;
	}
}
